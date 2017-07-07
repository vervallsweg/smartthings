/**
 *  LGTV UDAP 2.0
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
metadata {
    definition (name: "LGTV UDAP 2.0", namespace: "vervallsweg", author: "Tobias Haerke") {
        capability "Actuator"
        //capability "Audio Notification"
        //capability "Health Check"
        //capability "Image Capture"
        //capability "Media Controller"
        //capability "Music Player"
        capability "Polling"
        capability "Refresh"
        //capability "Samsung TV"
        //capability "Speech Synthesis"
        capability "Switch"
        capability "Switch Level"
        capability "TV"
        
        command "getLevel"
        command "renwePairing"
        command "mute"
        command "unmute"
        command "btn3D"
        command "btnInput"
        command "btnUp"
        command "btnDown"
        command "btnLeft"
        command "btnRight"
        command "btnOk"
        command "btnBack"
        command "btnPlay"
        command "btnPause"
        command "btnFf"
        command "btnRew"
        command "btnSkipB"
        command "btnSkipF"
        command "btnMyApps"
        command "btnSmartHome"
        command "btnSettings"
        command "btnRatio"
        command "btnQMenu"
        command "btnAVMode"
        command "btnFav"
        command "btnStop"
        command "nextInput"
        command "nextBrightness"
    }


    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
    
        multiAttributeTile(name:"sliderTile", type:"generic", width:6, height:4) {
            tileAttribute("device.input", key: "PRIMARY_CONTROL") {
                attributeState "EXT", label:'${name}', backgroundColor:"#00A0DC", action: "off", nextState:"off"
                attributeState "TV", label:'${name}', backgroundColor:"#00A0DC", action: "off", nextState:"off"
                attributeState "on", label:'On', backgroundColor:"#00A0DC", action: "off", nextState:"off"
                attributeState "off", label:'Off', backgroundColor:"#ffffff"
            }
            tileAttribute("device.mute", key: "SECONDARY_CONTROL") {
                attributeState "true", icon: 'st.custom.sonos.muted', action:"unmute", label:"muted", nextState: "false"
                attributeState "false", icon: 'st.custom.sonos.unmuted', action:"mute", label:"unmuted", nextState: "true"
            }
            tileAttribute("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"switch level.setLevel", defaultState: true
            }
            tileAttribute("device.channelNumber", key: "VALUE_CONTROL") {
                attributeState "VALUE_UP", action: "channelUp"
                attributeState "VALUE_DOWN", action: "channelDown"
            }
        }
        
        valueTile("power", "device.switch", width: 1, height: 1) {
            state "on", icon:'st.samsung.da.RC_ic_power', action: "off", nextState:"off"
            state "off", icon:'st.samsung.da.RC_ic_power'
        }
        
        valueTile("refresh", "refresh", width: 1, height: 1) {
            state "val", icon:'st.secondary.refresh-icon', defaultState: true, action: "poll"
        }
        
        valueTile("volumeUp", "volumeUp", width: 2, height: 2) {
            state "val", icon:'st.samsung.da.oven_ic_plus', defaultState: true, action: "volumeUp"
        }
        
        valueTile("volumeDown", "volumeDown", width: 2, height: 2) {
            state "val", icon:'st.samsung.da.oven_ic_minus', defaultState: true, action: "volumeDown"
        }
        
        valueTile("channelUp", "channelUp", width: 2, height: 2) {
            state "val", icon:'st.samsung.da.oven_ic_up', defaultState: true, action: "channelUp"
        }
        
        valueTile("channelDown", "channelDown", width: 2, height: 2) {
            state "val", icon:'st.samsung.da.oven_ic_down', defaultState: true, action: "channelDown"
        }
        
        valueTile("btn3D", "btn3D", width: 1, height: 1) {
            state "val", label:'3D', defaultState: true, action: "btn3D"
        }
        
        valueTile("toggleMute", "device.mute", width: 1, height: 1) {
            state "true", icon: 'st.custom.sonos.muted', action:"unmute", nextState: "false"
            state "false", icon: 'st.custom.sonos.unmuted', action:"mute", nextState: "true"
        }
        
        valueTile("btnInput", "btnInput", width: 1, height: 1) {
            state "val", icon:'st.Electronics.electronics6', defaultState: true, action: "btnInput"
        }
   
        valueTile("btnUp", "btnUp", width: 2, height: 2) {
            state "val", icon:'st.thermostat.thermostat-up', defaultState: true, action: "btnUp"
        }
        
        valueTile("btnDown", "btnDown", width: 2, height: 2) {
            state "val", icon:'st.thermostat.thermostat-down', defaultState: true, action: "btnDown"
        }
        
        valueTile("btnLeft", "btnLeft", width: 2, height: 2) {
            state "val", icon:'st.thermostat.thermostat-left', defaultState: true, action: "btnLeft"
        }
        
        valueTile("btnRight", "btnRight", width: 2, height: 2) {
            state "val", icon:'st.thermostat.thermostat-right', defaultState: true, action: "btnRight"
        }
        
        valueTile("btnOk", "btnOk", width: 2, height: 2) {
            state "val", label:' ', defaultState: true, action: "btnOk"
        }
        
        valueTile("btnBack", "btnBack", width: 2, height: 2) {
            state "val", label:'Back', defaultState: true, action: "btnBack"
        }
        
        valueTile("btnPlay", "btnPlay", width: 1, height: 1) {
            state "val", icon:'st.sonos.play-btn', defaultState: true, action: "btnPlay"
        }
        
        valueTile("btnPause", "btnPause", width: 1, height: 1) {
            state "val", icon:'st.sonos.pause-btn', defaultState: true, action: "btnPause"
        }
        
        valueTile("btnFf", "btnFf", width: 1, height: 1) {
            state "val", icon:'st.sonos.next-btn', defaultState: true, action: "btnFf"
        }
        
        valueTile("btnRew", "btnRew", width: 1, height: 1) {
            state "val", icon:'st.sonos.previous-btn', defaultState: true, action: "btnRew"
        }
        
        valueTile("btnSkipF", "btnSkipF", width: 1, height: 1) {
            state "val", icon:'st.sonos.next-btn', defaultState: true, action: "btnSkipF"
        }
        
        valueTile("btnSkipB", "btnSkipB", width: 1, height: 1) {
            state "val", icon:'st.sonos.previous-btn', defaultState: true, action: "btnSkipB"
        }
        
        valueTile("btnMyApps", "btnMyApps", width: 1, height: 1) {
            state "val", label:'My apps', defaultState: true, action: "btnMyApps"
        }
        
        valueTile("btnSmartHome", "btnSmartHome", width: 1, height: 1) {
            state "val", label:'Home', defaultState: true, action: "btnSmartHome"
        }
        
        valueTile("btnSettings", "btnSettings", width: 1, height: 1) {
            state "val", label:'Settings', defaultState: true, action: "btnSettings"
        }
        
        valueTile("btnRatio", "btnRatio", width: 1, height: 1) {
            state "val", label:'Ratio', defaultState: true, action: "btnRatio"
        }
        
        valueTile("btnQMenu", "btnQMenu", width: 1, height: 1) {
            state "val", label:'Q.MENU', defaultState: true, action: "btnQMenu"
        }
        
        valueTile("btnAVMode", "btnAVMode", width: 1, height: 1) {
            state "val", label:'A/V Mode', defaultState: true, action: "btnAVMode"
        }
        
        valueTile("btnFav", "btnFav", width: 1, height: 1) {
            state "val", label:'FAV', defaultState: true, action: "btnFav"
        }
        
        valueTile("btnStop", "btnStop", width: 2, height: 1) {
            state "val", icon:'st.sonos.stop-btn', defaultState: true, action: "btnStop"
        }
        
        valueTile("nextInput", "nextInput", width: 2, height: 1) {
            state "val", label:'Next input', defaultState: true, action: "nextInput"
        }
        
        valueTile("nextBrightness", "nextBrightness", width: 2, height: 1) {
            state "val", label:'Change brigthness', defaultState: true, action: "nextBrightness"
        }
        
        main "sliderTile"
        details(["sliderTile",
                "channelUp",
                "power",
                "refresh",
                "volumeUp",
                "btnInput",
                "toggleMute",
                "channelDown",
                "btnPlay",
                "btnPause",
                "volumeDown",
                "btnRew",
                "btnFf",
                "btnStop",
                "btnUp",
                "btn3D",
                "btnSettings",
                "btnSmartHome",
                "btnMyApps",
                "btnRatio",
                "btnQMenu",
                "btnLeft",
                "btnOk",
                "btnRight",
                "btnBack",
                "btnDown",
                "btnAVMode",
                "btnFav",
                "nextInput",
                "btnSkipB",
                "btnSkipF",
                "nextBrightness"])
    }
}

// parse events into attributes
// def parse(xml.data.value) ?
def parse(String description) {
    //log.debug "Parsing '${description}'"
    
    def playerStatus = createEvent(name: "status", value: "playing")
    def trackDescription = createEvent(name: "trackDescription", value: "on")
    def switchStatus = createEvent(name: "switch", value: "on")
    def power = createEvent(name: "power", value: "on")
    def input = createEvent(name: "input", value: "on")
    def msg = parseLanMessage(description)
    def status = msg.status
    def body = msg.body
    
    if(device.currentValue("input")=="TV"||device.currentValue("input")=="EXT") {
        input = createEvent(name: "input", value: device.currentValue("input"))
        trackDescription = createEvent(name: "trackDescription", value: device.currentValue("input"))
    }
    //log.debug "parse, status: " + msg.status + ", body: " + msg.body + ", header: " + msg.header
    
    if(status == 200 || status == null) {
        def dataListName = getDataListName(body)
        def apiType = getApiType(body)
        def xml = getXml(body)
        
        if(xml!=null) {
        
            if(dataListName!=null) {
            
                if(dataListName=="Volume Info") {
                    def level = createEvent(name: "level", value: getVolumeInfoLevel(xml.dataList) )
                    def volume = createEvent(name: "volume", value: getVolumeInfoLevel(xml.dataList) )
                    def mute = createEvent(name: "mute", value: getVolumeInfoMuted(xml.dataList) )
                    return [level, volume, mute, switchStatus, power, input, playerStatus, trackDescription]
                }
                
                if(dataListName=="Current Channel Info") {
                    trackDescription = createEvent(name: "trackDescription", value: "EXT")
                    input = createEvent(name: "input", value: "EXT")
                    
                    def channelNumber = createEvent(name: "channelNumber", value: null )
                    def channelName = createEvent(name: "channelName", value: null )
                    def channel = createEvent(name: "channelName", value: null )
                    
                    if(xml.dataList.data!="terrestrial00000-1000") {
                        input = createEvent(name: "input", value: "TV")
                        channelNumber = createEvent(name: "channelNumber", value: getChannelInfoChannelNumber(xml.dataList.data) )
                        channelName = createEvent(name: "channelName", value: getChannelInfoChannelName(xml.dataList.data) )
                        channel = createEvent(name: "channel", value: getChannelInfoChannelName(xml.dataList.data) )
                    }
                    return [input, channelNumber, channelName, switchStatus, power, channel, playerStatus, trackDescription]
                }
                
            }
            
            if(apiType!=null) {
            
                if(apiType=="pairing") {
                    if(xml.api.name == "byebye") {
                        trackDescription = createEvent(name: "trackDescription", value: "off")
                        playerStatus = createEvent(name: "status", value: "stopped")
                        switchStatus = createEvent(name: "switch", value: "off")
                        power = createEvent(name: "power", value: "off")
                        input = createEvent(name: "input", value: "off")
                        
                        return [switchStatus, power, input, playerStatus, trackDescription]
                    }
                }
                
                if(apiType=="event") {
                    if(xml.api.name == "ChannelChanged") {
                        log.debug "ChannelChanged, xml.api: " + xml.api
                        trackDescription = createEvent(name: "trackDescription", value: "EXT")
                        input = createEvent(name: "input", value: "EXT")
                        def channelNumber = createEvent(name: "channelNumber", value: null )
                        def channelName = createEvent(name: "channelName", value: null )

                        if(xml.api!="ChannelChangedterrestrial00000008080") {
                            trackDescription = createEvent(name: "trackDescription", value: "TV")
                            input = createEvent(name: "input", value: "TV")
                            channelNumber = createEvent(name: "channelNumber", value: getChannelInfoChannelNumber(xml.api) )
                            channelName = createEvent(name: "channelName", value: getChannelInfoChannelName(xml.api) )
                        }
                        return [input, channelNumber, channelName, switchStatus, power, playerStatus, trackDescription]
                    }
                }
                
            }
        
        }       
    }
    
    if(status == 400) {
        log.error "parse, 400: bad request, the command format is not valid or it has an incorrect value."
    }
    if(status == 401) {
        log.error "parse, 401: unauthorized, controller not paired, state.counter401: "+ state.counter401
        if(!state.counter401){
            state.counter401 = 0;
        }
        if(state.counter401>=3) {
            log.error "Manual repairing required!"
        } else {
            state.counter401++;
            renwePairing()
        }
    }
    if(status == 404) {
        log.error "parse, 404: not found, path of a command is incorrect."
    }
    if(status == 500) {
        log.error "parse, 500: internal server error, the command execution has failed."
    }
    
    log.debug "Returning switchStatus, power, input, playerStatus"
    return [switchStatus, power, input, playerStatus, trackDescription]
    //HEALTH CHECK
    // TODO: handle 'checkInterval' attribute
    //IMAGE CAPTURE
    // TODO: handle 'image' attribute
    //MEDIA CONTROLLER
    // TODO: handle 'activities' attribute 
    // TODO: handle 'currentActivity' attribute 
    //MUSIC PLAYER
    // TODO: handle 'trackData' attribute < possible, duplicate of other events, nobody uses trackData anyways
    //SAMSUNG TV
    // TODO: handle 'pictureMode' attribute < not possible
    // TODO: handle 'soundMode' attribute < not possible
    // TODO: handle 'messageButton' attribute < wtf?
    //TV
    // TODO: handle 'picture' attribute < not possible
    // TODO: handle 'sound' attribute < not possible
    // TODO: handle 'movieMode' attribute < not possible
}

def installed() {
    log.debug "Executing 'installed'"
    renwePairing()
    runIn(60, getLevel)
}

def updated() {
    log.debug "Executing 'updated'"
    stopPolling()
    startPolling()
}

def getDataListName(String body) {
    try {
        def dataListNameTag = ""+(body =~ /<dataList name=[^>]*/)[0]
        def dataListName = ""+(dataListNameTag =~ /"(.*?)"/)[0][1]
        log.debug "getDataListName, dataListNameTag: " + dataListNameTag
        log.debug "getDataListName, dataListName: " + dataListName
        return dataListName
    } catch (Exception e) {
        log.warn "getDataListName, not valid dataListName, no dataListName, e: " + e
    }
    return null
}

