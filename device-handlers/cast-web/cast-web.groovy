/**
 *  cast-web
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
import org.json.JSONObject

preferences {
    input("configOn", "enum", title: "Switch on does?",
        required: false, multiple:false, value: "nothing", options: ["Play","Pause","Stop","Play preset 1","Play preset 2","Play preset 3","Play preset 4","Play preset 5","Play preset 6"])
    input("configNext", "enum", title: "Next song does?",
        required: false, multiple:false, value: "nothing", options: ["Play","Pause","Stop","Play preset 1","Play preset 2","Play preset 3","Play preset 4","Play preset 5","Play preset 6"])
    input("configPrev", "enum", title: "Previous song does?",
        required: false, multiple:false, value: "nothing", options: ["Play","Pause","Stop","Play preset 1","Play preset 2","Play preset 3","Play preset 4","Play preset 5","Play preset 6"])
}
 
metadata {
    definition (name: "cast-web", namespace: "vervallsweg", author: "Tobias Haerke") {
        capability "actuator"
        capability "Music Player"
        capability "switch"
        capability "Speech Synthesis"
        capability "Refresh"
        capability "Polling"
        capability "Audio Notification"
        //capability "Health Check" //TODO: Implement health check

        command "restartPolling"
        command "updateAttributesMedia"
        command "playPreset", ["number"]
        command "preset1"
        command "preset2"
        command "preset3"
        command "preset4"
        command "preset5"
        command "preset6"
        command "off"
        
        // SONOS' CUSTOM COMMANDS: //command "subscribe" //command "getVolume" //command "getCurrentMedia" //command "getCurrentStatus" //command "seek" //command "unsubscribe" //command "setLocalLevel", ["number"] //command "tileSetLevel", ["number"] //attribute "currentValue" //command "playSoundAndTrack", ["string","number","json_object","number"] //command "playTrackAtVolume", ["string","number"]
        //command "playText", ["string", "number"] //(STRING message, NUMBER level)
        //command "playTextAndRestore", ["string","number"] //(STRING message, NUMBER level)
        //command "playTrack", ["string","number"] //(STRING uri, NUMBER level)
        //command "playTrackAndRestore", ["string","number","number"] //(STRING uri, NUMBER level)
        
        //LATER maybe use restoreTrack, resume Track 
        //command "playTextAndResume", ["string","number"] //(STRING message, NUMBER level)
        //command "playTrackAndResume", ["string","number","number"] //(STRING uri, NUMBER level)  
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "mediaMulti", type:"mediaPlayer", width:6, height:4, canChangeIcon: true) {
            tileAttribute("device.status", key: "PRIMARY_CONTROL") {
                attributeState("paused", label:"Paused", icon:"st.sonos.pause-icon")
                attributeState("playing", label:"Playing", icon:"st.sonos.play-icon", backgroundColor:"#00a0dc")
                attributeState("Ready to cast", label:"Ready to cast")
                attributeState("group", label:"Group playback", icon:"st.sonos.play-icon", backgroundColor:"#00a0dc")
            }
            tileAttribute("device.status", key: "MEDIA_STATUS") {
                attributeState("paused", label:"Paused", icon:"st.sonos.pause-icon", action:"music Player.play", nextState: "playing")
                attributeState("playing", label:"Playing", icon:"st.sonos.play-icon", action:"music Player.pause", nextState: "paused", backgroundColor:"#00a0dc")
                attributeState("Ready to cast", label:"Ready to cast", action:"refresh", nextState: "Ready to cast")
                attributeState("group", label:"Group", icon:"st.sonos.play-icon", backgroundColor:"#00a0dc")
            }
            tileAttribute("device.status", key: "PREVIOUS_TRACK") {
                attributeState("status", action:"music Player.previousTrack", defaultState: true)
            }
            tileAttribute("device.status", key: "NEXT_TRACK") {
                attributeState("status", action:"music Player.nextTrack", defaultState: true)
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL", icon: "st.custom.sonos.unmuted") {
                attributeState("level", action:"music Player.setLevel", icon: "st.custom.sonos.unmuted")
            }
            tileAttribute ("device.mute", key: "MEDIA_MUTED") {
                attributeState("unmuted", action:"music Player.mute", nextState: "muted", icon: "st.custom.sonos.unmuted")
                attributeState("muted", action:"music Player.unmute", nextState: "unmuted", icon: "st.custom.sonos.muted")
            }
            tileAttribute("device.trackDescription", key: "MARQUEE") {
                attributeState("trackDescription", label:"${currentValue}", defaultState: true)
            }
        }
        
        valueTile("applicationName", "device.deviceApplicationDisplayName", width: 2, height: 2) {
            state "val", label:'${currentValue}', defaultState: true
        }
        
        standardTile("updateDeviceStatus", "device", width: 2, height: 2, decoration: "flat") {
            state "val", label: 'Refresh', action: "refresh", icon: "st.secondary.refresh-icon", backgroundColor: "#ffffff", defaultState: true
        }
        
        standardTile("stop", "device", width: 2, height: 2, decoration: "flat") {
            state "val", label: '', action: "music Player.stop", icon: "st.sonos.stop-btn", backgroundColor: "#ffffff", defaultState: true
        }
        
        valueTile("albumName", "device.trackDataAlbumName", width: 2, height: 2) {
            state "val", label:'${currentValue}', defaultState: true
        }
        
        valueTile("title", "device.trackDataTitle", width: 2, height: 2) {
            state "val", label:'${currentValue}', defaultState: true
        }
        
        valueTile("artist", "device.trackDataArtist", width: 2, height: 2) {
            state "val", label:'${currentValue}', defaultState: true
        }
        
        valueTile("deviceNetworkId", "device.dni", width: 2, height: 2) {
            state "val", label:'${currentValue}', defaultState: true
        }
        
        standardTile("preset1", "device.preset1Name", width: 2, height: 2, decoration: "flat") {
            state "val", label:'${currentValue}', action:'preset1', defaultState: true
        }
        
        standardTile("preset2", "device.preset2Name", width: 2, height: 2, decoration: "flat") {
            state "val", label:'${currentValue}', action:'preset2', defaultState: true
        }
        
        standardTile("preset3", "device.preset3Name", width: 2, height: 2, decoration: "flat") {
            state "val", label:'${currentValue}', action:'preset3', defaultState: true
        }
        
        standardTile("preset4", "device.preset4Name", width: 2, height: 2, decoration: "flat") {
            state "val", label:'${currentValue}', action:'preset4', defaultState: true
        }
        
        standardTile("preset5", "device.preset5Name", width: 2, height: 2, decoration: "flat") {
            state "val", label:'${currentValue}', action:'preset5', defaultState: true
        }
        
        standardTile("preset6", "device.preset6Name", width: 2, height: 2, decoration: "flat") {
            state "val", label:'${currentValue}', action:'preset6', defaultState: true
        }
        
        main "mediaMulti"
        details(["mediaMulti",
                "applicationName",
                "stop",
                "updateDeviceStatus",
                "title",
                "albumName",
                "artist",
                "preset1",
                "preset2",
                "preset3",
                "preset4",
                "preset5",
                "preset6",
                "deviceNetworkId"])
    }
}

// Device handler states

def installed() {
    //log.debug "Executing 'installed'"
    setDefaultAttributes()
    
    //Poll settings
    Random rand = new Random(now())
    updateDataValue('pollSecond', ""+rand.nextInt(60))
    updateDataValue('pollMinutes', "5")
    updateDataValue('deviceType', "video")
    startPolling()
    
    //Presets, tiles
    sendEvent(name: "dni", value: device.deviceNetworkId, displayed: false)
    setDefaultPresets()
}

def updated() {
    //log.debug "Executing 'updated'"
    
    setDefaultPresets()
    restartPolling()
}

def refresh() {
    getDeviceStatus()
}

def poll() {
    if(state.badResponseCounter>4) {
        //log.error "badResponseCounter>4"
        return stopPolling()
    }
    if(device.currentValue("trackDataTitle")=="Group playback") {
        //log.debug("'trackDataTitle' == Group playback")
        return stopPolling()
    } else {
        return refresh()
    }
}

def startPolling() {
    //log.debug "startPolling"
    
    poll()

    Random rand = new Random(now())
    def seconds = getDataValue('pollSecond')
    def minutes = getDataValue('pollMinutes')
    def sched = "${seconds} 0/${minutes} * * * ?"

    //log.debug "Scheduling polling task with \"${sched}\""
    schedule(sched, poll)
}

def stopPolling() {
    //log.debug "stopPolling"
    unschedule()
}

def restartPolling() {
    //log.debug "restartPolling"
    stopPolling()
    startPolling()
}

// parse events into attributes
def parse(String description) {
    //log.debug "Parsing '${description}'"
    
    def msg, json, status
    try {
        msg = parseLanMessage(description)
        status = msg.status
        json = msg.json
    } catch (e) {
        //log.warning "Exception caught while parsing data: "+e
        setDefaultAttributes()
        return null;
    }
    
    if(status==200){
        state.badResponseCounter=0
        if(json.type=="RECEIVER_STATUS"){
            //log.debug "Received RECEIVER_STATUS"
            updateAttributesDevice(json.status)
        }
        if(json.type=="MEDIA_STATUS"){
            //log.debug "Received MEDIA_STATUS"
            updateAttributesMedia(json.status)
            if(device.currentValue("lastPresetPlayed")!=null) {
                //log.warn "last preset played: " + device.currentValue("lastPresetPlayed")
                getDeviceStatus()
                sendEvent(name: "lastPresetPlayed", value: null)
            }
            
        }
    } else {
        //log.error "HTTP response not ok, status code: " + status + " requestId: " + msg.requestId;
        state.badResponseCounter++
        setDefaultAttributes()
    }
}

// handle commands
def play() {
    //log.debug "Executing 'play'"
    if(device.currentValue("deviceSessionId")!=null&&device.currentValue("deviceMediaSessionId")!=null){
        setMediaPlaybackPlay(device.currentValue("deviceSessionId"), device.currentValue("deviceMediaSessionId"))
    }
}

def pause() {
    //log.debug "Executing 'pause'"
    if(device.currentValue("deviceSessionId")!=null&&device.currentValue("deviceMediaSessionId")!=null){
        setMediaPlaybackPause(device.currentValue("deviceSessionId"), device.currentValue("deviceMediaSessionId"))
    }
}

def stop() {
    //log.debug "Executing 'stop'"
    if(device.currentValue("deviceSessionId")!=null){
        setDevicePlaybackStop(device.currentValue("deviceSessionId"))
    } else {
        //log.debug "No deviceSessionId"
    }
}

def nextTrack() {
    //log.debug "Executing 'nextTrack' encode: "
    selectableAction(settings.configNext)
}

def previousTrack() {
    //log.debug "Executing 'previousTrack'"
    selectableAction(settings.configPrev)
}

def playTrack(uri, level) {
    log.info "Executing 'playTrack': " + uri

    if (level) {
        setLevel(level)
    }
    return setMediaPlayback('audio/mp3', trackToPlay, 'BUFFERED', 'SmartThings', 'SmartThings%20playback', 'https://lh3.googleusercontent.com/nQBLtHKqZycERjdjMGulMLMLDoPXnrZKYoJ8ijaVs8tDD6cypInQRtxgngk9SAXHkA=w300')
}

def setLevel(level) {
    //log.debug "Executing 'setLevel'"
    double lvl = (double) level;
    setDeviceVolume(lvl)
}

def mute() {
    //log.debug "Executing 'mute'"
    setDeviceMuted(true)
}

def unmute() {
    //log.debug "Executing 'unmute'"
    setDeviceMuted(false)
}

def setTrack(trackToSet) {
    //log.debug "Executing 'setTrack'"
    // TODO: handle 'setTrack' command
}

def resumeTrack() {
    //log.debug "Executing 'resumeTrack'"
    // TODO: handle 'resumeTrack' command
}

def restoreTrack() {
    //log.debug "Executing 'restoreTrack'"
    // TODO: handle 'restoreTrack' command
}

def setDefaultPresets() {
    def p1 = "Preset 1", p2 = "Preset 2", p3 = "Preset 3", p4 = "Preset 4", p5 = "Preset 5", p6 = "Preset 6", presets
    if(getDataValue("presetObject")) {
        try {
            presets = parseJson(getDataValue("presetObject"))
        } catch (Exception e) {
            //log.debug "Cannot parse JSON: " + e
            setDefaultPresetObject()
        }
        
        try{p1=presets.preset1.mediaTitle}catch(Exception e){/*log.debug "Preset 1 not set"*/}
        try{p2=presets.preset2.mediaTitle}catch(Exception e){/*log.debug "Preset 2 not set"*/}
        try{p3=presets.preset3.mediaTitle}catch(Exception e){/*log.debug "Preset 3 not set"*/}
        try{p4=presets.preset4.mediaTitle}catch(Exception e){/*log.debug "Preset 4 not set"*/}
        try{p5=presets.preset5.mediaTitle}catch(Exception e){/*log.debug "Preset 5 not set"*/}
        try{p6=presets.preset6.mediaTitle}catch(Exception e){/*log.debug "Preset 6 not set"*/}
        
    } else {
        setDefaultPresetObject()
    }
    sendEvent(name: "preset1Name", value: p1, displayed: false)
    sendEvent(name: "preset2Name", value: p2, displayed: false)
    sendEvent(name: "preset3Name", value: p3, displayed: false)
    sendEvent(name: "preset4Name", value: p4, displayed: false)
    sendEvent(name: "preset5Name", value: p5, displayed: false)
    sendEvent(name: "preset6Name", value: p6, displayed: false)
}

