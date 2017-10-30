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
        capability "Actuator"
        capability "Audio Notification"
        capability "Music Player"
        capability "Polling"
        capability "Refresh"
        capability "Speech Synthesis"
        capability "Switch"
        //capability "Health Check" //TODO: Implement health check

        command "checkForUpdate"
        command "preset1"
        command "preset2"
        command "preset3"
        command "preset4"
        command "preset5"
        command "preset6"
        command "playPreset", ["number"]
        //command "off"
        command "playText", ["string"]
        command "playText", ["string", "number"]
        command "playTextAndResume", ["string"]
        command "playTextAndResume", ["string", "number"]
        command "playTextAndRestore", ["string"]
        command "playTextAndRestore", ["string", "number"]
        command "restartPolling"
        command "updateAttributesMedia"
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
        
        valueTile("updateStatus", "device.updateStatus", width: 2, height: 2) {
            state "val", label:'${currentValue}', defaultState: true, action: "checkForUpdate"
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
                "deviceNetworkId",
                "updateStatus"])
    }
}

// Device handler states

def installed() {
    logger('debug', "Executing 'installed'")
    setDefaultAttributes()
    
    //Poll settings
    Random rand = new Random(now())
    updateDataValue('pollSecond', ""+rand.nextInt(60))
    updateDataValue('pollMinutes', "5")
    updateDataValue('deviceType', "video")
    updateDataValue('logLevel', "0")
    startPolling()
    
    //Presets, tiles
    sendEvent(name: "dni", value: device.deviceNetworkId, displayed: false)
    sendEvent(name: "updateStatus", value: ("Version "+getThisVersion() + "\nClick to check for updates"), displayed: false)
    setDefaultPresets()
}

def updated() {
    logger('debug', "Executing 'updated'")
    sendEvent(name: "updateStatus", value: ("Version "+getThisVersion() + "\nClick to check for updates"), displayed: false)
    
    setDefaultPresets()
    restartPolling()
}

def refresh() {
    getDeviceStatus()
}

def poll() {
    if(state.badResponseCounter>4) {
        logger('error', "poll(), state.badResponseCounter > 4")
        return stopPolling()
    }
    if(device.currentValue("trackDataTitle")=="Group playback") {
        logger('debug', "poll(), 'trackDataTitle' == Group playback")
        return stopPolling()
    } else {
        return refresh()
    }
}

def startPolling() {
    logger('debug', "Executing 'startPolling()'")
    
    poll()

    Random rand = new Random(now())
    def seconds = getDataValue('pollSecond')
    def minutes = getDataValue('pollMinutes')
    def sched = "${seconds} 0/${minutes} * * * ?"

    logger('debug', "startPolling(), scheduling polling task with \"${sched}\"")
    schedule(sched, poll)
    runEvery3Hours(checkForUpdate)
}

def stopPolling() {
    logger('debug', "stopPolling")
    unschedule()
}

def restartPolling() {
    logger('debug', "restartPolling")
    stopPolling()
    startPolling()
}

// parse events into attributes
def parse(String description) {
    logger('debug', "Parsing '${description}'")
    
    def msg, json, status
    try {
        msg = parseLanMessage(description)
        status = msg.status
        json = msg.json
        logger('debug', 'parse, msg.json: ' + json)
    } catch (e) {
        logger('warn', "Exception caught while parsing data: "+e)
        setDefaultAttributes()
        return null;
    }
    
    if(status==200){
        state.badResponseCounter=0
        if(json.type=="RECEIVER_STATUS"){
            logger('debug', "Received RECEIVER_STATUS")
            updateAttributesDevice(json.status)
        }
        if(json.type=="MEDIA_STATUS"){
            logger('debug', "Received MEDIA_STATUS")
            updateAttributesMedia(json.status)
            if(device.currentValue("lastPresetPlayed")!=null) {
                logger('warn', "last preset played: " + device.currentValue("lastPresetPlayed"))
                getDeviceStatus()
                sendEvent(name: "lastPresetPlayed", value: null)
            }
            
        }
    } else {
        logger('error', "HTTP response not ok, status code: " + status + " requestId: " + msg.requestId)
        state.badResponseCounter++
        setDefaultAttributes()
    }
}