def getApiType(String body) {
    try {
        def apiTypeTag = ""+(body =~ /<api type=[^>]*>/)[0]
        def apiType = ""+(apiTypeTag =~ /"(.*?)"/)[0][1]
        log.debug "getApiType, apiTypeTag: " + apiTypeTag
        log.debug "getApiType, apiType: " + apiType
        return apiType
    } catch (Exception e) {
        log.warn "getApiType, response not valid apiType, no apiType, e: " + e
    }
    return null
}

def getXml(String body) {
    try {
        def xml = parseXml(body)
        return xml
    } catch (Exception e) {
        log.warn "getXml, not xml, e: " + e
    }
    return null
}

def getVolumeInfoLevel(volumeInfo) {
    def level
    try {
        level = volumeInfo.data.level
    } catch (Exception e) {
        log.error "getVolumeInfoLevel, volumeInfo.data.level not set, e: " + e
    }
    return level
}

def getVolumeInfoMuted(volumeInfo) {
    def mute
    try {
        mute = volumeInfo.data.mute
    } catch (Exception e) {
        log.error "getVolumeInfoMuted, volumeInfo.data.mute not set, e: " + e
    }
    return mute
}

def getChannelInfoChannelName(channelInfo) {
    def channelName
    try {
        channelName = channelInfo.chname
        log.debug "getChannelInfoChannelName, channelName: " + channelName
    } catch (Exception e) {
        log.error "getChannelInfoChannelName, channelInfo.chname not set, e: " + e
    }
    return channelName
}

