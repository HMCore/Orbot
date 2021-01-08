package de.wulkanat

import de.wulkanat.cli.Cli
import de.wulkanat.cli.makeCli
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class OwnerCli : ListenerAdapter() {
    private val cli: Cli<PrivateMessageReceivedEvent> = makeCli(prefix = "!") {
        command name "add" does "Add this channel to the notified list" through ::OwnerCliStuff.addChannel
        command name "remove" does "Remove this channel to the notified list" through removeChannel
        command name "publish" with { required literal argument with "on" or "off" } does
                "[Community|Partner|Verified only] Auto publish the message if in an announcement channel" through publish
        command name "ping" with { required string argument } does "What role to ping" through ping
        command name "setMessage" with { required string argument } does "Set a custom message to show" through setMessage

    }
}

object OwnerCliStuff {
    private fun addChannel(_required: List<String>, _optional: MutableMap<String, String>, event: PrivateMessageReceivedEvent) {
        val result = Channels.addChannel(event.channel.idLong, null)
        if (result == null) {
            event.message.channel.sendMessage("Already added.").queue()
        } else {
            event.message.channel.sendMessage("Added.").queue()
            Admin.info()
        }
    }

    private val removeChannel =
        { _: List<String>, _: MutableMap<String, String>, event: PrivateMessageReceivedEvent ->
            val result = Channels.channels.removeAll { it.id == event.channel.idLong }
            Channels.saveChannels()
            if (result) {
                event.message.channel.sendMessage("Removed.").queue()
            } else {
                event.message.channel.sendMessage("This channel is not registered.").queue()
            }
        }

    private val publish =
        publish@{ required: List<String>, _: MutableMap<String, String>, event: PrivateMessageReceivedEvent ->
            val channel = Channels.channels.find { it.id == event.channel.idLong } ?: run {
                event.message.channel.sendMessage("Channel not registered.").queue()
                return@publish
            }

            channel.autoPublish = required.first() == "on"
            Channels.saveChannels()

            event.message.channel.sendMessage("Auto publish is now ${required.first()}").queue()
        }

    private val ping =
        ping@{ required: List<String>, _: MutableMap<String, String>, event: PrivateMessageReceivedEvent ->
            val channel = Channels.channels.find { it.id == event.channel.idLong } ?: run {
                event.message.channel.sendMessage("Channel is not registered.").queue()
                return@ping
            }

            val roleName = required.first()
            val role = event.message.guild.getRolesByName(required.first(), false).firstOrNull()

            channel.mentionedRole = when {
                roleName == "everyone" -> {
                    event.message.channel.sendMessage("Now pinging $roleName.").queue()
                    roleName
                }
                roleName == "none" -> {
                    event.message.channel.sendMessage("Now pinging $roleName.").queue()
                    null
                }
                role != null -> {
                    event.message.channel.sendMessage("Now pinging ${role.name}").queue()
                    role.id
                }
                else -> {
                    event.message.channel.sendMessage("Unknown role.").queue()
                    channel.mentionedRole
                }
            }
            Channels.saveChannels()
        }

    private val setMessage =
        setMessage@{ required: List<String>, _: MutableMap<String, String>, event: PrivateMessageReceivedEvent ->
            val result = Channels.channels.find { it.id == event.channel.channelId } ?: run {
                event.message.channel.sendMessage("Channel is not registered.").queue()
                return@setMessage
            }

            val message = required.first()
            result.message = CustomMessage(message)
            Channels.saveChannels()
            event.message.channel.sendMessage("Set `$message` as message.").queue()
        }
}

