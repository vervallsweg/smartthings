<link rel="stylesheet" type="text/css" href="semantic/dist/semantic.min.css">
<script
  src="https://code.jquery.com/jquery-3.1.1.min.js"
  integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
  crossorigin="anonymous"></script>
<script src="semantic/dist/semantic.min.js"></script>
# Preset generator
Generate presets for your Google Cast devices in Smartthings.
## What are presets?
They can be used to quickly play your favorite content from the Smartthings app. Furthermore you can use them to automate your cast devices, use them with Smartapps, routines, CoRE, you name it.
## Limitations
Due to the way the Google Cast protocol works, you're limited to the default media receiver. This means you can only play DRM free content, such as mp3/mp4 files/streams. For more information about what you can play visit the [web-api's documentation](https://github.com/vervallsweg/cast-web-api#setmediaplayback-address-mediatype-mediaurl-mediastreamtype-mediatitle-mediasubtitle-mediaimageurl "web-api's documentation"), it will link you to everything you should know.
## 1. Paste your current presets (optional)
<input type="text" name="current_preset" name="current_preset">
<button>Skip this step</button><button>Next</button>
## 2. Edit your presets
<input type="text" name="blah">
## 3. Copy your new presets
<input type="text" name="output_preset">
<button>Copy to clipboard</button>
