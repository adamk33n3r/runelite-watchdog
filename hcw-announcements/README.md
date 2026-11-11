# HCW Announcements
This alert group is intended to contain Game Message alerts to trigger a runelite "collection log" style popup, and the human_death sound effect (Sound ID:512) in two scenarios:
- Game message announcement when a player's killer is known (PVE/PVP deaths)
- Game message announcement when a player's killer is unknown (environmental)

If choosing to edit either of the existing alerts, the breakdown of the capture groups from the regex used is listed below. Usage of capture groups is documented [here](https://github.com/adamk33n3r/runelite-watchdog/wiki/Capture-Groups)
### Known killer alert

- $1 - Killer
- $2 - Victim
- $3 - Total level (In the event of 1000+ total, the value is abbreviated to 1k, 1.1k, etc.)
- $4 - Damage amount
- $5 - Location

### Unknown killer alert

- $1 - Victim
- $2 - Total level (In the event of 1000+ total, the value is abbreviated to 1k, 1.1k, etc.)
- $3 - Damage amount
- $4 - Location