// handle commands
def play() {
    logger('debug', "Executing 'play'")
    if(device.currentValue("deviceSessionId")!=null&&device.currentValue("deviceMediaSessionId")!=null){
        setMediaPlaybackPlay(device.currentValue("deviceSessionId"), device.currentValue("deviceMediaSessionId"))
    }
}

def pause() {
    logger('debug', "Executing 'pause'")
    if(device.currentValue("deviceSessionId")!=null&&device.currentValue("deviceMediaSessionId")!=null){
        setMediaPlaybackPause(device.currentValue("deviceSessionId"), device.currentValue("deviceMediaSessionId"))
    }
}

def stop() {
    logger('debug', "Executing 'stop'")
    if(device.currentValue("deviceSessionId")!=null){
        setDevicePlaybackStop(device.currentValue("deviceSessionId"))
    }
}

def nextTrack() {
    logger('debug', "Executing 'nextTrack' encode: ")
    selectableAction(settings.configNext)
}

def previousTrack() {
    logger('debug', "Executing 'previousTrack'")
    selectableAction(settings.configPrev)
}

def setLevel(level) {
    logger('debug', "Executing 'setLevel', level: " + level)
    double lvl
    try { lvl = (double) level; } catch (e) {
        lvl = Double.parseDouble(level)
    }
    setDeviceVolume(lvl)
}

def mute() {
    logger('debug', "Executing 'mute'")
    setDeviceMuted(true)
}

def unmute() {
    logger('debug', "Executing 'unmute'")
    setDeviceMuted(false)
}

def setTrack(trackToSet) {
    logger('debug', "Executing 'setTrack'")
    return playTrack(trackToSet)
}

def resumeTrack(trackToSet) {
    logger('debug', "Executing 'resumeTrack'")
    return playTrack(trackToSet)
}

def restoreTrack(trackToSet) {
    logger('debug', "Executing 'restoreTrack'")
    return playTrack(trackToSet)
}

def setDefaultPresets() {
    def p1 = "Preset 1", p2 = "Preset 2", p3 = "Preset 3", p4 = "Preset 4", p5 = "Preset 5", p6 = "Preset 6", presets
    if(getDataValue("presetObject")) {
        try {
            presets = parseJson(getDataValue("presetObject"))
        } catch (Exception e) {
            logger('debug', "Cannot parse JSON: " + e)
            setDefaultPresetObject()
        }
        
        try{p1=presets.preset1.mediaTitle}catch(Exception e){logger('debug', "Preset 1 not set")}
        try{p2=presets.preset2.mediaTitle}catch(Exception e){logger('debug', "Preset 2 not set")}
        try{p3=presets.preset3.mediaTitle}catch(Exception e){logger('debug', "Preset 3 not set")}
        try{p4=presets.preset4.mediaTitle}catch(Exception e){logger('debug', "Preset 4 not set")}
        try{p5=presets.preset5.mediaTitle}catch(Exception e){logger('debug', "Preset 5 not set")}
        try{p6=presets.preset6.mediaTitle}catch(Exception e){logger('debug', "Preset 6 not set")}
        
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
    logger('debug', "DO: " + defaultObject)
    updateDataValue("presetObject", defaultObject)
}

def playPreset(number) {
    logger('debug', "Executing 'playPreset': "+number)
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
        logger('debug', "Couldn't play preset: " + e)
    }
}

def preset1() {
    logger('debug', "Executing 'preset1'")
    playPreset(1)
}

def preset2() {
    logger('debug', "Executing 'preset2': ")
    playPreset(2)
}

def preset3() {
    logger('debug', "Executing 'preset3': ")
    playPreset(3)    
}

def preset4() {
    logger('debug', "Executing 'preset4': ")
    playPreset(4)
}

