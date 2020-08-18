# BlogShot
A bot that automatically polls the newest blogpost from [Hytale News Tab](https://www.hytale.com/news) and posts a message into servers if there is a new one.
## Add to your server
Click [this](https://discord.com/api/oauth2/authorize?client_id=743447329901641799&permissions=150528&scope=bot) link to invite
the bot to your server. Please note that only people with *Administrator* permission will be able to
configure it.

You can type `%!info` to get an overview over all available commands.

## Self Hosting
Okay, this isn't really meant for you to setup, but if you *really* want to set it up yourself, fine.
* first go to the release tab, download the jar, and put it in a folder
* Add two files in the root of the repo, an `admin.json` and a `servers.json`.
Add your Discord ID (not name), Bot token, and update frequency to the `admin.json`:
```json
{
  "adminId": 12345678910,
  "token": "AOGH@(AKnjsfjiJijaig3ijgG92jaij",
  "updateMs": 30000,
  "watchingMessage": "for new Blogposts"
}
```
* add your servers to `servers.json`
```json
[
  {
    "id": 15050067772322222,
    "mentionedRole": "everyone",
    "autoPublish": true,
    "message": null
  },
  {
    "id": 74050067772325222,
    "mentionedRole": null,
    "autoPublish":false,
    "message": null
  },
  {
    "id": 74050067772325222,
    "mentionedRole": "74036067771625222",
    "autoPublish":false,
    "message": null
  }
]
```
* add a `test.json` with the same schema as the `server.json`. When
you enable test mode, the servers from there will be used instead allowing
you to test if it works.

## Compiling yourself
I developed it under Windows, and had some trouble compiling it on Linux. You mileage may vary.

## Admin commands

Start the server with `java -jar [server-file-name]` If you put in everything correctly, the bot should message you on Discord.
### Adding Servers
Please edit the JSON file.
You can force an update by calling 
```
%!refreshList
```
### Testing
Switching between test and production files
```
%!testMode
%!fakeUpdate
```
```
%!productionMode
```
**WARNING**: Initiating a fake update is not being cancelled by switching
to production.
### Stop the server from within Discord
```
%!stop
```
### Show servers, channels and roles
```
%!info
```

These commands will work in every channel, but will be ignored if they don't come from you, however the bot will always respond in a private message.
It will also print errors directly in a Discord private message.

## TODO

Mainly reaction roles for convenience, self setup on invite to server, Twitter integration.

## Other

Thanks to [Forcellrus](https://github.com/Forcellrus/Discord-Auto-Publisher) for discovering a way to auto publish messages
in news channels