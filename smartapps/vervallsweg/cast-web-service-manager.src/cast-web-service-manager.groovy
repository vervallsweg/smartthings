/**
 *  Cast web - service manager
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
    name: "Cast web - service manager",
    namespace: "vervallsweg",
    author: "Tobias Haerke",
    description: "Connect your Cast devices through the Cast web API to SmartThings.",
    category: "SmartThings Labs",
    iconUrl: "https://github.com/vervallsweg/smartthings/raw/master/icn/ic_cast_grey_24dp.png",
    iconX2Url: "https://github.com/vervallsweg/smartthings/raw/master/icn/ic_cast_grey_24dp.png",
    iconX3Url: "https://github.com/vervallsweg/smartthings/raw/master/icn/ic_cast_grey_24dp.png") {
    appSetting "api"
}


preferences {
    page(name: "mainPage")
    page(name: "checkApiConnectionPage")
    page(name: "discoveryPage")
    page(name: "addDevicesPage")
    page(name: "healthCheckPage")
    page(name: "manualHealthCheck")
    page(name: "configureDevicePage")
    page(name: "saveDeviceConfigurationPage")
    page(name: "updateApiHostPage")
    page(name: "updateServiceManagerPage")
}

def mainPage() {
    restartHealthCheck()
    dynamicPage(name: "mainPage", title: "Manage your Cast devices", nextPage: null, uninstall: true, install: true) {
        section("Configure web API"){
            input "apiHostAddress", "string", title: "API host address", required: true
            href "updateServiceManagerPage", title: "Check for API updates", description:""
            href "checkApiConnectionPage", title: "Test API connection", description:""
            href "updateApiHostPage", title: "Update API host address", description:""
        }
        section("Configure Cast devices"){
            input(name: "settingsLogLevel", type: "enum", title: "Service manager log level", options: [0, 1, 2, 3, 4])
            href "discoveryPage", title:"Discover Devices", description:""//, params: [pbutton: i]
            href "healthCheckPage", title: "Health check", description:""
            href(name: "presetGenerator",title: "Preset generator",required: false,style: "external",url: "https://vervallsweg.github.io/smartthings/cast-web-preset-generator/preset-generator.html",description: "")
        }
        section("Installed Devices"){
            def dMap = [:]
            getChildDevices().sort({ a, b -> a["label"] <=> b["label"] }).each {
                href "configureDevicePage", title:"$it.label", description:"", params: [dni: it.deviceNetworkId]
            }
        }
    }
}

def checkApiConnectionPage() {
    dynamicPage(name:"checkApiConnectionPage", title:"Test API connection", nextPage: "mainPage", refreshInterval:10) {
        getDevices() //TODO: get root and only check status
        logger('debug', "checkApiConnectionPage(), refresh")
        
        section("Please wait for the API to answer, this might take a couple of seconds.") {
            if(state.latestHttpResponse) {
                if(state.latestHttpResponse==200) {
                    paragraph "Connected \nOK: 200"
                } else {
                    paragraph "Connection error \nHTTP response code: " + state.latestHttpResponse
                }
            }
        }
    }
}

def discoveryPage() {
    dynamicPage(name:"discoveryPage", title:"Discovery Started!", nextPage: "addDevicesPage", refreshInterval:10) {
        getDevices()
        logger('debug', "discoveryPage(), refresh")
        
        section("Please wait while we discover your Cast devices. Discovery can take five minutes or more, so sit back and relax! Select your device below once discovered.") {
            if(state.latestDeviceMap!=null && state.latestDeviceMap.size()>0) {
                input "selectedDevices", "enum", required:false, title:"Select Cast Device ("+ state.latestDeviceMap.size() +" found)", multiple:true, options: state.selectionMap
                state.selectedDevicesMap = state.latestDeviceMap
            } else {
                input "selectedDevices", "enum", required:false, title:"Select Cast Device (0 found)", multiple:true, options: [:]
                state.selectedDevicesMap = null
            }
        }
        
        state.latestDeviceMap = null
    }
}

def addDevicesPage() {
    def addedDevices = addDevices(selectedDevices)
    def nextPage = "mainPage"
    
    dynamicPage(name:"addDevicesPage", title:"Done", nextPage: nextPage) {
        section("Devices added") {
            if( !addedDevices.equals("0") ) {
                addedDevices.each{ key, value ->
                    paragraph title: value, ""+key
                }
            } else {
                paragraph "No devices added."
            }
        }
    }
}

def addDevices(selectedDevices) {
    def addedDevices = [:]
    logger('debug', "selectedDevices: "+selectedDevices)
    
    if(selectedDevices && selectedDevices!=null) {
        for(int i=0; i<selectedDevices.size(); i++) {
            logger('debug', "Selected device "+i+ ", DNI: " + state.selectedDevicesMap[selectedDevices[i]] )
            if(getChildDevices()?.find { it.deviceNetworkId == state.selectedDevicesMap[selectedDevices[i]] }) {
                logger("error", "Device exists!")
            } else {
                logger('debug', "Does not exist, creating device: " + state.selectedDevicesMap[selectedDevices[i]])

                addChildDevice("vervallsweg", "cast-web", state.selectedDevicesMap[selectedDevices[i]], location.hubs[0].id, [
                    "label": state.latestDevicesNameMap[selectedDevices[i]],
                    "data": [
                        "apiHost": apiHostAddress,
                        "deviceAddress": selectedDevices[i]
                    ]
                ])
                addedDevices.put(selectedDevices[i], state.latestDevicesNameMap[selectedDevices[i]])
            }
        }
    }
    
    if(addedDevices==[:]) {
        return "0"
    } else {
        return addedDevices
    }
}

def updateDeviceAddresses() {
    logger('debug', "updateDeviceAddresses() executed")
    
    //TODO: cleanMapOfDuplicates(map)
    
    def deviceMap = state.latestDeviceMap
    def updatedDeviceMap = [:]
    
    deviceMap.each { key, value ->
        logger('debug', "Selected device address: " + key + ", DNI: " + value)
        def d = getChildDevices()?.find { it.deviceNetworkId == value }
        //REALY bad temporary fix to deal with changing group ids
        if(!d) {
            d = getChildDevices()?.find { it.deviceNetworkId == flipCastId(value) }
            if(d) {
                logger("warn", "DNI: " + d.deviceNetworkId + " moved to: " + value)
                d.setDeviceNetworkId(value)
            }
        }
        
        if(d) {
            logger("warn", "Device (" + d + ":" + d.deviceNetworkId + ") exists, old address: " + d.getDataValue("deviceAddress") + " new address: " + key)
            updatedDeviceMap.put(d.deviceNetworkId, "from: " + d.getDataValue("deviceAddress") + " to: " + key)
            d.updateDataValue("deviceAddress", key)
        }
    }
    
    logger('debug', "updateDeviceAddresses() finished, updatedDeviceMap: " + updatedDeviceMap)
    state.latestUpdatedDevicesMap = updatedDeviceMap
    state.latestUpdatedDevicesTime = getTimeStamp()
}

def flipCastId(castId) {
    logger('debug', "flipCastId() executed, castId: " + castId + ", flipCastId, last two characters: " + castId[-2..-1])
    
    if( castId[-2..-1].equals("-1") ) {
        logger('debug', "flipCastId flipped: " + castId[0..-3])
        return castId[0..-3]
    } else {
        logger('debug', "flipCastId flipped: " + castId+"-1")
        return castId+"-1"
    }
}

def configureDevicePage(dni) {
    def d = getChildDevices()?.find { it.deviceNetworkId == dni["dni"] }
    logger('debug', "configureDevicePage() selected device d: " + d)
    state.configCurrentDevice = d.deviceNetworkId
    
    if(!d.getDataValue("logLevel")) {
        logger("warn", "logLevel not set, setting to 0")
        d.updateDataValue("logLevel", "0")
    }
    
    resetFormVar(d)
    
    dynamicPage(name: "configureDevicePage", title: "Configure "+d.deviceNetworkId, nextPage: "saveDeviceConfigurationPage") {
        section("Device settings") {
            input(name: "label", type: "text", title: "Device name", defaultValue: d.label, required: true)
            input(name: "device_type", type: "enum", title: "Device type", defaultValue: [d.getDataValue("deviceType")], options: ["audio","video"], required: true)
        }
        section("Refresh settings") {
            input(name: "poll_minutes", type: "enum", title: "Refresh every x minute", defaultValue: [d.getDataValue("pollMinutes")], options: ["1","2","5","10","20","30"], required: true)
            input(name: "poll_seconds", type: "number", title: "Refresh on every x second", defaultValue: [d.getDataValue("pollSecond")], required: true, range: "0..59")
        }
        section("Connection settings") {
            input(name: "device_address", type: "text", title: "Cast device address", defaultValue: [d.getDataValue("deviceAddress")], required: true)
            input(name: "api_host_address", type: "text", title: "Web API host address", defaultValue: [d.getDataValue("apiHost")], required: true)
        }
        section("Presets") {
            input(name: "presetObject", type: "text", title: "Preset object", defaultValue: [d.getDataValue("presetObject")], required: true)
            href(name: "presetGenerator",title: "Edit this preset in your browser",required: false,style: "external",url: "https://vervallsweg.github.io/smartthings/cast-web-preset-generator/preset-generator.html?"+d.getDataValue("presetObject"),description: "")
        }
        
        section("Logging") {
            input(name: "log_level", type: "enum", title: "Logging level", defaultValue: [d.getDataValue("logLevel")], options: ["0","1","2","3","4"], required: true)
        }
    }
}

def saveDeviceConfigurationPage() {
    def d = getChildDevices()?.find { it.deviceNetworkId == state.configCurrentDevice }
    logger('debug', "saveDeviceConfigurationPage() writing configuration for d: " + d)
    
    d.displayName = label
    d.updateDataValue("deviceType", device_type)
    d.updateDataValue("pollMinutes", ""+poll_minutes)
    d.updateDataValue("pollSecond", ""+poll_seconds)
    d.updateDataValue("deviceAddress", device_address)
    d.updateDataValue("apiHost", api_host_address)
    d.updateDataValue("presetObject", presetObject)
    d.updateDataValue("logLevel", ""+log_level)
    d.updated()
    
    dynamicPage(name: "saveDeviceConfigurationPage", title: "Configuration updated for: "+d.deviceNetworkId, nextPage: "mainPage") {
        section("Device name"){ paragraph ""+d.displayName }
        section("Device type"){ paragraph ""+d.getDataValue("deviceType") }
        section("Refresh every x minute"){ paragraph ""+d.getDataValue("pollMinutes") }
        section("Refresh on every x second"){ paragraph ""+d.getDataValue("pollSecond") }
        section("Cast device IP address"){ paragraph ""+d.getDataValue("deviceAddress") }
        section("Web API host address"){ paragraph ""+d.getDataValue("apiHost") }
        section("Presets"){ paragraph ""+d.getDataValue("presetObject") }
        section("Log level"){ paragraph ""+d.getDataValue("logLevel") }
    }
}

def resetFormVar(device) {
    if(label){ app.updateSetting("label", device.label) }
    if(device_type){ app.updateSetting("device_type", [device.getDataValue("deviceType")]) }
    if(poll_minutes){ app.updateSetting("poll_minutes", [device.getDataValue("pollMinutes")]) }
    if(poll_seconds){ app.updateSetting("poll_seconds", [device.getDataValue("pollSecond")]) }
    if(device_address){ app.updateSetting("device_address", [device.getDataValue("deviceAddress")]) }
    if(api_host_address){ app.updateSetting("api_host_address", [device.getDataValue("apiHost")]) }
    if(presetObject){ app.updateSetting("presetObject", [device.getDataValue("presetObject")]) }
    if(log_level){ app.updateSetting("log_level", [device.getDataValue("logLevel")]) }
}

def healthCheck() {
    logger('debug', "healthCheck() called")
    
    getDevices()
    runIn(10, updateDeviceAddresses)
}

def healthCheckPage() {
    def latestUpdatedDevicesMap = state.latestUpdatedDevicesMap
    def latestDevicesNameMap = state.latestDevicesNameMap
    def lastRunAt = state.latestUpdatedDevicesTime
    def title
    
    logger('debug', "healthCheckPage() state.latestUpdatedDevicesTime: "+state.latestUpdatedDevicesTime+", latestDevicesNameMap: " + latestDevicesNameMap)
    
    if(lastRunAt+1200>getTimeStamp()) {title="Health check in progress"} else {title="Health check not running"}
    
    dynamicPage(name: "healthCheckPage", title: title, nextPage: "mainPage") {
        section("Latest health check at") {
            paragraph "" + getTimeStringFromEpoch(lastRunAt)
            href "manualHealthCheck", title: "Run now", description:""
        }
        section("Result of the latest health check (every 15 minutes)") {
            if(latestUpdatedDevicesMap==null|| latestUpdatedDevicesMap==[:]) {
                paragraph "Nothing changed"
            } else {
                latestUpdatedDevicesMap.each{ key, value ->
                    paragraph title: getChildDevices()?.find { it.deviceNetworkId == key }.label, "ID: "+key+", "+value
                }
            }
        }
    }
}

def manualHealthCheck() {
    dynamicPage(name: "manualHealthCheck", title: "Restart health check", nextPage: null, install: true) {
        healthCheck()
        section("Click save for health check") {
            paragraph "Service manager will close and conduct a manual health check."
        }
    }
}

def restartHealthCheck() {
    logger('debug', "restartHealthCheck(), unschedule")
    unschedule()
    
    logger('debug', "restartHealthCheck(), runEvery15Minutes(healthCheck)")
    runEvery15Minutes(healthCheck)
}

def updateApiHostPage() {
    logger('debug', "Executing 'updateApiHostPage()'")
    
    dynamicPage(name: "updateApiHostPage", title: "Updated API host address on all devices", nextPage: "mainPage") {
        section("Result") {
            if(updateApiHost()) {
                paragraph "Success!"
            } else {
                paragraph "Error while updating API address"
            }
            
        }
    }
}

def updateApiHost() {
    logger("warn", "Executing 'updateApiHost()'")
    
    try {
        getChildDevices().each {
            logger('debug', "Updating apiHost to: "+ apiHostAddress + ", on: " + it.deviceNetworkId)
            it.updateDataValue("apiHost", ""+apiHostAddress)
        }
        return true
    } catch (Exception e) {
        logger("error", "Exception caught: " + e)
        return false
    }
    
}

def installed() {
    logger('debug', "Installed with settings: ${settings}")

    initialize()
}

def updated() {
    logger('debug', "Updated with settings: ${settings}")

    unsubscribe()
    initialize()
}

def initialize() {
    // TODO: subscribe to attributes, devices, locations, etc.
    state.latestUpdatedDevicesTime = getTimeStamp()
}

def getTimeStamp() {
    Date now = new Date(); 
    def timeStamp = (long)(now.getTime()/1000)
    logger("debug", "getTimeStamp(), timestamp generated: "+timeStamp)
    return timeStamp;
}

def getTimeStringFromEpoch(epoch) {
    long ep = (long) epoch;
    return new Date(ep*1000).toString()
}

def getDevices() {
    logger('debug', "Executing 'getDevices'")
    sendHttpRequest(apiHostAddress, '/getDevices')
}

def sendHttpRequest(String host, String path) {
    logger('debug', "Executing 'sendHttpRequest' host: "+host+" path: "+path)
    sendHubCommand(new physicalgraph.device.HubAction("""GET ${path} HTTP/1.1\r\nHOST: $host\r\n\r\n""", physicalgraph.device.Protocol.LAN, host, [callback: hubResponseReceived]))
}

void hubResponseReceived(physicalgraph.device.HubResponse hubResponse) {
    parse(hubResponse.description)
}

def parse(description) {
    logger('debug', "Parsing '${description}'")
    
    def msg, json, status
    try {
        msg = parseLanMessage(description)
        status = msg.status
        json = msg.json
    } catch (e) {
        logger("error", "Exception caught while parsing data: "+e)
        return null;
    }
  
    state.latestHttpResponse = status
    if(status==200){
        def length = 0
        logger('debug', "JSON rcvd: "+json+", JSON.size: "+json.size)
        
        def devicesMap = [:]
        def devicesNameMap = [:]
        def selectionMap = [:]
        for(int i=0; i<json.size; i++) {
            logger('debug', "index "+ i +": "+json[i]['deviceName']+", "+ json[i]['deviceAddress'])
            devicesMap.put(json[i]['deviceAddress']+":"+json[i]['devicePort'], json[i]['deviceName'])
            devicesNameMap.put(json[i]['deviceAddress']+":"+json[i]['devicePort'], json[i]['deviceFriendlyName'])
            selectionMap.put(json[i]['deviceAddress']+":"+json[i]['devicePort'], json[i]['deviceFriendlyName']+" ("+json[i]['deviceName']+")")
        }
       
        logger('debug', "devicesMap: " + devicesMap)
        logger('debug', "devicesNameMap: " + devicesNameMap)
        logger('debug', "selectionMap: " + selectionMap)
        state.latestDeviceMap = devicesMap
        state.latestDevicesNameMap = devicesNameMap
        state.selectionMap = selectionMap
    } else {
        state.latestDeviceMap = [:]
        state.latestDevicesNameMap = [:]
        state.selectionMap = [:]
    }
}

//UPDATE
def getThisVersion() {
    return 0.2
}

def getLatestVersion() {
    try {
        httpGet([uri: "https://raw.githubusercontent.com/vervallsweg/smartthings/master/smartapps/vervallsweg/cast-web-service-manager.src/version.json"]) { resp ->
            logger('debug', "getLatestVersion(), response status: ${resp.status}")
            String data = "${resp.getData()}"
            logger('debug', "getLatestVersion(), data: ${data}")
            
            if(resp.status==200 && data!=null) {
                return parseJson(data)
            } else {
                return null
            }
        }
    } catch (e) {
        logger("error", "getLatestVersion(), something went wrong: "+e)
        return null
    }
}

def checkForUpdate() {
    if(getThisVersion() != getLatestVersion().version) {
        return "Update available from: " + getThisVersion() + " to: " + getLatestVersion().version
    } else {
        logger('debug', "Up to date, " + "thisVersion: " + getThisVersion() + ", latestVersion: " + getLatestVersion().version)
        return "Up to date: " + getThisVersion()
    }
}

def updateServiceManagerPage() {
    dynamicPage(name:"updateServiceManagerPage", title:"Check for updates", nextPage: nextPage) {
        section("Checked for updates") {
            paragraph "" + checkForUpdate()
        }
        section("Latest version") {
            def latestVersion = getLatestVersion()
            paragraph "Version: " + latestVersion.version
            paragraph "Type: " + latestVersion.type
            paragraph "Release date: " + latestVersion.date
            href(name: "Changelog",title: "Changelog",required: false, url: latestVersion.changelog, description: "")
        }
    }
}

//DEBUGGING
def logger(level, message) {
    def smLogLevel = 0
    if(settingsLogLevel) {
        smLogLevel = settingsLogLevel.toInteger()
    }
    if(level=="error"&&smLogLevel>0) {
        log.error message
    }
    if(level=="warn"&&smLogLevel>1) {
        log.warn message
    }
    if(level=="info"&&smLogLevel>2) {
        log.info message
    }
    if(level=="debug"&&smLogLevel>3) {
        log.debug message
    }
}