def setDefaultPresetObject() {
    def defaultObject = '{"preset1":{"mediaTitle":"Preset 1","mediaSubtitle":"","mediaType":"","mediaUrl":"","mediaStreamType":"","mediaImageUrl":""},"preset2":{"mediaTitle":"Preset 2","mediaSubtitle":"","mediaType":"","mediaUrl":"","mediaStreamType":"","mediaImageUrl":""},"preset3":{"mediaTitle":"Preset 3","mediaSubtitle":"","mediaType":"","mediaUrl":"","mediaStreamType":"","mediaImageUrl":""},"preset4": {"mediaTitle":"Preset 4","mediaSubtitle":"","mediaType":"","mediaUrl":"","mediaStreamType":"","mediaImageUrl":""},"preset5":{"mediaTitle":"Preset 5","mediaSubtitle":"","mediaType":"","mediaUrl":"","mediaStreamType":"","mediaImageUrl":""},"preset6":{"mediaTitle":"Preset 6","mediaSubtitle":"","mediaType":"","mediaUrl":"","mediaStreamType":"","mediaImageUrl":""}}'
    //log.debug "DO: " + defaultObject
    updateDataValue("presetObject", defaultObject)
}

def playPreset(number) {
    //log.debug "Executing 'playPreset': "+number
    sendEvent(name: "lastPresetPlayed", value: number)
    
    def presets, preset, mediaType, mediaUrl, streamType, mediaTitle, mediaSubtitle, mediaImageUrl
    try {
        presets = parseJson(getDataValue("presetObject"))
        if(number==1) {preset=presets.preset1}
        if(number==2) {preset=presets.preset2}
        if(number==3) {preset=presets.preset3}
        if(number==4) {preset=presets.preset4}
        if(number==5) {preset=presets.preset5}
        if(number==6) {preset=presets.preset6}
        mediaType = preset.mediaType
        mediaUrl = preset.mediaUrl
        streamType = preset.streamType
        mediaTitle = preset.mediaTitle
        mediaSubtitle = preset.mediaSubtitle
        mediaImageUrl = preset.mediaImageUrl
        if(mediaTitle!="Preset "+number) {
            setMediaPlayback(mediaType, mediaUrl, mediaStreamType, mediaTitle, mediaSubtitle, mediaImageUrl)
        }
    } catch (Exception e) {
        //log.debug "Couldn't play preset: " + e
    }
}

