# BlogShot
A bot that automatically polls the newest blogpost from [Hytale News Tab](https://www.hytale.com/news) and posts a message into servers if there is a new one.
## Add to your server
Click [this](https://discord.com/api/oauth2/authorize?client_id=743447329901641799&permissions=150528&scope=bot) link to invite
the bot to your server. Please note that only people with *Administrator* permission will be able to
configure it.

You can type `%!info` to get an overview over all available commands.
## Commands

| **Command**      | **Arguments**                                          | **Info**                                                                                          |
|------------------|--------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| %!add            |                                                        | Add current channel to the notified list                                                          |
| %!remove         |                                                        | Remove current channel to the notified list                                                       |
| %!publish        | on &#124; off                                              | [Community&#124;Partner&#124;Verified only] Auto publish the message if in an announcement channel        |
| %!ping           | none &#124; everyone &#124; roleName                           | What role to ping                                                                                 |
| %!setMessage     | message | Set a custom message when a blogpost arrives |  
| %!resetMessage   |                                                        |  Reset the custom message to none                                                                 |
| %!serviceChannel | add &#124; remove                                          | Add/remove channel from service notification list                                                 |
| %!publishMessage | on &#124; off                                              | [Community&#124;Partner&#124;Verified only] Auto publish the custom message if in an announcement channel |
| %!info           |                                                        | Show an overview about all channels registered on this server                                     |
| %!report         | Your message                                           | Report an issue to the Bot Admin (this will share your user name so they can contact you)         |
| %!help           |                                                        | Show a help dialog with all these commands                                                        |

## Self Hosting
Okay, this isn't really meant for you to setup, but if you *really* want to set it up yourself, fine.
Go to the release tab, download the jar, and put it in a folder.

Start the server with `java -jar [server-file-name]` If you put in everything correctly,
the bot should message you on Discord.

*Note:* You need to invite the bot into a server before it can message you.

Run it once (it should crash or print an error), so `admin.json`, `servers.json` and `service_channels.json`
are being created.
Add your Discord ID `adminId` (not name), Bot token `token`, and update frequency `updateMs` to the `admin.json`,
optionally you can add your own messages for when the bot is looking and when it can't reach Hytale Servers.

If you verified that everything works correctly, you can start the server in the background, on Linux that is
`nohup java -Xmx1024m -jar [server-file-name]`. To stop it you can either type `!stop` in the Admin Console (Discord PM) or
if the bot is unresponsive the the PID of it through `ps -ef` and `kill [pid]`

I'm not 100% certain how much RAM the bot needs, default is typically `-Xmx256m`, and that lead to some issues, `-Xmx512m` is probably plenty, because my server has
tons of unused ram I set it to `-Xmx2048m`, just try and look what works for you.

## Compiling yourself
I developed it under Windows, and had some trouble compiling it on Linux. You mileage may vary.

## Admin commands

| **Command**      | **Arguments**     | **Info**     |
|------------------|-------|---------------------|
| !info            |   | Show all registered channels and servers.  |
| !stop | | Stop the server (useful when running in `nohup`) |
| !serviceMessage | message | Send a service message to all registered channels |
| !fakeUpdate | | Cause a fake update (**WARNING**: This will show on **ALL** registered servers) |
| !refreshList | | Refresh servers and service channels from disk (if you manually edit the JSON files) |
| !removeInactive | | Remove inactive channels |
| !help | | Show a help dialog with all these commands |

These commands will only work by private messaging the bot (and will be ignored if they don't
come from the admin registered in the `admin.json`.

## TODO

Mainly reaction roles for convenience, Twitter integration to either be even faster or to brag how much faster
we were over the official Hytale Twitter.

## Other

Thanks to [Forcellrus](https://github.com/Forcellrus/Discord-Auto-Publisher) for discovering a way to auto publish messages
in news channels
