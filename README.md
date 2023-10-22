![Guard_dog_resize](https://user-images.githubusercontent.com/1350444/149637084-270521ab-2d96-4c54-a7b4-71357fb6b291.png)

# Watchdog
[![Plugin Hub](https://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/installs/plugin/watchdog)](https://runelite.net/plugin-hub/show/watchdog)
[![Discord](https://img.shields.io/discord/1064234152314015875?color=%235865F2&label=Watchdog&logo=discord&logoColor=white&style=flat)](https://discord.gg/n8mxYAHJR9)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/T6T0JH18I)

Create custom alerts triggered by
- Game Messages (supports glob pattern or regex)
- Player Chat Messages (supports glob pattern or regex)
- Stat Drain/Change
- Notifications (supports glob pattern or regex. allows you to hook into existing notifications, even if you have the alerts off in Runelite. For example low prayer, idle, tempoross, etc) 
- Inventory Changes
- Object/Item/NPC Spawned
- XP Drop

with any amount of unique notification types like
- Game Message
- Screen Flash
- Custom Sound
- In-Game Sound Effect
- Text to Speech (Now supports using Eleven Labs)
- Tray Notification
- Overhead Text
- Overlay
- RuneLite Notification (to trigger things like RL Tray Notifications)

_You can set defaults for the notifications in the plugin config_

## Examples
![Attack Drained Example](https://user-images.githubusercontent.com/1350444/221425644-0211c4d7-2838-4e63-986a-8ab313052ad5.png)
![Harvest Example](https://user-images.githubusercontent.com/1350444/221425625-4e246cb6-eff0-4f8f-855f-80fd7b36bc9d.png)

## Alert Hub
Add alerts other users have shared directly from the panel!

![image](https://github.com/adamk33n3r/runelite-watchdog/assets/1350444/08ecf612-11ba-4bd1-b2c3-d624e40ca9a1)


## Capture Groups
With glob patterns (or regex) and capture groups you can make your notifications dynamic. Using glob patterns you wrap
the text you want in curly braces `{}` and in regex you use parenthesis `()`.
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