def preset1() {
    //log.debug "Executing 'preset1'"
    playPreset(1)
}

def preset2() {
    //log.debug "Executing 'preset2': "
    playPreset(2)
}

def preset3() {
    //log.debug "Executing 'preset3': "
    playPreset(3)    
}

def preset4() {
    //log.debug "Executing 'preset4': "
    playPreset(4)
}

def preset5() {
    //log.debug "Executing 'preset5': "
    playPreset(5)
}

def preset6() {
    //log.debug "Executing 'preset6': "
    playPreset(6)
}

def on() {
    //log.debug "Executing 'on'"
    selectableAction(settings.configOn)
}

def off() {
    //log.debug "Executing 'off'"
    stop()
}

def speak(phrase) {
    //def sound = textToSpeech(phrase, true)
    return playTrack( textToSpeech(phrase, true).uri )
}

def playTrackAtVolume(String, number) {
    log.info "playTrackAtVolume" + String;
    
    def url = "" + String;
    if(number!=null) { setLevel(number); }
    return playTrack(String)
}

def playTrackAndResume(uri, level) {
    log.info "playTrackAndResume: " + uri

    if (level) {
      setLevel(level)  
    }
    return playTrack(uri)
    //TODO: resume playback to previously playing track
}

def playTextAndResume(message, level) {
    log.info "playTextAndResume: " + message

    if (level) {
      setLevel(level)  
    }
    return speak(message)
    //TODO: resume playback to previously playing track
}

