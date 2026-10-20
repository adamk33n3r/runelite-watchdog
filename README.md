![Guard_dog_resize](https://user-images.githubusercontent.com/1350444/149637084-270521ab-2d96-4c54-a7b4-71357fb6b291.png)

# Watchdog

[![Plugin Hub](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/installs/plugin/watchdog)](https://runelite.net/plugin-hub/show/watchdog)
[![Discord](https://img.shields.io/discord/1064234152314015875?color=%235865F2&label=Watchdog&logo=discord&logoColor=white&style=flat)](https://discord.gg/n8mxYAHJR9)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/T6T0JH18I)

Create custom in-game alerts triggered by specific events, each firing one or more configurable actions. For full documentation, see the **[wiki](https://github.com/adamk33n3r/runelite-watchdog/wiki)**.

## Alerts

| Alert                                                                                                 | What it watches                                    |
| ----------------------------------------------------------------------------------------------------- | -------------------------------------------------- |
| [Game Message](https://github.com/adamk33n3r/runelite-watchdog/wiki/Game-Message-Alert)               | System/server messages in the chat box             |
| [Player Chat Message](https://github.com/adamk33n3r/runelite-watchdog/wiki/Player-Chat-Message-Alert) | Messages from other players                        |
| [Overhead Text](https://github.com/adamk33n3r/runelite-watchdog/wiki/Overhead-Text-Alert)             | Text displayed above NPCs or players               |
| [Stat Changed](https://github.com/adamk33n3r/runelite-watchdog/wiki/Stat-Changed-Alert)               | Skill level changes (boosts, drains, thresholds)   |
| [XP Drop](https://github.com/adamk33n3r/runelite-watchdog/wiki/XP-Drop-Alert)                         | Receiving XP in a chosen skill                     |
| [Sound Fired](https://github.com/adamk33n3r/runelite-watchdog/wiki/Sound-Fired-Alert)                 | A specific in-game sound effect plays              |
| [Spawned Object](https://github.com/adamk33n3r/runelite-watchdog/wiki/Spawned-Alert)                  | An object, NPC, item, or player spawns or despawns |
| [Inventory](https://github.com/adamk33n3r/runelite-watchdog/wiki/Inventory-Alert)                     | Inventory full/empty, slot count, or item quantity |
| [Location](https://github.com/adamk33n3r/runelite-watchdog/wiki/Location-Alert)                       | Player is within range of a world coordinate       |
| [Notification Fired](https://github.com/adamk33n3r/runelite-watchdog/wiki/Notification-Fired-Alert)   | Any RuneLite plugin fires a system notification    |

_Many alert types support glob patterns and regex with [capture groups](https://github.com/adamk33n3r/runelite-watchdog/wiki/Capture-Groups) for dynamic action messages._

## Actions

_You can set defaults for actions in the plugin config._

### Text

| Action                                                                                                     | What it does                                     |
| ---------------------------------------------------------------------------------------------------------- | ------------------------------------------------ |
| [Game Message](https://github.com/adamk33n3r/runelite-watchdog/wiki/Game-Message-Notification)             | Writes a colored message into your chat box      |
| [Overhead Text](https://github.com/adamk33n3r/runelite-watchdog/wiki/Overhead-Notification)                | Displays text above your player character        |
| [Tray Notification](https://github.com/adamk33n3r/runelite-watchdog/wiki/Tray-Notification)                | Sends an OS desktop notification                 |
| [Plugin Message](https://github.com/adamk33n3r/runelite-watchdog/wiki/Plugin-Message-Notification)         | Sends a message event to another RuneLite plugin |
| [Notification Event](https://github.com/adamk33n3r/runelite-watchdog/wiki/Notification-Event-Notification) | Fires a RuneLite `NotificationFired` event       |

### Audio

| Action                                                                                                         | What it does                                     |
| -------------------------------------------------------------------------------------------------------------- | ------------------------------------------------ |
| [In-Game Sound Effect](https://github.com/adamk33n3r/runelite-watchdog/wiki/In-Game-Sound-Effect-Notification) | Plays a built-in OSRS sound by ID                |
| [Custom Sound](https://github.com/adamk33n3r/runelite-watchdog/wiki/Custom-Sound-Notification)                 | Plays a local `.wav` or `.mp3` file              |
| [Text to Speech](https://github.com/adamk33n3r/runelite-watchdog/wiki/Text-To-Speech-Notification)             | Synthesizes speech via Eleven Labs or legacy TTS |

### Visual

| Action                                                                                           | What it does                                                |
| ------------------------------------------------------------------------------------------------ | ----------------------------------------------------------- |
| [Screen Flash](https://github.com/adamk33n3r/runelite-watchdog/wiki/Screen-Flash-Notification)   | Flashes the game screen a custom color                      |
| [Overlay](https://github.com/adamk33n3r/runelite-watchdog/wiki/Overlay-Notification)             | Displays a text banner on screen                            |
| [Popup](https://github.com/adamk33n3r/runelite-watchdog/wiki/Popup-Notification)                 | Shows a popup styled like a collection log entry            |
| [Screen Marker](https://github.com/adamk33n3r/runelite-watchdog/wiki/Screen-Marker-Notification) | Draws a rectangle marker on a 2D screen region              |
| [Object Marker](https://github.com/adamk33n3r/runelite-watchdog/wiki/Object-Marker-Notification) | Highlights a world object or tile in-game                   |
| [Dismiss Overlay](https://github.com/adamk33n3r/runelite-watchdog/wiki/Dismiss-Notifications)    | Dismisses a sticky Overlay, Screen Marker, or Object Marker |

### Advanced

| Action                                                                                           | What it does                                       |
| ------------------------------------------------------------------------------------------------ | -------------------------------------------------- |
| [Counter](https://github.com/adamk33n3r/runelite-watchdog/wiki/Counter-Notification)             | Increments a persistent counter                    |
| [Alert Toggle](https://github.com/adamk33n3r/runelite-watchdog/wiki/Alert-Toggle-Notification)   | Enables, disables, or toggles another alert        |
| [Plugin Toggle](https://github.com/adamk33n3r/runelite-watchdog/wiki/Plugin-Toggle-Notification) | Enables, disables, or toggles a RuneLite plugin    |
| [Request Focus](https://github.com/adamk33n3r/runelite-watchdog/wiki/Request-Focus-Notification) | Brings the RuneLite window to the foreground       |
| [Dink](https://github.com/adamk33n3r/runelite-watchdog/wiki/Dink-Notification)                   | Sends a Discord webhook via the Dink plugin        |
| [Shortest Path](https://github.com/adamk33n3r/runelite-watchdog/wiki/Shortest-Path-Notification) | Sets or clears a route in the Shortest Path plugin |

## Disabled Areas

Watchdog is automatically disabled in the following areas _(updated 2026/10/19)_:

|                  |                |                                |            |
| ---------------- | -------------- | ------------------------------ | ---------- |
| Alchemical Hydra | Vardorvis      | Leviathan                      | Whisperer  |
| Sucellus         | Vorkath        | Inferno                        | Fight Cave |
| Colosseum        | Kalphite Queen | COX                            | TOB        |
| TOA              | Yama           | Delve Boss (Doom of Mokhaiotl) | Nightmare  |
| Muspah           | Maggot King    | TFA                            |            |

## Recommended RuneLite Notification Settings

![image](https://github.com/adamk33n3r/runelite-watchdog/assets/1350444/18eb10dd-9ddb-4248-9d5f-ddc335acc103)

Set **Request Focus** to `Off` to avoid incorrect behavior with background notifications.

## Examples

![Attack Drained Example](https://user-images.githubusercontent.com/1350444/221425644-0211c4d7-2838-4e63-986a-8ab313052ad5.png)
![Harvest Example](https://user-images.githubusercontent.com/1350444/221425625-4e246cb6-eff0-4f8f-855f-80fd7b36bc9d.png)

## Alert Hub

Add alerts shared by other users directly from the panel. See the [alert hub branch](https://github.com/adamk33n3r/runelite-watchdog/tree/alert-hub) to learn how to upload your own.

![image](https://github.com/adamk33n3r/runelite-watchdog/assets/1350444/08ecf612-11ba-4bd1-b2c3-d624e40ca9a1)

## More Info

- [Capture Groups](https://github.com/adamk33n3r/runelite-watchdog/wiki/Capture-Groups)
- [Eleven Labs Setup](https://github.com/adamk33n3r/runelite-watchdog/wiki/Text-To-Speech-Notification#eleven-labs-setup)

## Attribution

This project uses the [JACo MP3 Player](http://jacomp3player.sourceforge.net) to play mp3 files. Its source is available on [SourceForge](https://sourceforge.net/p/jacomp3player/code/HEAD/tree/) and is licensed under LGPL ([ThirdPartyLicenses.txt](./ThirdPartyLicenses.txt)).