def getChannelInfoChannelNumber(channelInfo) {
    def channelNumber
    try {
        channelNumber = channelInfo.major
        log.debug "getChannelInfoChannelNumber, channelNumber: " + channelNumber
    } catch (Exception e) {
        log.error "getChannelInfoChannelNumber, channelInfo.minor not set, e: " + e
    }
    return channelNumber
}

// Audio Notification
/*def playText() {
    log.debug "Executing 'playText'"
    // TODO: handle 'playText' command
}

def playTextAndResume() {
    log.debug "Executing 'playTextAndResume'"
    // TODO: handle 'playTextAndResume' command
}

def playTextAndRestore() {
    log.debug "Executing 'playTextAndRestore'"
    // TODO: handle 'playTextAndRestore' command
}

def playTrack() {
    log.debug "Executing 'playTrack'"
    // TODO: handle 'playTrack' command
}

def playTrackAndResume() {
    log.debug "Executing 'playTrackAndResume'"
    // TODO: handle 'playTrackAndResume' command
}

def playTrackAndRestore() {
    log.debug "Executing 'playTrackAndRestore'"
    // TODO: handle 'playTrackAndRestore' command
}

//HEALTH CHECK
def ping() {
    log.debug "Executing 'ping'"
    // TODO: handle 'ping' command
}

//IMAGE CAPTURE
def take() {
    log.debug "Executing 'take'"
    // TODO: handle 'take' command
}

//MEDIA CONTROLLER
def startActivity() {
    log.debug "Executing 'startActivity'"
    // TODO: handle 'startActivity' command
}*/

