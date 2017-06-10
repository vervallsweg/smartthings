/**
 *  LGTV (UDAP 2.0) service manager
 *
 *  Copyright 2017 Tobias Haerke
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "LGTV (UDAP 2.0) service manager",
    namespace: "vervallsweg",
    author: "Tobias Haerke",
    description: "Connect your NetCast 3.0, NetCast 4.0 (LGTV 2012-2013) and any other UDAP 2.0 compatible SmartTV to Smartthings.",
    category: "SmartThings Labs",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    page(name: "mainPage")
	page(name: "deviceDiscovery")
    page(name: "devicePairing")
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
    unsubscribe()
	unschedule()

	ssdpSubscribe()

	if (selectedDevices) {
		addDevices()
	}

	//runEvery5Minutes("ssdpDiscover")
}

def mainPage() {
    state.searchTarget = "udap:rootservice"
    return dynamicPage(name: "deviceDiscovery", title: "LG TV service manager", nextPage: "", uninstall: true) {
        section("Discover devices"){
           href "deviceDiscovery", title: "Discover devices", description:""
        }
        section("Installed devices"){
          	def dMap = [:]
            getChildDevices().sort({ a, b -> a["label"] <=> b["label"] }).each {
            	paragraph "$it.label"
            }
        }
        /*section("Search Target") {
            input "searchTarget", "string", title: "Search Target", defaultValue: "udap:rootservice", required: true
        }*/
 	}
}

def deviceDiscovery() {
	def options = [:]
	def devices = getVerifiedDevices()
	devices.each {
		def value = it.value.name ?: "UPnP Device ${it.value.ssdpUSN.split(':')[1][-3..-1]}"
		def key = it.value.mac
		options["${key}"] = value
	}

	ssdpSubscribe()

	ssdpDiscover()
	verifyDevices()

	return dynamicPage(name: "deviceDiscovery", title: "Discovery Started!", refreshInterval: 5, nextPage: "devicePairing") {
		section("Please wait while we discover your UPnP Device. Discovery can take five minutes or more, so sit back and relax! Select your device below once discovered.") {
			input "selectedDevices", "enum", required: false, title: "Select Devices (${options.size() ?: 0} found)", multiple: true, options: options
		}
	}
}

def getVerifiedDevices() {
	getDevices().findAll{ it.value.verified == true }
}

def getDevices() {
	if (!state.devices) {
		state.devices = [:]
	}
	state.devices
}

void ssdpSubscribe() {
	subscribe(location, "ssdpTerm.${state.searchTarget}", ssdpHandler)
}

def ssdpHandler(evt) {
	def description = evt.description
	def hub = evt?.hubId
    
	def parsedEvent = parseLanMessage(description)
    log.warn "ssdpHandler called, parsedEvent: " + parsedEvent
    
	parsedEvent << ["hub":hub]

	def devices = getDevices()
	String ssdpUSN = parsedEvent.ssdpUSN.toString()
    log.warn "ssdpHandler, ssdpUSN: " + ssdpUSN
    
	if (devices."${ssdpUSN}") {
		def d = devices."${ssdpUSN}"
        log.debug "ssdpHandler, device in devices array: " + ssdpUSN
		if (d.networkAddress != parsedEvent.networkAddress || d.deviceAddress != parsedEvent.deviceAddress || d.ssdpPath != parsedEvent.ssdpPath) {
        	log.debug "ssdpHandler updating d.networkAddress from: " + d.networkAddress + " to: parsedEvent.networkAddress: " + parsedEvent.networkAddress + " AND/OR d.deviceAddress from: " + d.deviceAddress + " to: " + parsedEvent.deviceAddress + " AND/OR d.ssdpPath from: " + d.ssdpPath + " to: " + parsedEvent.ssdpPath
			
            d.networkAddress = parsedEvent.networkAddress
			d.deviceAddress = parsedEvent.deviceAddress
            d.ssdpPath = parsedEvent.ssdpPath
            
			def child = getChildDevice(parsedEvent.mac)
			if (child) {
            	log.debug "ssdpHandler syncing: " + child
				child.sync(parsedEvent.networkAddress, parsedEvent.deviceAddress)
			}
		}
	} else {
    	log.debug "ssdpHandler, device doesnot exist: " + ssdpUSN
		devices << ["${ssdpUSN}": parsedEvent]
	}
}