def playTrackAndRestore(uri, level) {
    log.info "playTrackAndRestore: " + uri

    if (level) {
        setLevel(level)        
    }
    return playTrackAtVolume(uri, level)
}

def playText(message, level) {
    log.info "playText, message: " + message

    if (level) {
        log.info "level: " + level
        setLevel(number)
    }
    return speak(message)
}

def playTextAndRestore(message, level) {
    log.info "playTextAndRestore, message: " + message

    if (level) {
        log.info "level: " + level
        //TODO: Reset level to level before the message was played
    }
    return speak(message)
}

// HANDLE ATTRIBUTES
def updateAttributesDevice(deviceStatus) {
    //log.debug "Executing 'updateAttributesDevice'"
    
    if(deviceStatus.volume.level) {
        //log.debug "deviceStatus.volume.level: "+(Math.round(deviceStatus.volume.level*100))
        sendEvent(name: "level", value: Math.round(deviceStatus.volume.level*100), unit: "%")
    }
    if(deviceStatus.volume.muted) {
        //log.debug "deviceStatus.volume.muted: "+deviceStatus.volume.muted
        sendEvent(name: "mute", value: "muted")
    } else {
        //log.debug "deviceStatus.volume.muted: "+deviceStatus.volume.muted
        sendEvent(name: "mute", value: "unmuted")
    }
    try {
        if(deviceStatus.applications.displayName.get(0)!="Backdrop"&&getDataValue("deviceType")!="Video") {
            //log.debug "Receiver has sessionId: "+deviceStatus.applications.sessionId.get(0)
            sendEvent(name: "deviceSessionId", value: deviceStatus.applications.sessionId.get(0), displayed: false)

            //log.debug "Receiver application running, displayName: "+deviceStatus.applications.displayName.get(0)
            sendEvent(name: "deviceApplicationDisplayName", value: deviceStatus.applications.displayName.get(0), displayed: false)
            getMediaStatus(deviceStatus.applications.sessionId.get(0))
        } else {
            throw new NullPointerException() //Btw. check the logs, such ironic expection caught :D
        }
    } catch (e) {
        //log.debug "No application running, exception: "+e
        sendEvent(name: "status", value: "Ready to cast")
        sendEvent(name: "switch", value: off)
        sendEvent(name: "playpause", value: "pause", displayed: false)
        setDefaultAttributesMedia()
    }
}