//MUSIC PLAYER
def play() {
    log.debug "Executing 'play'"
    btnPlay()
}

def pause() {
    log.debug "Executing 'pause'"
    btnPause()
}

def stop() {
    log.debug "Executing 'stop'"
    btnStop()
}

def nextTrack() {
    log.debug "Executing 'nextTrack'"
    btnSkipF()
}

def previousTrack() {
    log.debug "Executing 'previousTrack'"
    btnSkipB()
}

/*def playTrack() {
    log.debug "Executing 'playTrack'"
    // TODO: handle 'playTrack' command
}

def setTrack() {
    log.debug "Executing 'setTrack'"
    // TODO: handle 'setTrack' command
}

def resumeTrack() {
    log.debug "Executing 'resumeTrack'"
    // TODO: handle 'resumeTrack' command
}

def restoreTrack() {
    log.debug "Executing 'restoreTrack'"
    // TODO: handle 'restoreTrack' command
}*/

//POLLING
def startPolling() {
    runEvery1Minute(poll)
}

def stopPolling () {
    unschedule()
}

def poll() {
    log.debug "Executing 'poll'"
    
    getLevel()
    getChannelInfo()
}

//REFRESH
def refresh() {
    log.debug "Executing 'refresh'"
    // TODO: handle 'refresh' command
    poll()
}