def preset5() {
    logger('debug', "Executing 'preset5': ")
    playPreset(5)
}

def preset6() {
    logger('debug', "Executing 'preset6': ")
    playPreset(6)
}

def on() {
    logger('debug', "Executing 'on'")
    selectableAction(settings.configOn)
}

def off() {
    logger('debug', "Executing 'off'")
    stop()
}

def speak(phrase) {
    //def sound = textToSpeech(phrase, true)
    return playTrack( textToSpeech(phrase, true).uri )
}
//AUDIO NOTIFICATION, TEXT
def playText(message, level = 0) {
    logger('info', "playText, message: " + message + " level: " + level)
    
    if (level!=0&&level!=null) { setLevel(level) }
    return speak(message)
}

def playTextAndResume(message, level = 0, thirdValue = 0) {
    logger('info', "playTextAndResume, message: " + message + " level: " + level)
    //TODO: resume playback to previously playing track
    playText(message, level)
}

def playTextAndRestore(message, level = 0, thirdValue = 0) {
    logger('info', "playTextAndRestore, message: " + message + " level: " + level)
    //TODO: Reset level to level before the message was played
    playText(message, level)
}

def playTrackAtVolume(trackToPlay, level = 0) {
    logger('info', "playTrackAtVolume" + trackToPlay)
    
    def url = "" + trackToPlay;
    return playTrack(url, level)
}
//AUDIO NOTIFICATION, TRACK
def playTrack(uri, level = 0, thirdValue = 0) {
    logger('info', "Executing 'playTrack', uri: " + uri + " level: " + level)

    if (level!=0&&level!=null) { setLevel(level) }
    return setMediaPlayback('audio/mp3', uri, 'BUFFERED', 'SmartThings', 'SmartThings playback', 'https://lh3.googleusercontent.com/nQBLtHKqZycERjdjMGulMLMLDoPXnrZKYoJ8ijaVs8tDD6cypInQRtxgngk9SAXHkA=w300')
}

def playTrackAndResume(uri, level = 0) {
    logger('info', "Executing 'playTrackAndResume', uri: " + uri + " level: " + level)
    //TODO: resume playback to previously playing track
    return playTrack(uri, level)
}

def playTrackAndResume(String uri, String duration, level = 0) {
    logger('info', "Executing 'playTrackAndResume', uri: " + uri + " duration: " + duration + " level: " + level)
    //TODO: resume playback to previously playing track
    return playTrack(uri, level)
}

def playTrackAndRestore(uri, level = 0) {
    logger('info', "Executing 'playTrackAndRestore', uri: " + uri + " level: " + level)
    //TODO: restore
    return playTrack(uri, level) 
}

def playTrackAndRestore(String uri, String duration, level = 0) {
    logger('info', "Executing 'playTrackAndRestore', uri: " + uri + " duration: " + duration + " level: " + level)
    //TODO: restore
    return playTrack(uri, level) 
}

// HANDLE ATTRIBUTES
def updateAttributesDevice(deviceStatus) {
    logger('debug', "Executing 'updateAttributesDevice'")
    
    if(deviceStatus.volume.level) {
        logger('debug', "deviceStatus.volume.level: "+(Math.round(deviceStatus.volume.level*100)))
        sendEvent(name: "level", value: Math.round(deviceStatus.volume.level*100), unit: "%")
    }
    if(deviceStatus.volume.muted) {
        logger('debug', "deviceStatus.volume.muted: "+deviceStatus.volume.muted)
        sendEvent(name: "mute", value: "muted")
    } else {
        logger('debug', "deviceStatus.volume.muted: "+deviceStatus.volume.muted)
        sendEvent(name: "mute", value: "unmuted")
    }
    try {
        if(deviceStatus.applications.displayName.get(0)!="Backdrop"&&getDataValue("deviceType")!="Video") {
            logger('debug', "Receiver has sessionId: "+deviceStatus.applications.sessionId.get(0))
            sendEvent(name: "deviceSessionId", value: deviceStatus.applications.sessionId.get(0), displayed: false)

            logger('debug', "Receiver application running, displayName: "+deviceStatus.applications.displayName.get(0))
            sendEvent(name: "deviceApplicationDisplayName", value: deviceStatus.applications.displayName.get(0), displayed: false)
            getMediaStatus(deviceStatus.applications.sessionId.get(0))
        } else {
            throw new NullPointerException() //Btw. check the logs, such ironic expection caught :D
        }
    } catch (e) {
        logger('debug', "No application running, exception: "+e)
        sendEvent(name: "status", value: "Ready to cast")
        sendEvent(name: "switch", value: off)
        sendEvent(name: "playpause", value: "pause", displayed: false)
        setDefaultAttributesMedia()
    }
}