def updateAttributesMedia(mediaStatus) {
    //log.debug "Executing 'updateAttributesMedia'"
    //log.trace "mediaStatus: "+mediaStatus
    
    if(mediaStatus.playerState) {
        //log.debug "mediaStatus.playerState: "+mediaStatus.playerState.get(0).toLowerCase()
        sendEvent(name: "status", value: mediaStatus.playerState.get(0).toLowerCase(), changed: true)
        if(mediaStatus.playerState.get(0).toLowerCase()=="playing") {
            sendEvent(name: "playpause", value: "play", displayed: false)
            sendEvent(name: "switch", value: on)
        }
        if(mediaStatus.playerState.get(0).toLowerCase()=="paused") {
            sendEvent(name: "playpause", value: "pause", displayed: false)
            sendEvent(name: "switch", value: off)
        }
    } else {
        //log.debug "mediaStatus.playerState not set, probably group playback"
        sendEvent(name: "status", value: "group");
        sendEvent(name: "switch", value: on)
        sendEvent(name: "playpause", value: "play", displayed: false)
    }
    
    if(mediaStatus.media) {
        if(mediaStatus.media.metadata.metadataType) { //OR IS PART OF GROUP PLAYBACK
            if(mediaStatus.media.metadata.metadataType.get(0)==3||mediaStatus.media.metadata.metadataType.get(0)==0) {
                //log.debug "mediaStatus.media.metadata.metadataType is MusicTrackMediaMetadata"
                JSONObject jsonO = new JSONObject(mediaStatus.media.metadata.get(0))
                sendEvent(name: "trackData", value: jsonO, displayed:false)
                updateAttributesTrack()
            }
        } 
    } else {
        //log.debug "mediaStatus.media not set, probably group playback"
        sendEvent(name: "trackData", value: ('{ "title": "Group playback" }'), displayed: false)
        updateAttributesTrack()
    }
    
    if(mediaStatus.mediaSessionId){
        //log.debug "mediaStatus.mediaSessionId: "+mediaStatus.mediaSessionId.get(0)
        sendEvent(name: "deviceMediaSessionId", value: mediaStatus.mediaSessionId.get(0), displayed: false)
    } else {
        //log.debug "mediaStatus.mediaSessionId not set, probably group playback"
        sendEvent(name: "deviceMediaSessionId", value: null, displayed: false)
    }
}