void ssdpDiscover() {
	sendHubCommand(new physicalgraph.device.HubAction("lan discovery ${state.searchTarget}", physicalgraph.device.Protocol.LAN))
}

void verifyDevices() {
	def devices = getDevices().findAll { it?.value?.verified != true }
	devices.each {
		int port = convertHexToInt(it.value.deviceAddress)
		String ip = convertHexToIP(it.value.networkAddress)
        String udapRootService = it.value.ssdpPath
		String host = "${ip}:${port}"
        String path = "${udapRootService}"
        log.info "verifyDevices host: " + host + " path: " + path
        
		sendHubCommand(new physicalgraph.device.HubAction("""GET ${path} HTTP/1.1\r\nHOST: $host\r\nUser-Agent: UDAP/2.0 Samsung Smartthings \r\n\r\n""", physicalgraph.device.Protocol.LAN, host, [callback: deviceDescriptionHandler]))
    }
}

void deviceDescriptionHandler(physicalgraph.device.HubResponse hubResponse) {
    log.debug "deviceDescriptionHandler, status: " + hubResponse.status
    
	def body = hubResponse.body
    def xml = parseXml(body)
    log.debug "deviceDescriptionHandler, xml: " + xml
    
    def modelName = ""+xml.device.modelName
    def friendlyName = ""+xml.device.friendlyName
    def uuid = ""+xml.device.uuid
    
    def ssdpUSN = "uuid:"+uuid+"::udap:rootservice"

    log.error "modelName: " + modelName + ", friendlyName: " + friendlyName + ", uuid: " + uuid + ", ssdpUSN: " + ssdpUSN + ", pairingKey: " + null +", paired: " + false
	
    def devices = getDevices()
	def device = devices.find { it?.key?.contains( ssdpUSN ) }
	if (device) {
    	log.debug "Verifying device: " + ssdpUSN
		device.value << [name: friendlyName, model: modelName, serialNumber: uuid, pairingKey:null, paired:false, verified: true]
	}
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

def addDevices() {
	def devices = getDevices()

	log.debug "devices: " + devices
    log.debug "selectedDevices: " + selectedDevices
    
	selectedDevices.each { dni ->
		def selectedDevice = devices.find { it.value.mac == dni }
		def d
        
		if (selectedDevice) {
			d = getChildDevices()?.find {
				it.deviceNetworkId == selectedDevice.value.mac
			}
		}

		if (!d) {
        	log.warn "device " + dni + " doesnot exist, selectedDevice: " + selectedDevice
            
            if(settings["pairingKey"+selectedDevice.value.serialNumber]) {
            	log.warn "Pairing key exists " + settings["pairingKey"+selectedDevice.value.serialNumber]
            	if(selectedDevice.value.paired == true) {
                	log.warn "Device paired "
                    log.debug "Creating LGTV UDAP 2.0 Device with name: " + selectedDevice.value.name + ", mac: " + selectedDevice.value.mac + ", ip: " + selectedDevice.value.networkAddress + ", port: " + selectedDevice.value.deviceAddress + ", netcruUrl: " + selectedDevice.value.ssdpPath + ", uuid: " + selectedDevice.value.serialNumber + ", pairingKey: " + settings["pairingKey"+selectedDevice.value.serialNumber]
        	
                    addChildDevice("vervallsweg", "LGTV UDAP 2.0", selectedDevice.value.mac, selectedDevice?.value.hub, [
                        "label": selectedDevice?.value?.name ?: "LGTV UDAP 2.0",
                        "data": [
                            "mac": selectedDevice.value.mac,
                            "ip": selectedDevice.value.networkAddress,
                            "port": selectedDevice.value.deviceAddress,
                            "netcruUrl": selectedDevice.value.ssdpPath,
                            "uuid": selectedDevice.value.serialNumber,
                            "model": selectedDevice.value.model,
                            "pairingKey": settings["pairingKey"+selectedDevice.value.serialNumber]
                        ]
                    ])
                } else {
                	log.warn "Device not paired"
                    pairDevice(dni, settings["pairingKey"+selectedDevice.value.serialNumber])
                }
            }
		} else {
        	log.warn "device " + dni + " exists"
        }
	}
}

def devicePairing() {
	def devices = getDevices()
    def pairingKeyMap = [:]

	log.debug "devicePairing, devices: " + devices
    log.debug "devicePairing, selectedDevices: " + selectedDevices
    
    return dynamicPage(name: "devicePairing", title: "Enter pairing key", nextPage: "", install: true, uninstall: true) {
		section("A pairing key will be displayed on every newly added device. Go to your TVs now and enter every key below. Note: The keys will expire after some time. If you don't finish installation now you maybe need to restart the process.") {
			selectedDevices.each { dni ->
            	def selectedDevice = devices.find { it.value.mac == dni }
                if(!selectedDevice.value.pairingKey) {
                	int port = convertHexToInt(selectedDevice.value.deviceAddress)
                    String ip = convertHexToIP(selectedDevice.value.networkAddress)
                    String host = "${ip}:${port}"
                	requestPairingKey( host ) 
               	}
                
                input "pairingKey"+selectedDevice.value.serialNumber, "number", required: true, title: selectedDevice.value.model+" ("+selectedDevice.value.name+")", defaultValue: selectedDevice.value.pairingKey
            }
		}
	}
}

def requestPairingKey(host) {
	log.error "requestPairingKey, host: " + host + "/udap/api/pairing"
    def hubComm = new physicalgraph.device.HubAction("""POST /udap/api/pairing HTTP/1.1\r\nHOST: $host\r\nUser-Agent: UDAP/2.0 Samsung Smartthings\r\nContent-Type: text/xml; charset=utf-8\r\nCache-Control: no-cache\r\nContent-Length: 105\r\nConnection: Close\r\n\r\n<?xml version="1.0" encoding="utf-8"?><envelope><api type="pairing"><name>showKey</name></api></envelope>""", physicalgraph.device.Protocol.LAN, host, [callback: requestPairingKeyHandler])
    sendHubCommand(hubComm)
    log.warn "hubComm: " + hubComm
}

def requestPairingKeyHandler(physicalgraph.device.HubResponse hubResponse) {
	log.debug 'requestPairingKeyHandler, hubResponse.status: ' + hubResponse.status
}

def pairDevice(dni, pairingKey) {
	log.error "pairDevice, dni: " + dni + ", pairingKey: " + pairingKey
    
    def selectedDevice = devices.find { it.value.mac == dni }
    int port = convertHexToInt(selectedDevice.value.deviceAddress)
    String ip = convertHexToIP(selectedDevice.value.networkAddress)
    String host = "${ip}:${port}"
    selectedDevice.value.pairingKey = pairingKey
    log.warn "selectedDevice: " + selectedDevice
    
    def hubComm = new physicalgraph.device.HubAction("""POST /udap/api/pairing HTTP/1.1\r\nHOST: $host\r\nUser-Agent: UDAP/2.0 Samsung Smartthings\r\nContent-Type: text/xml; charset=utf-8\r\nCache-Control: no-cache\r\nContent-Length: 141\r\nConnection: Close\r\n\r\n<?xml version="1.0" encoding="utf-8"?><envelope><api type="pairing"><name>hello</name><value>${pairingKey}</value><port>8080</port></api></envelope>""", physicalgraph.device.Protocol.LAN, host, [callback: pairDeviceHandler])
    sendHubCommand(hubComm)
    log.warn "hubComm: " + hubComm
}

def pairDeviceHandler(physicalgraph.device.HubResponse hubResponse) {
    log.debug 'pairDeviceHandler, hubResponse.mac: ' + hubResponse.mac + ', hubResponse.status: ' + hubResponse.status
    def selectedDevice = devices.find { it.value.mac == hubResponse.mac }
    
    //RESPONSE,REQ MATCHING > currDevi.paired==true
    if (selectedDevice) {
    	log.debug "Device found, selectedDevice: " + selectedDevice
        
        if(hubResponse.status==200){
            log.info "Successfully paired device, selectedDevice.value.deviceAddress: " + selectedDevice.value.deviceAddress
            selectedDevice.value.paired=true
        } else {
            log.error "Could not pair device, selectedDevice.value.deviceAddress: " + selectedDevice.value.deviceAddress + ", hubResponse.status: " + hubResponse.status
        }
    } else {
    	log.warn "Pairing response received from non ST device, hubResponse.mac: " + hubResponse.mac
    }
    
}