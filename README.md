![Guard_dog_resize](https://user-images.githubusercontent.com/1350444/149637084-270521ab-2d96-4c54-a7b4-71357fb6b291.png)

# Watchdog
[![](https://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/installs/plugin/watchdog)](https://runelite.net/plugin-hub/show/watchdog)

Create custom alerts triggered by
- Chat/Game Messages (supports glob pattern or regex)
- Stat Drain/Change
- Sound Fired (trigger on a sound effect playing)
- Notifications (supports glob pattern or regex. allows you to hook into existing notifications, even if you have the alerts off in Runelite. For example low prayer, idle, tempoross, etc) 

with any amount of unique notification types like
- Game Message
- Screen Flash
- Custom Sound
- Text to Speech
- Tray Notification
- Overhead Text
- Overlay

## Example
![image](https://user-images.githubusercontent.com/1350444/210900848-4c177374-9b74-4cb3-b7a7-92c29596ff40.png)

## Capture Groups
With glob patterns (or regex) and capture groups you can make your notifications dynamic.
Say you have a Notification Fired Alert with the Message set to `Your {*} is ready to harvest in {*}`.
This will capture the crop and the location which you can then use in your notifications.
In message type notifications like Text to Speech, Tray Notification, or Game Message you can use those
capture groups to create your own message.
For example, you could have a TTS notification set to `Go get your $1 in $2!` which would make it say something like
`Go get your Ranarr in Ardougne!`.

You can also use capture groups in a Sound Notification to create a dynamic file path. It's a little tricky since you
have to have a dummy file that has the capture group values in the name so that the path can be read with the variables.
But if you select a file with the name `$1.wav` for example you can then have other files in the same directory like
`ranarr.wav` and `torstol.wav` to have unique sounds for different herb types.
