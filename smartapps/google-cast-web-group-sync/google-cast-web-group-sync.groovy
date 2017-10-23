/**
 *  Cast web - group sync
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
    iconUrl: "https://github.com/vervallsweg/cast-web-api/raw/master/icn/ic_speaker_group_grey_24px.png",
    iconX2Url: "https://github.com/vervallsweg/cast-web-api/raw/master/icn/ic_speaker_group_grey_24px.png",
    iconX3Url: "https://github.com/vervallsweg/cast-web-api/raw/master/icn/ic_speaker_group_grey_24px.png")


preferences {
    section("Audio group") {
        input "theAudioGroup", "capability.musicPlayer", required: true, title: "Audio group", multiple: false
    }
    section("Members of this group") {
        input "theMembers", "capability.musicPlayer", required: true, multiple: true, title: "Members of this group"
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
	// TODO: subscribe to attributes, devices, locations, etc.
    setLabel()
    subscribe(theAudioGroup, "status", groupStatusUpdated)
   	subscribe(theAudioGroup, "getDeviceStatus", groupStatusUpdated)
    subscribe(app, syncVolume)
}

def setLabel() {
	if(app.label=="Cast web - group sync") {
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
    
    runIn(10, groupStatusUpdated)
}

def groupStatusUpdated(evt) {
	log.debug "groupStatusUpdated called"
    
    theMembers.each{
    	log.debug "theAudioGroup.currentState('getDeviceStatus').value: " + theAudioGroup.currentState("getDeviceStatus").value
        log.debug "theAudioGroup.currentState('status').value " + theAudioGroup.currentState("status").value
        
        if(theAudioGroup.currentState("status").value == "playing" || theAudioGroup.currentState("status").value == "paused") {
        	log.debug "Setting group for: " + it.name
            def jsonData = parseJson("{}")
            log.debug "jsonData: " + jsonData
            it.updateAttributesMedia(jsonData)
        }
        if(theAudioGroup.currentState("status").value == "Ready to cast") {
        	log.warn "restartPolling"
            it.refresh()
        	it.restartPolling()
        }
    }
}

// TODO: implement event handlers