//SAMSUNG TV
def volumeUp() {
    log.debug "Executing 'volumeUp'"
    
    sendIrCode(24)
}

def volumeDown() {
    log.debug "Executing 'volumeDown'"
    
    sendIrCode(25)
}

def setVolume(level) {
    log.debug "Executing 'setVolume'"
    
    setLevel(level)
}

def mute() {
    log.debug "Executing 'mute'"
    sendIrCode(26)
}

def unmute() {
    log.debug "Executing 'unmute'"
    sendIrCode(26)
}

/* TODO:
def setPictureMode() {
    log.debug "Executing 'setPictureMode'"
    // TODO: handle 'setPictureMode' command
}*/ 

/*def setSoundMode() {
    log.debug "Executing 'setSoundMode'"
    // TODO: handle 'setSoundMode' command
}

def showMessage() {
    log.debug "Executing 'showMessage'"
    // TODO: handle 'showMessage' command
}

//SPEECH SYNTHESIS
def speak() {
    log.debug "Executing 'speak'"
    // TODO: handle 'speak' command
}*/

//SWITCH
def on() {
    log.debug "Executing 'on'"
    // TODO: handle 'on' command
}

def off() {
    log.debug "Executing 'off'"
    sendIrCode(1)
}

//SWITCH LEVEL
def setLevel(level) {
    log.debug "Executing 'setLevel', level: " + level
    state.targetLevel = level
    
    getEnvelope("/udap/api/data?target=volume_info", [callback: getLevelTargetHandler])
}

