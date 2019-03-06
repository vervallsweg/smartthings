/**
 *  cast-web-api
 *
 *  Copyright 2018 Tobias Haerke
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
 preferences {
    input("configLoglevel", "enum", title: "Log level?",
        required: false, multiple:false, value: "nothing", options: ["0","1","2","3","4"])
    input("syncNamesFromAPI", "bool", title: "Don't sync cast-device names from API?", required: false)
}
 
metadata {
    definition (name: "cast-web-api", namespace: "vervallsweg", author: "Tobias Haerke") {
        capability "Actuator"
        capability "Audio Notification"
        capability "Bridge"
        capability "Refresh"
        capability "Speech Synthesis"
        
        command "checkAssistant"
        command "checkVersion"
        command "setApiHost", ["string"]
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        standardTile("mainTile", "device", width: 6, height: 2, decoration: "flat") {
            state "val", icon:"https://raw.githubusercontent.com/vervallsweg/smartthings/master/icn/ic-cast-web-api-gray-1200.png", label:'Active', action:null, defaultState: true
        }
        standardTile("refresh", "device.refresh", width: 2, height: 2, decoration: "flat") {
            state "val", icon:"st.secondary.refresh-icon", label:'All devices', action:refresh, defaultState: true
        }
        valueTile("updateStatus", "device.updateStatus", width: 4, height: 2) {
            state "val", label:'${currentValue}', defaultState: true, action: "checkVersion"
        }
        valueTile("assistantStatus", "device.assistantStatus", width: 6, height: 2) {
            state "val", label:'${currentValue}', defaultState: true, action: "checkAssistant"
        }
        //main "mainTile"
        details(["mainTile", "refresh", "updateStatus", "assistantStatus"])
    }
}

// parse events into attributes
def parse(String description) {
    //er('debug', "Parsing: "+description)
    def message = parseLanMessage(description)
    def children = getChildDevices()
    logger('debug', "Parsing json: "+message.json + ", status"+message.status)
    
    if(message.json) {
        if(message.json.id) {
            def foundTarget = false;
            children.each { child ->
                if ( child.deviceNetworkId == message.json.id ){
                    foundTarget = true;
                    logger('debug', "Parsing found target name: "+child.displayName+", dni: "+child.deviceNetworkId)
                    if(message.json.name) {
                        if(settings.syncNamesFromAPI != true) {
                            child.displayName = message.json.name
                        }
                    }
                    //if(message.json.connection) { //WTF: cannot be set by dev?!
                    //    if( message.json.connection.equals("connected") ) {
                    //      child.status = "ONLINE"
                    //    }
                    //    if( message.json.connection.equals("disconnected") ) {
                    //      child.status = "OFFLINE"
                    //    }
                    //}
                    child.parse(description)
                }
            }
            if(!foundTarget) {
                logger('error', "Parsing found no target for: "+message.json.id)
            }
        }
        if(message.json.api) {
            if(message.json.api.version) {
                if (message.json.api.version.this && message.json.api.version.latest) {
                    sendEvent(name: "updateStatus", value: ("Current: "+ message.json.api.version.this + "\nLatest: " + message.json.api.version.latest), displayed: false)
                }
            }
        }
        if(message.json.containsKey("assistant") && message.json.containsKey("ready")) {
            logger('debug', "containsKey")
            sendEvent(name: "assistantStatus", value: ("Assistant: "+ message.json.assistant + "\nready: " + message.json.ready + "\nIf you never used Google Assistant with cast-web go to service manager > Setup Google Assistant"), displayed: false)   
        }
    }
}

def installed() {
    installDevices( parseListFromString( getDataValue("devices") ) )
    sendEvent(name: "updateStatus", value: "Click to check for updates", displayed: false)
}

def updated() {
    installDevices( parseListFromString( getDataValue("devices") ) )
}

def refresh() {
    unschedule(refreshAll)
    refreshAll()
    runEvery5Minutes(refreshAll)
}

def refreshAll() {
    def hub = location.hubs[0];
    //sendHttpRequest("GET", getDataValue("apiHost"), "/device", '');
    sendHttpRequest("POST", getDataValue("apiHost"), "/callback", '[{"url":"'+hub.localIP+':'+hub.localSrvPortTCP+'", "settings":""}]');
    getChildDevices().each{child -> child.refresh()}
}

def installDevices(ids) {
    def devicesToCreate = []
    def children = getChildDevices()
    logger('debug', "installDevices() children: " + children.size() + ", ids: " + ids)
    
    ids.each { id ->
        
        def exists = false
        
        children.each { child ->
            if( id.equals( child.deviceNetworkId ) ) { //id has a nbsp added to the front
                exists = true
            }
        }
        
        if (exists) {
            logger('debug', "installDevices() id: "+id+", exists!")
        } else {
            logger('warn', "installDevices() id: "+id+", doesn't exist")
            devicesToCreate.add(id)
            logger('debug', "installDevices() devicesToCreate: "+devicesToCreate)
        }
    }
    
    createDevices(devicesToCreate)
}

def createDevices(ids) {
    logger('debug', "createDevices() ids: " + ids)
    
    ids.each {
        if(it!=null&&it!="") {
            logger('debug', "createDevices() adding id: " + it)
            addChildDevice("vervallsweg", "cast-web-device", it, location.hubs[0].id, [
                label: "cast-web-device",
                data: [
                    "apiHost": getDataValue("apiHost")
                ]
            ])
        }
    }
}

def parseListFromString(string) {
    string = string.replace("[", "").replace("]", "").replace(" ", "")
    def list = string.split(',').collect{it as String}
    logger('debug', 'parseListFromString(), string: '+string+', list: ' + list)
    return list
}

def checkVersion() {
    sendHttpRequest("GET", getDataValue("apiHost"), "/config", "");
    //sendHttpTest(getDataValue("apiHost"), "/config")
}

def checkAssistant() {
    sendHttpRequest("GET", getDataValue("apiHost"), "/assistant", "");
}

def speak(phrase) {
    logger('info', "speak(), phrase: " + phrase)
    sendHttpRequest("POST", getDataValue("apiHost"), "/assistant/broadcast/", '{"message":"'+phrase+'"}');
}

def playText(message, level = 0, resume = false) {
    logger('info', "playText, message: " + message + " level: " + level)
    return speak(message)
}

def playTextAndResume(message, level = 0, thirdValue = 0) {
    logger('info', "playTextAndResume, message: " + message + " level: " + level)
    return speak(message)
}

def playTextAndRestore(message, level = 0, thirdValue = 0) {
    logger('info', "playTextAndRestore, message: " + message + " level: " + level)
    return speak(message)
}

def setApiHost(apiHost) {
    log.warn "apiHost: "+apiHost
    logger('info', 'setApiHost(), to: '+apiHost)
    updateDataValue("apiHost", apiHost)
    
    def children = getChildDevices()
    children.each { child ->
        child.updateDataValue("apiHost", apiHost)
    }
}

def sendHttpRequest(String method, String host, String path, def body="") {
    logger('debug', "Executing 'sendHttpRequest' method: "+method+" host: "+host+" path: "+path+" body: "+body)
    def bodyHead = "";
    if(!body.equals("")) {
        bodyHead = "Content-Type: application/json\r\nContent-Length:${body.length()+1}\r\n";
        body = " "+body;
    }
    sendHubCommand(new physicalgraph.device.HubAction("""${method} ${path} HTTP/1.1\r\nHOST: $host\r\n${bodyHead}\r\n${body}""", physicalgraph.device.Protocol.LAN, host))
}

//DEBUGGING
def logger(level, message) {
    def logLevel=1
    if(settings.configLoglevel) {
        logLevel = settings.configLoglevel.toInteger() ?: 0
    }
    if(level=="error"&&logLevel>0) {
        log.error message
    }
    if(level=="warn"&&logLevel>1) {
        log.warn message
    }
    if(level=="info"&&logLevel>2) {
        log.info message
    }
    if(level=="debug"&&logLevel>3) {
        log.debug message
    }
}