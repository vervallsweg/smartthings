/**
 *  cast web - group sync
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
    name: "Cast web - group sync",
    namespace: "vervallsweg",
    author: "Tobias Haerke",
    description: "Syncs the music playback on a Cast audio group, accessed through a web API.",
    category: "Convenience",
    iconUrl: "https://github.com/vervallsweg/smartthings/raw/master/icn/ic_speaker_group_grey_24px.png",
    iconX2Url: "https://github.com/vervallsweg/smartthings/raw/master/icn/ic_speaker_group_grey_24px.png",
    iconX3Url: "https://github.com/vervallsweg/smartthings/raw/master/icn/ic_speaker_group_grey_24px.png")


preferences {
    section("Audio group") {
        input "theAudioGroup", "capability.musicPlayer", required: true, title: "Audio group", multiple: false
    }
    section("Members of this group") {
        input "theMembers", "capability.musicPlayer", required: true, multiple: true, title: "Members of this group"
    }
    section("Version info") {
        paragraph "This version: " + getThisVersion() + ", latest: " + getLatestVersion()
    }
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
    state.lastStatus = ""
    setLabel()
    subscribe(theAudioGroup, "status", groupStatusUpdated) //only subscribes to status==play&&status==pause
    subscribe(theAudioGroup, "getDeviceStatus", groupStatusUpdated)
    subscribe(app, syncVolume)
}

def setLabel() {
    if(app.label=="Google cast web - group sync") {
        if(theAudioGroup.label) {
            log.debug "Changing label to: " + theAudioGroup.label
            app.updateLabel(theAudioGroup.label)
        }
    }
}

def syncVolume(evt) {
    log.debug "app event (syncVolume) ${evt.name}:${evt.value} received"
    double masterVolume = Double.parseDouble(theAudioGroup.currentState('level').value)
    log.debug "masterVolume: " + masterVolume
    
    theMembers.each{
        it.setLevel(masterVolume)
    }
    //runIn(10, groupStatusUpdated) //WTF?
}

def groupStatusUpdated(evt) {
    log.debug "groupStatusUpdated called, theAudioGroup.currentState('status').value: " + theAudioGroup.currentState("status").value
    
    if( theAudioGroup.currentState("status").value.equals("playing") || theAudioGroup.currentState("status").value.equals("paused") ) {
        state.lastStatus = ""
        theMembers.each {
            if( !it.currentState("status").value.equals("group") ) {
                log.warn "setGroupPlayback(true) calling on it: " + it
                it.setGroupPlayback(true)
            }   
        }
    }
    if( theAudioGroup.currentState("status").value.equals("Ready to cast") ) {
        if( !state.lastStatus.equals("Ready to cast") ) {
            log.warn "setGroupPlayback(false) calling on theMembers.each"
            state.lastStatus = "Ready to cast"
            theMembers.each {
                it.setGroupPlayback(false)
            }
        }
    }
}

def getThisVersion() {
    return 0.2
}

def getLatestVersion() {
    try {
        httpGet([uri: "https://raw.githubusercontent.com/vervallsweg/smartthings/master/smartapps/vervallsweg/cast-web-group-sync.src/version.json"]) { resp ->
            log.debug "getLatestVersion(), response status: " + resp.status
            String data = "${resp.getData()}"
            log.debug "getLatestVersion(), data: " + data
            
            if(resp.status==200 && data!=null) {
                return parseJson(data).version
            } else {
                return null
            }
        }
    } catch (e) {
        log.error "getLatestVersion(), something went wrong: " + e
        return null
    }
}