def updateAttributesTrack() {
    //log.debug "Executing 'updateAttributesTrack'"
    JSONObject jO = new JSONObject(device.currentValue("trackData"))
    
    if(jO.has("albumName")) {
        if(device.currentValue("trackDataAlbumName")!=jO.getString("albumName")){
            //log.debug 'jO.getString("albumName"): ' +jO.getString("albumName")
            sendEvent(name: "trackDataAlbumName", value: jO.getString("albumName"), displayed:false)
        }
    } else if(jO.has("subtitle")) {
        if(device.currentValue("trackDataAlbumName")!=jO.getString("subtitle")){
            //log.debug 'jO.getString("subtitle"): ' +jO.getString("subtitle")
            sendEvent(name: "trackDataAlbumName", value: jO.getString("subtitle"), displayed:false)
        }
    } else {
        sendEvent(name: "trackDataAlbumName", value: "--", displayed:false)
    }
    if(jO.has("title")) {
        if(device.currentValue("trackDataTitle")!=jO.getString("title")){
            //log.debug 'jO.getString("title"): ' +jO.getString("title")
            sendEvent(name: "trackDataTitle", value: jO.getString("title"), displayed:false)
        }
    } else {
        sendEvent(name: "trackDataTitle", value: "--", displayed:false)
    }
    if(jO.has("artist")) {
        if(device.currentValue("trackDataArtist")!=jO.getString("artist")){
            //log.debug 'jO.getString("artist"): ' +jO.getString("artist")
            sendEvent(name: "trackDataArtist", value: jO.getString("artist"), displayed:false)
        }
    } else {
        sendEvent(name: "trackDataArtist", value: "--", displayed:false)
    }
    
    if(device.currentValue("trackDataTitle")){
        //log.debug "mediaStatus.media.metadata.title: "+device.currentValue("trackDataTitle")
        def trackDescription = device.currentValue("deviceApplicationDisplayName") +"\n"+ device.currentValue("trackDataTitle")
        
        if(device.currentValue("trackDataAlbumName")!="--") {
            trackDescription = trackDescription +"\n"+ device.currentValue("trackDataAlbumName")
        }
        sendEvent(name: "trackDescription", value: trackDescription, displayed:false)
    }
}

