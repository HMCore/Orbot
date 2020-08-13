# BlogShot
A bot that automatically polls the newest blogpost from [Hytale News Tab](https://www.hytale.com/news) and posts a message into servers if there is a new one.
## Setup
Okay, this isn't really meant for you to setup, if you want it though it first is easier to just dm me on Twitter [@tale_talk](https://twitter.com/tale_talk) so I can add you to the server list.
If you *really* want to set it up yourself, fine.
1. first clone the repo, build it, etc.
2. Add two files in the root of the repo, an `admin.json` and a `servers.json`.
Add your Discord ID (not name), Bot token, and update frequency to the `admin.json`:
```json
{"adminId": 12345678910,"token": "AOGH@(AKnjsfjiJijaig3ijgG92jaij","updateMs":30000}
```
3. add an empty array to your `servers.json`
```json
[]
```

Not sure, but it might be that multiline JSON doesn't work.

Start the server. If you put in everything correctly, the bot should message you on Discord.
### Adding Servers
```
%!addChannel [channelID] [roleID/everyone]
```
Second argument is optional.
### Cause a fake update (test if it works)
```
%!fakeUpdate
```
### Stop the server from within Discord
```
%!stop
```


These commands will work in every channel, but will be ignored if they don't come from you, however the bot will always respond in a private message.
It will also print errors directly in a Discord private message.