//TV
def channelUp() {
    log.debug "Executing 'channelUp'"
    
    sendIrCode(27)
}

def channelDown() {
    log.debug "Executing 'channelDown'"
    
    sendIrCode(28)
}

//CUSTOM
def getLevel() {
    log.debug "Executing 'getLevel'"
    
    getEnvelope("/udap/api/data?target=volume_info")
}

def getChannelInfo() {
    log.debug "Executing 'getChannelInfo'"
    
    getEnvelope("/udap/api/data?target=cur_channel")
}

def btn3D() {
    log.debug "Executing 'btn3D'"
    
    sendIrCode(400)
}

def btnInput() {
    log.debug "Executing 'btnInput'"
    
    sendIrCode(47)
}

def btnUp() {
    log.debug "Executing 'btnInput'"
    
    sendIrCode(12)
}

def btnDown() {
    log.debug "Executing 'btnDown'"
    
    sendIrCode(13)
}

def btnLeft() {
    log.debug "Executing 'btnLeft'"
    
    sendIrCode(14)
}

def btnRight() {
    log.debug "Executing 'btnRight'"
    
    sendIrCode(15)
}

def btnOk() {
    log.debug "Executing 'btnOk'"
    
    sendIrCode(20)
}

def btnBack() {
    log.debug "Executing 'btnBack'"
    
    sendIrCode(23)
}

def btnPlay() {
    log.debug "Executing 'btnPlay'"
    
    sendIrCode(33)
}

def btnPause() {
    log.debug "Executing 'btnPause'"
    
    sendIrCode(34)
}

def btnSkipB() {
    log.debug "Executing 'btnSkipB'"
    
    sendIrCode(39)
}

def btnSkipF() {
    log.debug "Executing 'btnSkipF'"
    
    sendIrCode(38)
}

def btnFf() {
    log.debug "Executing 'btnFf'"
    
    sendIrCode(36)
}

def btnRew() {
    log.debug "Executing 'btnRew'"
    
    sendIrCode(37)
}

def btnMyApps() {
    log.debug "Executing 'btnMyApps'"
    
    sendIrCode(417)
}

def btnSmartHome() {
    log.debug "Executing 'btnSmartHome' netcast"
    
    sendIrCode(21)
}

def btnSettings() {
    log.debug "Executing 'btnSettings'"
    
    sendIrCode(22)
}

def btnRatio() {
    log.debug "Executing 'btnRatio'"
    
    sendIrCode(46)
}

def btnQMenu() {
    log.debug "Executing 'btnQMenu'"
    
    sendIrCode(405)
}

def btnAVMode() {
    log.debug "Executing 'btnAVMode'"
    
    sendIrCode(410)
}

def btnFav() {
    log.debug "Executing 'btnFav'"
    
    sendIrCode(404)
}

def btnStop() {
    log.debug "Executing 'btnStop'"
    
    sendIrCode(35)
}

def nextBrightness() {
    log.debug "Executing 'nextBrightness'"
    
    sendIrCode(409, [callback: nextBrightnessDown])
}

def nextBrightnessDown(physicalgraph.device.HubResponse hubResponse) {
    log.debug "Executing 'nextBrightnessDown'"
    
    if(hubResponse.status==200) {
        sendIrCode(13, [callback: nextBrightnessOk])
    }
}

def nextBrightnessOk(physicalgraph.device.HubResponse hubResponse) {
    log.debug "Executing 'nextBrightnessOk'"
    
    if(hubResponse.status==200) {
        sendIrCode(20, [callback: nextBrightnessClose1])
    }
}

def nextBrightnessClose1(physicalgraph.device.HubResponse hubResponse) {
    log.debug "Executing 'nextBrightnessClose1'"
    
    if(hubResponse.status==200) {
        sendIrCode(13, [callback: nextBrightnessClose2])
    }
}

