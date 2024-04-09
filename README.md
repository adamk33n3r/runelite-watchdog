![Guard_dog_resize](https://user-images.githubusercontent.com/1350444/149637084-270521ab-2d96-4c54-a7b4-71357fb6b291.png)

# Watchdog
[![Plugin Hub](https://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/installs/plugin/watchdog)](https://runelite.net/plugin-hub/show/watchdog)
[![Discord](https://img.shields.io/discord/1064234152314015875?color=%235865F2&label=Watchdog&logo=discord&logoColor=white&style=flat)](https://discord.gg/n8mxYAHJR9)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/T6T0JH18I)

Create custom alerts triggered by
- Game Messages (supports glob pattern or regex)
- Player Chat Messages (supports glob pattern or regex)
- Stat Drain/Change
- Sound Fired (trigger on a sound effect playing)
- Notifications (supports glob pattern or regex. allows you to hook into existing notifications, even if you have the alerts off in Runelite. For example low prayer, idle, tempoross, etc) 
- Inventory Changes
- Object/Item/NPC Spawned
- XP Drop
- Location

with any amount of unique notification types like
- Game Message
- Screen Flash
- Custom Sound
- In-Game Sound Effect
- Text to Speech (Now supports using Eleven Labs)
- Tray Notification
- Overhead Text
- Overlay
- Dismiss Overlay
- Screen Marker
- Request Focus
- RuneLite Notification (to trigger things like RL Tray Notifications)

_You can set defaults for the notifications in the plugin config_

For more information on trigger and notification types, [see the wiki](https://github.com/adamk33n3r/runelite-watchdog/wiki).

**_NOTE: Alerts will not fire in boss areas._**

## Recommended RuneLite Notification Settings
![image](https://github.com/adamk33n3r/runelite-watchdog/assets/1350444/18eb10dd-9ddb-4248-9d5f-ddc335acc103)

Request Focus should be set to `Off` otherwise you will get some wrong behaviors with your background notifications.

## Examples
![Attack Drained Example](https://user-images.githubusercontent.com/1350444/221425644-0211c4d7-2838-4e63-986a-8ab313052ad5.png)
![Harvest Example](https://user-images.githubusercontent.com/1350444/221425625-4e246cb6-eff0-4f8f-855f-80fd7b36bc9d.png)

## Alert Hub
Add alerts other users have shared directly from the panel! You can check out the [alert hub branch](https://github.com/adamk33n3r/runelite-watchdog/tree/alert-hub) to learn how to upload your own.

![image](https://github.com/adamk33n3r/runelite-watchdog/assets/1350444/08ecf612-11ba-4bd1-b2c3-d624e40ca9a1)


## Capture Groups
Capture groups can make your alerts dynamic by changing the output depending on what triggered the alert.

Any trigger with a text input can use `{}` to create a capture group around the text inside it. You can then use the
captured text in your alert output by writing `$1`. Multiple brackets can be read sequentially with `$2`, `$3` etc.
This is useful when using glob [glob](https://en.wikipedia.org/wiki/Glob_(programming)), since the text that was
globbed can now be captured with `{*}`. 

Say you have a Notification Fired Alert with the Message set to `Your {*} is ready to harvest in {*}`. 
You could then have a TTS notification set to `Go get your $1 in $2!` which would make it say something like
`Go get your Ranarr in Ardougne!`.

Another useful output is as the file name for a sound notification. With the above example, we could set the alert
to play `$1.wav`, which would actually play other files in the same folder such as `ranarr.wav` and `torstol.wav`.
To set this up, you have to include and select a dummy file `$1.wav` in the folder with the files you wish to use,
even though the file itself will not be played.

With [regex](https://en.wikipedia.org/wiki/Regular_expression) enabled, you instead use parenthesis `()` to surround the text you wish to capture.

## Attribution
This project uses the [JACo MP3 Player](http://jacomp3player.sourceforge.net) to play mp3 files. Its source can be found [here](https://sourceforge.net/p/jacomp3player/code/HEAD/tree/) and is licensed under LGPL which you can find [here](./ThirdPartyLicenses.txt) or otherwise [here](https://www.gnu.org/licenses/lgpl-3.0.en.html).
