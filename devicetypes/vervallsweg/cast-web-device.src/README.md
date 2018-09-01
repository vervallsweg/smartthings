# Changelog
##Version 1.1.0
- Added: support for preset queues

##Version 1.0.0
- Renamed to cast-web-device
- Compatible with API version 1.0.0
- Cast device changes immediately displayed in ST
- No more polling
- Audio notifications can now resume last playing preset
- Support for queued playback
- Connection to the API displayed on 'refresh' button
- New Google Cast icons added
- Group playback now detected by API automatically
- New selectable options in device settings
- Device icon no longer changeable by user
- User can now change TTS voice to Google, thanks to [@CultusMechanicus](https://github.com/vervallsweg/cast-web-api/pull/64 "@CultusMechanicus")

## Version 0.2
- DTH is now aware of which preset is currently playing
- Indicators for the currently playing presets
- Next/prev buttons can now swich presets accordingly
- Short media files, like ST notifications will not cause device reset anymore
- Optimized all parsing methods
- Cleaned device events to minimum required

## Version 0.1
- Check for updates on every device page
- SmartThings GitHub integration
- [Audio notification](http://docs.smartthings.com/en/latest/capabilities-reference.html#audio-notification "Audio notification") capability added
- SmartThings 'Speaker Companion' [fixed](https://github.com/vervallsweg/smartthings/issues/7 "fixed")
- DTH's loging level can now be changed
- Group playback detection temporarily disabled
- Renamed to cast-web
- Changelog added