def updateAttributesMedia(mediaStatus) {
    logger('debug', "Executing 'updateAttributesMedia'")
    logger('debug', "mediaStatus: "+mediaStatus)
    
    if(mediaStatus.playerState) {
        logger('debug', "mediaStatus.playerState: "+mediaStatus.playerState.get(0).toLowerCase())
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
        logger('debug', "mediaStatus.playerState not set, probably group playback")
        /*sendEvent(name: "status", value: "group");
        sendEvent(name: "switch", value: on)
        sendEvent(name: "playpause", value: "play", displayed: false)*/
    }
    
    if(mediaStatus.media) {
        if(mediaStatus.media.metadata.metadataType) { //OR IS PART OF GROUP PLAYBACK
            if(mediaStatus.media.metadata.metadataType.get(0)==3||mediaStatus.media.metadata.metadataType.get(0)==0) {
                logger('debug', "mediaStatus.media.metadata.metadataType is MusicTrackMediaMetadata")
                JSONObject jsonO = new JSONObject(mediaStatus.media.metadata.get(0))
                sendEvent(name: "trackData", value: jsonO, displayed:false)
                updateAttributesTrack()
            }
        } 
    } else {
        logger('debug', "mediaStatus.media not set, probably group playback")
        /*sendEvent(name: "trackData", value: ('{ "title": "Group playback" }'), displayed: false)*/
        updateAttributesTrack()
    }
    
    if(mediaStatus.mediaSessionId){
        logger('debug', "mediaStatus.mediaSessionId: "+mediaStatus.mediaSessionId.get(0))
        sendEvent(name: "deviceMediaSessionId", value: mediaStatus.mediaSessionId.get(0), displayed: false)
    } else {
        logger('debug', "mediaStatus.mediaSessionId not set, probably group playback")
        sendEvent(name: "deviceMediaSessionId", value: null, displayed: false)
    }
}

def updateAttributesTrack() {
    logger('debug', "Executing 'updateAttributesTrack'")
    JSONObject jO = new JSONObject(device.currentValue("trackData"))
    
    if(jO.has("albumName")) {
        if(device.currentValue("trackDataAlbumName")!=jO.getString("albumName")){
            logger('debug', 'jO.getString("albumName"): ' +jO.getString("albumName"))
            sendEvent(name: "trackDataAlbumName", value: jO.getString("albumName"), displayed:false)
        }
    } else if(jO.has("subtitle")) {
        if(device.currentValue("trackDataAlbumName")!=jO.getString("subtitle")){
            logger('debug', 'jO.getString("subtitle"): ' +jO.getString("subtitle"))
            sendEvent(name: "trackDataAlbumName", value: jO.getString("subtitle"), displayed:false)
        }
    } else {
        sendEvent(name: "trackDataAlbumName", value: "--", displayed:false)
    }
    if(jO.has("title")) {
        if(device.currentValue("trackDataTitle")!=jO.getString("title")){
            logger('debug', 'jO.getString("title"): ' +jO.getString("title"))
            sendEvent(name: "trackDataTitle", value: jO.getString("title"), displayed:false)
        }
    } else {
        sendEvent(name: "trackDataTitle", value: "--", displayed:false)
    }
    if(jO.has("artist")) {
        if(device.currentValue("trackDataArtist")!=jO.getString("artist")){
            logger('debug', 'jO.getString("artist"): ' +jO.getString("artist"))
            sendEvent(name: "trackDataArtist", value: jO.getString("artist"), displayed:false)
        }
    } else {
        sendEvent(name: "trackDataArtist", value: "--", displayed:false)
    }
    
    if(device.currentValue("trackDataTitle")){
        logger('debug', "mediaStatus.media.metadata.title: "+device.currentValue("trackDataTitle"))
        def trackDescription = device.currentValue("deviceApplicationDisplayName") +"\n"+ device.currentValue("trackDataTitle")
        
        if(device.currentValue("trackDataAlbumName")!="--") {
            trackDescription = trackDescription +"\n"+ device.currentValue("trackDataAlbumName")
        }
        sendEvent(name: "trackDescription", value: trackDescription, displayed:false)
    }
}