/*class OwnerCli2 : ListenerAdapter() {
    private val prefix = "%!"

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val msg = event.message.contentRaw
        // Only accept admin requests
        if (event.message.member?.hasPermission(Permission.ADMINISTRATOR) != true || !msg.startsWith(prefix)) {
            return
        }

        val command = msg.removePrefix(prefix).split(Regex("\\s+"))
        val channelId = event.message.channel.idLong

        when (command.first()) {
            "add" -> {
                val result = Channels.addChannel(channelId, null)
                if (result == null) {
                    event.message.channel.sendMessage("Already added.").queue()
                } else {
                    event.message.channel.sendMessage("Added.").queue()
                    Admin.info()
                }
            }
            "remove" -> {
                val result = Channels.channels.removeAll { it.id == channelId }
                Channels.saveChannels()
                if (result) {
                    event.message.channel.sendMessage("Removed.").queue()
                } else {
                    event.message.channel.sendMessage("This channel is not registered.").queue()
                }
            }
            "publish" -> {
                val result = Channels.channels.find { it.id == channelId }
                if (result != null) {
                    if (command.size > 1 && listOf("on", "off").contains(command[1])) {
                        result.autoPublish = command[1] == "on"
                        Channels.saveChannels()

                        event.message.channel.sendMessage("Auto publish is now ${command[1]}").queue()
                    } else {
                        event.message.channel.sendMessage("Usage: `${prefix}publish [on|off]`")
                    }
                } else {
                    event.message.channel.sendMessage("Channel not registered.").queue()
                }
            }
            "ping" -> {
                val result = Channels.channels.find { it.id == channelId }
                if (result != null) {
                    if (command.size > 1) {
                        val roles = event.message.guild.getRolesByName(command[1], false)
                        result.mentionedRole = when {
                            command[1] == "everyone" -> {
                                event.message.channel.sendMessage("Now pinging everyone.").queue()
                                "everyone"
                            }
                            command[1] == "none" -> {
                                event.message.channel.sendMessage("Now pinging none.").queue()
                                null
                            }
                            roles.firstOrNull() != null -> {
                                event.message.channel.sendMessage("Now pinging ${roles.first().name}").queue()
                                roles.first().id
                            }
                            else -> {
                                event.message.channel.sendMessage("Unknown role.").queue()
                                result.mentionedRole
                            }
                        }
                        Channels.saveChannels()
                    } else {
                        event.message.channel.sendMessage("Usage: `${prefix}ping [everyone|none|roleName]`")
                    }
                } else {
                    event.message.channel.sendMessage("Channel is not registered.").queue()
                }
            }
            "setMessage" -> {
                val result = Channels.channels.find { it.id == channelId }
                if (result != null) {
                    if (command.size > 1) {
                        val message = event.message.contentRaw.removePrefix("${prefix}setMessage").trim()
                        result.message = CustomMessage(message)
                        Channels.saveChannels()
                        event.message.channel.sendMessage("Set `$message` as message.").queue()
                    } else {
                        event.message.channel.sendMessage("Usage: `${prefix}setMessage [message]`")
                    }
                } else {
                    event.message.channel.sendMessage("Channel is not registered.").queue()
                }
            }
            "resetMessage" -> {
                val result = Channels.channels.find { it.id == channelId }
                if (result != null) {
                    result.message = null
                    Channels.saveChannels()
                    event.message.channel.sendMessage("Reset to no message.").queue()
                } else {
                    event.message.channel.sendMessage("Channel is not registered.").queue()
                }
            }
            "serviceChannel" -> {
                if (command.size > 1 && listOf("add", "remove").contains(command[1])) {
                    if (command[1] == "add") {
                        if (Channels.serviceChannels.find { it.id == channelId } != null) {
                            event.message.channel.sendMessage("Already a service channel.").queue()
                        } else {
                            Channels.serviceChannels.add(ServiceChannel(channelId))
                            Channels.saveChannels()
                            event.message.channel.sendMessage("Added as service channel.").queue()
                        }
                    } else {
                        event.message.channel.sendMessage(
                            if (Channels.serviceChannels.removeAll { it.id == channelId }) "Channel removed."
                            else "Not a service channel."
                        ).queue()
                    }
                    Channels.saveChannels()
                } else {
                    event.message.channel.sendMessage("Usage: `${prefix}serviceChannel [add|remove]`")
                }

            }
            "publishMessage" -> {
                val result = Channels.channels.find { it.id == channelId }
                if (result != null) {
                    if (result.message != null) {
                        if (command.size > 1 && listOf("on", "off").contains(command[1])) {
                            result.message?.pushAnnouncement = command[1] == "on"
                            Channels.saveChannels()

                            event.message.channel.sendMessage("Auto publish (message) is now ${command[1]}").queue()
                        } else {
                            event.message.channel.sendMessage("Usage: `${prefix}publishMessage [on|off]`")
                        }
                    } else {
                        event.message.channel.sendMessage("Channel has no custom message.").queue()
                    }
                } else {
                    event.message.channel.sendMessage("Channel not registered.").queue()
                }
            }
            "info" -> {
                event.message.channel.sendMessage(
                    EmbedBuilder()
                        .setTitle("Server overview")
                        .setColor(Color.GREEN)
                        .setDescription("""
                            ${Channels.getServerNames(event.message.guild.idLong).joinToString("\n")}
                                                
                            **_Service Channels_**
                            ${Channels.getServiceChannelServers(event.message.guild.idLong).joinToString("\n")}
                        """.trimIndent())
                        .setAuthor(Admin.admin?.name, Admin.admin?.avatarUrl, Admin.admin?.avatarUrl)
                        .build()
                ).queue()
            }
            "report" -> {
                val errorReport = event.message.contentRaw.removePrefix("${prefix}report")
                Admin.error(event.message.guild.name, errorReport, event.author)
                event.message.channel.sendMessage(
                    EmbedBuilder()
                        .setTitle("Error Report Received")
                        .setColor(Color.RED)
                        .setDescription(errorReport)
                        .setAuthor(Admin.admin?.name, Admin.admin?.avatarUrl, Admin.admin?.avatarUrl)
                        .build()
                ).queue()
            }
            "help" -> {
                event.message.channel.sendMessage(
                    EmbedBuilder()
                        .setTitle("Help")
                        .setColor(Color.YELLOW)
                        .setAuthor(Admin.admin?.name, Admin.admin?.avatarUrl, Admin.admin?.avatarUrl)
                        .setDescription(
                            """
                            **${prefix}add**
                            Add this channel to the notified list
                            **${prefix}serviceChannel [add|remove]**
                            Add or remove this channel to receive service message from the bot developer (recommended)
                            **${prefix}remove**
                            Remove this channel to the notified list
                            **${prefix}publish [on|off]**
                            [Community|Partner|Verified only] Auto publish the message if in an announcement channel
                            **${prefix}ping [none|everyone|roleName]**
                            What role to ping
                            **${prefix}setMessage [message]**
                            Set a custom message to show
                            **${prefix}resetMessage**
                            Reset the message
                            **${prefix}info**
                            Show an overview about all channels registered on this server
                            **${prefix}report**
                            Report an issue to the Bot Admin (this will share your user name so they can contact you)
                            **${prefix}help**
                            Show this message
                        """.trimIndent()
                        )
                        .build()
                ).queue()
            }
        }
    }*
}*/