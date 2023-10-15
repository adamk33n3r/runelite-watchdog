# Example Alert
This is an example readme for this alert. This folder will be linked to on the alert hub item in the panel so you can add more explanations and photos etc here.

## Structure of the `alert.json`
| Property | Description | Example |
| -------- | ----------- | ------- |
| displayName | The name that will show up in the alert hub. | `"Example Alert Hub Alert"` |
| description | "The description that will show up in the alert hub. | `"This is an example alert for the alert hub."` |
| compatibleVersion | The last version this alert was tested with. | `"3.0.0"` |
| author | It you. | "adamk33n3r" |
| category | The category to put the alert in. Shows a related icon. | <ul><li>`COMBAT`</li><li>`SKILLING`</li><li>`BOSSES`</li><li>`AFK`</li><li>`MISC`</li></ul> |
| tags | A list of strings that will be used as search keywords. | `[ "farming", "plot", "harvest" ]`
| alert | The exported json of your alert. This must be a single `Alert` or `AlertGroup`. | See below

## Example `alert.json`
```json
{
  "displayName": "Example Alert Hub Alert",
  "description": "This is an example alert for the alert hub.",
  "compatibleVersion": "3.0.0",
  "author": "adamk33n3r",
  "category": "SKILLING",
  "tags": [ "farming", "plot", "harvest" ],
  "alert": {"type":"ChatAlert","message":"*is ready to harvest*","regexEnabled":false,"enabled":true,"name":"Ready to Harvest","debounceTime":500,"notifications":[{"type":"TrayNotification","message":"Time to harvest your crops!","fireWhenFocused":true},{"type":"Sound","path":"airplane_seatbelt.mp3","gain":8,"fireWhenFocused":true}]}
}
```