// SET DEFAULT ATTRIBUTES
def setDefaultAttributes() {
    logger('debug', "Executing 'setDefaultAttributes'")
    setDefaultAttributesDevice()
    setDefaultAttributesMedia()
}

def setDefaultAttributesDevice() {
    logger('debug', "Executing 'setDefaultAttributesDevice'")
   
    sendEvent(name: "level", value: 0)
    sendEvent(name: "mute", value: true)
    sendEvent(name: "status", value: "Ready to cast")
    sendEvent(name: "switch", value: off)
    sendEvent(name: "playpause", value: "pause", displayed: false)
}

def setDefaultAttributesMedia() {
    logger('debug', "Executing 'setDefaultAttributesMedia'")
    
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
    logger('debug', "Executing 'getDeviceStatus'")
    sendEvent(name: "getDeviceStatus", value: new Date(), displayed: false)
    sendHttpRequest(getDataValue('apiHost'), '/getDeviceStatus?address='+getDataValue('deviceAddress'))
}

def getMediaStatus(sessionId) {
    logger('debug', "Executing 'getMediaStatus' sessionId: "+sessionId)
    sendHttpRequest(getDataValue('apiHost'), '/getMediaStatus?address='+getDataValue('deviceAddress')+'&sessionId='+sessionId)
}

def setDeviceVolume(double volume) {
    logger('debug', "Executing 'setDeviceVolume' volume: "+(volume/100))
    sendHttpRequest(getDataValue('apiHost'), '/setDeviceVolume?address='+getDataValue('deviceAddress')+'&volume='+(volume/100))
}

def setDeviceMuted(boolean muted) {
    logger('debug', "Executing 'setDeviceMuted' muted: "+muted)
    sendHttpRequest(getDataValue('apiHost'), '/setDeviceMuted?address='+getDataValue('deviceAddress')+'&muted='+muted)
}

def setMediaPlaybackPlay(sessionId, mediaSessionId) {
    logger('debug', "Executing 'setMediaPlaybackPlay'; sessionId: "+sessionId+" mediaSessionId: "+mediaSessionId)
    sendHttpRequest(getDataValue('apiHost'), '/setMediaPlaybackPlay?address='+getDataValue('deviceAddress')+'&sessionId='+sessionId+'&mediaSessionId='+mediaSessionId)
}

def setMediaPlaybackPause(sessionId, mediaSessionId) {
    logger('debug', "Executing 'setMediaPlaybackPause'; sessionId: "+sessionId+" mediaSessionId: "+mediaSessionId)
    sendHttpRequest(getDataValue('apiHost'), '/setMediaPlaybackPause?address='+getDataValue('deviceAddress')+'&sessionId='+sessionId+'&mediaSessionId='+mediaSessionId)
}

def setDevicePlaybackStop(sessionId) {
    logger('debug', "Executing 'setDevicePlaybackStop'; sessionId: "+sessionId+" mediaSessionId: "+mediaSessionId)
    sendHttpRequest(getDataValue('apiHost'), '/setDevicePlaybackStop?address='+getDataValue('deviceAddress')+'&sessionId='+sessionId)
}