def nextBrightnessClose2(physicalgraph.device.HubResponse hubResponse) {
    log.debug "Executing 'nextBrightnessClose2'"
    
    if(hubResponse.status==200) {
        sendIrCode(23)
        runIn(1, nextBrightnessClose3)
        runIn(3, nextBrightnessClose3)
        runIn(5, nextBrightnessClose3)
        runIn(6, nextBrightnessClose3)
    }
}

def nextBrightnessClose3() {
    log.debug "Executing 'nextBrightnessClose3'"
    
    sendIrCode(23)
}

def nextInput() {
    log.debug "Executing 'nextInput'"
    
    sendIrCode(47, [callback: nextInputHandlerInput])
    //RETURNS TOUCHPAD "TouchPad" when input finally opens
}

def nextInputHandlerInput(physicalgraph.device.HubResponse hubResponse) {
    log.debug "Executing 'nextInputHandlerInput', hubResponse: " + hubResponse
    
    if(hubResponse.status==200) {
        log.debug 'Input button send: OK'
        getEnvelope("/udap/api/data?target=context_ui",  [callback: nextInputContextHandler])
    }
}

def nextInputContextHandler(physicalgraph.device.HubResponse hubResponse) {
     if(hubResponse.status==200) {
        def xml = getXml(hubResponse.body)
        log.debug "context_ui: " + xml.dataList.data.mode
        if(!state.contextCounter){state.contextCounter=0;}
        
        if(xml.dataList.data.mode=="TouchPad") {
            log.debug "input menu seems loaded"
            runIn(4, stRunInLimitation)
            
            state.contextCounter=0
        }
        
        if(xml.dataList.data.mode=="VolCh" && state.contextCounter<3) {
            log.debug "input menu NOT loaded, state.contextCounter: " + state.contextCounter
            getEnvelope("/udap/api/data?target=context_ui",  [callback: nextInputContextHandler])
            state.contextCounter++
        }
    }
}

def stRunInLimitation() {
    sendIrCode(47, [callback: nextInputHandlerSecondInput])
}

def nextInputHandlerSecondInput(physicalgraph.device.HubResponse hubResponse) {
    log.debug "Executing 'nextInputHandlerSecondInput', hubResponse: " + hubResponse
    
    if(hubResponse.status==200) {
        sendIrCode(20, [callback: nextInputHandlerOk])
    }
}

def nextInputHandlerOk(physicalgraph.device.HubResponse hubResponse) {
    log.debug "Executing 'nextInputHandlerOk', hubResponse: " + hubResponse
    
    if(hubResponse.status==200) {
        log.info "nextInputHandlerOk, input changed!"
    }
}

def getLevelTargetHandler(physicalgraph.device.HubResponse hubResponse) {
    log.debug "Executing 'getLevelTargetHandler', hubResponse: " + hubResponse
    def xml = getXml(hubResponse.body)
    
    if(hubResponse.status==200) {
        if(getVolumeInfoLevel(xml.dataList)!=null) {
            sendEvent( name: "level", value: getVolumeInfoLevel(xml.dataList).toInteger() )
            setLevel( (int) device.currentValue("level"), state.targetLevel.toInteger() )
        }
    }
    
}

def setLevelHandler(physicalgraph.device.HubResponse hubResponse) {
    log.debug "Executing 'getLevelTargetHandler', hubResponse: " + hubResponse
    
    if(hubResponse.status==200) {
        setLevel( (int) device.currentValue("level"), state.targetLevel.toInteger() )
    }
    
}

def setLevel(int current, int target) {
    log.debug 'setLevel, current: ' + current + ", target: " + target
    
    if (target>current) {
        def newLevel = device.currentValue("level")+1
        log.debug 'setLevel, one up, newLevel: ' + newLevel
        sendEvent( name: "level", value: newLevel )
        sendIrCode(24, [callback: setLevelHandler])
    }
    if (target<current) {
        def newLevel = device.currentValue("level")-1
        log.debug 'setLevel, one down, newLevel: ' + newLevel
        sendEvent( name: "level", value: newLevel )
        sendIrCode(25, [callback: setLevelHandler])
    }
    if (target==current) {
        log.debug 'setLevel, target reached'
        getLevel()
    }
}

