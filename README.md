# udb

## What is it?
`udb` is a native comand line tool, build with Kotlin/Native, which simplifies a lot of typical `adb` use cases. 

It's very early in development stages so don't get angry if it doesn't quite work as well as you hoped!

## Install
Homebrew coming very soon!

For now, you can check out this repository and run `./gradlew installReleaseBinary` to build from the source.

## Usage

`udb ui`
Dump UI nodes active in the view hierarchy

`udb ui --watch`
Subscribe to change in the view hierarchy

`adb ui --tap "Continue"`
Search the view hierarchy for a node with matching text and then tap it

`adb ui --type "Hello"`
Input the provided text (like keyboard input)

`adb ui --packages`
Dump list of all the app package names contributing to the active view hierarchy

`adb open http://www.bbc.co.uk/`
Open the provided URL on the device, the intent is available for any application to handle

`adb open com.myunidays/settings`
Searches for closest matching activity from applications installed on the connected device and launches it. This command aims to be flexible and forgiving.

`udb devices`
List connected devices

`udb devices --network-scan`
Attempt to connect to Bonjour registered Android devices, e.g. Android TVs

`udb devices --fly`
Re-connect to all USB connected devices via WiFi so that they can be unplugged!

`udb emulator --start`
Launch an emulator (note: you need to have an AVD created)

`udb emulator --start --silent`
Launch an emulator invisibly, without a window or sound

`udb emulator --stop`
Request the currently running emulator to stop

`udb logcat`
Subscribes to logcat logs from all connected devices