def setMediaPlayback(mediaType, mediaUrl, mediaStreamType, mediaTitle, mediaSubtitle, mediaImageUrl) {
    logger('debug', "Executing 'setMediaPlayback'; mediaType: "+mediaType+" mediaUrl: "+mediaUrl+" mediaStreamType: "+mediaStreamType+" mediaTitle: "+mediaTitle+" mediaSubtitle: "+mediaSubtitle+" mediaImageUrl: "+mediaImageUrl)
    sendHttpRequest(getDataValue('apiHost'), '/setMediaPlayback?address='+getDataValue('deviceAddress')+'&mediaType='+mediaType+'&mediaUrl='+mediaUrl+'&mediaStreamType='+mediaStreamType+'&mediaTitle='+urlEncode(mediaTitle)+'&mediaSubtitle='+urlEncode(mediaSubtitle)+'&mediaImageUrl='+mediaImageUrl)
}

// NETWORKING STUFF
def sendHttpRequest(String host, String path) {
    logger('debug', "Executing 'sendHttpRequest' host: "+host+" path: "+path)
    sendHubCommand(new physicalgraph.device.HubAction("""GET ${path} HTTP/1.1\r\nHOST: $host\r\n\r\n""", physicalgraph.device.Protocol.LAN, host, [callback: hubResponseReceived]))
}

void hubResponseReceived(physicalgraph.device.HubResponse hubResponse) {
    parse(hubResponse.description)
}

// PRESETS
def getNewPresetObject(mediaType, mediaUrl, mediaStreamType, mediaTitle, mediaSubtitle, mediaImageUrl) {
    JSONObject jsO = new JSONObject('{ "mediaType":"'+mediaType+'", "mediaUrl":"'+mediaUrl+'", "mediaStreamType":"'+mediaStreamType+'", "mediaTitle":"'+mediaTitle+'", "mediaSubtitle":"'+mediaSubtitle+'", "mediaImageUrl":"'+mediaImageUrl+'" }')
    logger('error', "JSO:" + jsO)
    return jsO
}

// HELPERS
def getTimeStamp() {
    Date now = new Date(); 
    def timeStamp = (long)(now.getTime()/1000)
    logger('info', "Timestamp generated: "+timeStamp)
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

//UPDATE
def getThisVersion() {
    return 0.1
}

def getLatestVersion() {
    try {
        httpGet([uri: "https://raw.githubusercontent.com/vervallsweg/smartthings/master/devicetypes/vervallsweg/cast-web.src/version.json"]) { resp ->
            logger('debug', "response status: ${resp.status}")
            String data = "${resp.getData()}"
            logger('debug', "data: ${data}")
            
            if(resp.status==200 && data!=null) {
                return parseJson(data)
            } else {
                return null
            }
        }
    } catch (e) {
        logger('error', "something went wrong: $e")
        return null
    }
}

def checkForUpdate() {
    def latestVersion = getLatestVersion()
    if (latestVersion == null) {
        logger('error', "Couldn't check for new version, thisVersion: " + getThisVersion())
        sendEvent(name: "updateStatus", value: ("Version "+getThisVersion() + "\n Error getting latest version \n"), displayed: false)
        return null
    }
    if(getThisVersion() != latestVersion) {
        logger('debug', "New version available, " + "thisVersion: " + getThisVersion() + ", latestVersion: " + getLatestVersion().version)
        sendEvent(name: "updateStatus", value: ("Version "+getThisVersion() + "\n Latestx: \n" + getLatestVersion().version), displayed: false)
    } else {
        sendEvent(name: "updateStatus", value: ("Version "+getThisVersion() + "\nUp to date"), displayed: false)
        logger('info', "Up to date, " + "thisVersion: " + getThisVersion() + ", latestVersion: " + getLatestVersion().version)
    }
}

//DEBUGGING
def logger(level, message) {
    def logLevel=0
    if(getDataValue('logLevel')) {
        logLevel = getDataValue('logLevel').toInteger()
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