def sendIrCode(int code) {
    log.debug "sendIrCode: " + code
    postEnvelope("/udap/api/command", """<api type="command"><name>HandleKeyInput</name><value>${ code }</value></api>""", "Close")
}

def sendIrCode(int code, callBackObject) {
    log.debug "sendIrCode: " + code + ", callBackObject: "+ callBackObject
    postEnvelope("/udap/api/command", """<api type="command"><name>HandleKeyInput</name><value>${ code }</value></api>""", "Close", callBackObject)
}

def sendIrCode(String[] codes) {
    log.debug 'sendIrCode: ' + codes
    for (int i=0; i<codes.length; i++){
        //log.debug 'sendIrCode: ' + codes[i]
        postEnvelope("/udap/api/command", """<api type="command"><name>HandleKeyInput</name><value>${ codes[i] }</value></api>""", "Keep-Alive")
    }
}

def postEnvelope(String location, String envelope) {
    //log.err "postEnvelope, location: "+location+", envelope: "+envelope
    postEnvelope(location, envelope, "Close")
}

def postEnvelope(String location, String envelope, String connection) {
    //log.err "postEnvelope, location: "+location+", envelope: "+envelope+", connection: "+connection
    postEnvelope(location, envelope, connection, [])
}

def postEnvelope(String location, String envelope, String connection, callBackObject) {
    //log.err "postEnvelope, location: " + location + ", envelope: " + envelope + ", connection: " + connection + ", callBackObject: " + callBackObject
    def host = "${ convertHexToIP(getDataValue("ip")) }:${ convertHexToInt(getDataValue("port")) }"
    def body = """<?xml version="1.0" encoding="utf-8"?><envelope>${ envelope }</envelope>"""
    def header = "POST ${ location } HTTP/1.1\r\nHOST: ${ host }\r\nUser-Agent: UDAP/2.0 Samsung Smartthings\r\nContent-Type: text/xml; charset=utf-8\r\nCache-Control: no-cache\r\nContent-Length: ${ body.length() }\r\nConnection: ${ connection }\r\n\r\n"
    def hubAction = new physicalgraph.device.HubAction("""${ header }${ body }""", physicalgraph.device.Protocol.LAN, host)
    
    if(callBackObject!=[]) {
        hubAction = new physicalgraph.device.HubAction("""${ header }${ body }""", physicalgraph.device.Protocol.LAN, host, callBackObject)
    }
    
    sendHubAction(hubAction)
}

def getEnvelope(String location) {
    getEnvelope(location, [])
}

def getEnvelope(String location, callBackObject) {
    def host = "${ convertHexToIP(getDataValue("ip")) }:${ convertHexToInt(getDataValue("port")) }"
    def hubAction = new physicalgraph.device.HubAction("""GET ${ location } HTTP/1.1\r\nHOST: ${ host }\r\nUser-Agent: UDAP/2.0 Samsung Smartthings \r\n\r\n""", physicalgraph.device.Protocol.LAN, host)

    if(callBackObject!=[]) {
        log.warn "callBackObject: " + callBackObject
        hubAction = new physicalgraph.device.HubAction("""GET ${ location } HTTP/1.1\r\nHOST: ${ host }\r\nUser-Agent: UDAP/2.0 Samsung Smartthings \r\n\r\n""", physicalgraph.device.Protocol.LAN, host, callBackObject)
    }
    sendHubAction(hubAction)
}

def sendHubAction(physicalgraph.device.HubAction hubAction) {
    sendHubCommand(hubAction)
    log.debug "sendHubAction: " + hubAction
}

private Integer convertHexToInt(hex) {
    Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
    [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getCallBackAddress() {
    return device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

def renwePairing() {
    log.debug "Executing renwePairing()"
    String envelope = """<api type="pairing"><name>hello</name><value>${ getDataValue("pairingKey") }</value><port>${ device.hub.getDataValue('localSrvPortTCP') }</port></api>"""
    postEnvelope("/udap/api/pairing", envelope)
}