// SET DEFAULT ATTRIBUTES
def setDefaultAttributes() {
    ////log.debug "Executing 'setDefaultAttributes'"
    setDefaultAttributesDevice()
    setDefaultAttributesMedia()
}

def setDefaultAttributesDevice() {
    ////log.debug "Executing 'setDefaultAttributesDevice'"
   
    sendEvent(name: "level", value: 0)
    sendEvent(name: "mute", value: true)
    sendEvent(name: "status", value: "Ready to cast")
    sendEvent(name: "switch", value: off)
    sendEvent(name: "playpause", value: "pause", displayed: false)
}

def setDefaultAttributesMedia() {
    //log.debug "Executing 'setDefaultAttributesMedia'"
    
    sendEvent(name: "trackDescription", value: "Nothing playing \n Ready to cast", displayed: false)
    sendEvent(name: "deviceMediaSessionId", value: null, displayed: false)
    sendEvent(name: "deviceSessionId", value: null, displayed: false)
    sendEvent(name: "deviceApplicationDisplayName", value: "Ready to cast", displayed: false)
    sendEvent(name: "switch", value: off)
    sendEvent(name: "trackData", value: new JSONObject("{}"), displayed: false)
    updateAttributesTrack()
}

// GOOGLE CAST
def getDeviceStatus() {
    //log.debug "Executing 'getDeviceStatus'"
    sendEvent(name: "getDeviceStatus", value: new Date(), displayed: false)
    sendHttpRequest(getDataValue('apiHost'), '/getDeviceStatus?address='+getDataValue('deviceAddress'))
}

def getMediaStatus(sessionId) {
    //log.debug "Executing 'getMediaStatus' sessionId: "+sessionId
    sendHttpRequest(getDataValue('apiHost'), '/getMediaStatus?address='+getDataValue('deviceAddress')+'&sessionId='+sessionId)
}

def setDeviceVolume(double volume) {
    //log.debug "Executing 'setDeviceVolume' volume: "+(volume/100)
    sendHttpRequest(getDataValue('apiHost'), '/setDeviceVolume?address='+getDataValue('deviceAddress')+'&volume='+(volume/100))
}

def setDeviceMuted(boolean muted) {
    //log.debug "Executing 'setDeviceMuted' muted: "+muted
    sendHttpRequest(getDataValue('apiHost'), '/setDeviceMuted?address='+getDataValue('deviceAddress')+'&muted='+muted)
}

def setMediaPlaybackPlay(sessionId, mediaSessionId) {
    //log.debug "Executing 'setMediaPlaybackPlay'; sessionId: "+sessionId+" mediaSessionId: "+mediaSessionId
    sendHttpRequest(getDataValue('apiHost'), '/setMediaPlaybackPlay?address='+getDataValue('deviceAddress')+'&sessionId='+sessionId+'&mediaSessionId='+mediaSessionId)
}

