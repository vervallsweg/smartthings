# Capabilities
## all planned capabilities
"Audio Notification"
	>Commands: playText, playTextAndResume, playTextAndRestore, playTrack, playTrackAndResume, playTrackAndRestore
"Health Check"
	>Attributes: checkInterval
	Commands: ping
"Image Capture"
	>Attributes: image
	Commands: take
"Media Controller"
	>Attributes: activities, currentActivity
	Commands: startActivity
"Music Player"
	>Attributes: status, level, trackDescription, trackData, mute
	Commands: play, pause, stop, nextTrack, playTrack, setLevel, mute, previousTrack, unmute, setTrack, resumeTrack, restoreTrack
"Polling"
	>Commands: poll
"Refresh"
	>Commands: refresh
"Samsung TV"
	>Attributes: volume, mute, pictureMode, soundMode, switch, messageButton
	Commands: volumeUp, volumeDown, setVolume, mute, unmute, setPictureMode, setSoundMode, on, off, showMessage
"Speech Synthesis"
	>Commands: speak
"Switch"
	>Attributes: switch
	Commands: on, off
"Switch Level"
	>Attributes: level
	Commands: setLevel
"TV"
	>Attributes: volume, channel, power, picture, sound, movieMode
	Commands: volumeUp, volumeDown, channelUp, channelDown
## roadmap
### Version 0.1
- "Switch" < on, off
- "Switch Level" < either volume or brightness
- "Polling" < refresh automatically
- "Refresh" < refresh status
-> rcu: power, volumeUp, volumeDown, brightness (eco), input, av, left, right, up, down, ok, back, exit
### Version 0.2
- "Samsung TV"
- "TV"
- "Music Player"
- "Media Controller"
### Version 3.0
- "Speech Synthesis"
- "Audio Notification"
### Version 4.0
- "Image Capture"