def setMediaPlaybackPause(sessionId, mediaSessionId) {
    //log.debug "Executing 'setMediaPlaybackPause'; sessionId: "+sessionId+" mediaSessionId: "+mediaSessionId
    sendHttpRequest(getDataValue('apiHost'), '/setMediaPlaybackPause?address='+getDataValue('deviceAddress')+'&sessionId='+sessionId+'&mediaSessionId='+mediaSessionId)
}

def setDevicePlaybackStop(sessionId) {
    //log.debug "Executing 'setDevicePlaybackStop'; sessionId: "+sessionId+" mediaSessionId: "+mediaSessionId
    sendHttpRequest(getDataValue('apiHost'), '/setDevicePlaybackStop?address='+getDataValue('deviceAddress')+'&sessionId='+sessionId)
}

def setMediaPlayback(mediaType, mediaUrl, mediaStreamType, mediaTitle, mediaSubtitle, mediaImageUrl) {
    //log.debug "Executing 'setMediaPlayback'; mediaType: "+mediaType+" mediaUrl: "+mediaUrl+" mediaStreamType: "+mediaStreamType+" mediaTitle: "+mediaTitle+" mediaSubtitle: "+mediaSubtitle+" mediaImageUrl: "+mediaImageUrl
    sendHttpRequest(getDataValue('apiHost'), '/setMediaPlayback?address='+getDataValue('deviceAddress')+'&mediaType='+mediaType+'&mediaUrl='+mediaUrl+'&mediaStreamType='+mediaStreamType+'&mediaTitle='+urlEncode(mediaTitle)+'&mediaSubtitle='+urlEncode(mediaSubtitle)+'&mediaImageUrl='+mediaImageUrl)
}

// NETWORKING STUFF
def sendHttpRequest(String host, String path) {
    //log.debug "Executing 'sendHttpRequest' host: "+host+" path: "+path
    sendHubCommand(new physicalgraph.device.HubAction("""GET ${path} HTTP/1.1\r\nHOST: $host\r\n\r\n""", physicalgraph.device.Protocol.LAN, host, [callback: hubResponseReceived]))
}

void hubResponseReceived(physicalgraph.device.HubResponse hubResponse) {
    parse(hubResponse.description)
}

// PRESETS
def getNewPresetObject(mediaType, mediaUrl, mediaStreamType, mediaTitle, mediaSubtitle, mediaImageUrl) {
    JSONObject jsO = new JSONObject('{ "mediaType":"'+mediaType+'", "mediaUrl":"'+mediaUrl+'", "mediaStreamType":"'+mediaStreamType+'", "mediaTitle":"'+mediaTitle+'", "mediaSubtitle":"'+mediaSubtitle+'", "mediaImageUrl":"'+mediaImageUrl+'" }')
    //log.error("JSO:" + jsO)
    return jsO
}

// HELPERS
def getTimeStamp() {
    Date now = new Date(); 
    def timeStamp = (long)(now.getTime()/1000)
    //log.info "Timestamp generated: "+timeStamp
    return timeStamp;
}

def urlEncode(String) {
    return java.net.URLEncoder.encode(String, "UTF-8")
}

def selectableAction(action) {
    if(action=="Play") {
        play()
    }
    if(action=="Pause") {
        pause()
    }
    if(action=="Stop") {
        stop()
    }
    if(action=="Play preset 1") {
        playPreset(1)
    }
    if(action=="Play preset 2") {
        playPreset(2)
    }
    if(action=="Play preset 3") {
        playPreset(3)
    }
    if(action=="Play preset 4") {
        playPreset(4)
    }
    if(action=="Play preset 5") {
        playPreset(5)
    }
    if(action=="Play preset 6") {
        playPreset(6)
    }
}

//DEBUGGING
def logDeviceValues() {
    //log.debug "Executing 'logDeviceValues'"
    //log.debug "apiHost: " + getDataValue('apiHost')
    //log.debug "deviceAddress: " + getDataValue('deviceAddress')
    //log.debug "deviceName: " + getDataValue('deviceName')
}