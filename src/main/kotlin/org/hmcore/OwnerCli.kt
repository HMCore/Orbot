package org.hmcore

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class OwnerCli : ListenerAdapter() {
    private val prefix = "%!"

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val msg = event.message.contentRaw
        // Only accept admin requests
        if (event.message.member?.hasPermission(Permission.ADMINISTRATOR) != true || !msg.startsWith(prefix)) {
            return
        }

        val command = msg.removePrefix(prefix).split(Regex("\\s+"))
        val channelId = event.message.channel.idLong
        val msgType: MessageType

        if(command.size < 2)
            msgType = MessageType.INVALID
        else msgType = when (command[1].lowercase()) {
                "blogpost" -> MessageType.BLOGPOST
                "twitter" -> MessageType.TWITTER
                "job" -> MessageType.JOB_LISTING
                "website" -> MessageType.WEBSITE_CHANGED
                else -> MessageType.INVALID
            }

        when (command.first()) {
            "categories" -> {
                event.message.channel.sendMessage(EmbedBuilder()
                    .setTitle("Categories")
                    .setColor(Color.YELLOW)
                    .setAuthor(Admin.admin?.name, Admin.admin?.avatarUrl, Admin.admin?.avatarUrl)
                    .setDescription("Valid Categories:\n" +
                            "   Blogpost\n" +
                            "   Twitter\n" +
                            "   Job - (changes of Job listings)\n" +
                            "   Website - (if the content of some website or subdomain thats owned by hypixel studios gets changed) - soon\n")
                    .build()).queue()
                return
            }
            "add" -> {
                if (msgType == MessageType.INVALID) {
                    event.message.channel.sendMessage("Please choose a valid category. List valid categories with ${prefix}categories").queue()
                    return
                }
                val result = Channels.addChannel(channelId, msgType)
                if (result == null) {
                    event.message.channel.sendMessage("Already added.").queue()
                } else {
                    event.message.channel.sendMessage("Added.").queue()
                    Admin.info()
                }
            }
            "remove" -> {
                if (msgType == MessageType.INVALID) {
                    event.message.channel.sendMessage("Please choose a valid category. List valid categories with ${prefix}categories").queue()
                }
                val result = Channels.channels.removeAll { it.id == channelId && (it.type == msgType || it.type == MessageType.INVALID) }
                Channels.saveChannels()
                if (result) {
                    event.message.channel.sendMessage("Removed.").queue()
                } else {
                    event.message.channel.sendMessage("This channel is not registered.").queue()
                }
            }
            "publish" -> {
                if (msgType == MessageType.INVALID) {
                    event.message.channel.sendMessage("Please choose a valid category. List valid categories with ${prefix}categories").queue()
                }
                val result = Channels.channels.find { it.id == channelId && it.type == msgType}
                if (result != null) {
                    if (command.size > 2 && listOf("on", "off").contains(command[2])) {
                        result.autoPublish = command[2] == "on"
                        Channels.saveChannels()

                        event.message.channel.sendMessage("Auto publish is now ${command[1]}").queue()
                    } else {
                        event.message.channel.sendMessage("Usage: `${prefix}publish [type] [on|off]`")
                    }
                } else {
                    event.message.channel.sendMessage("Channel not registered.").queue()
                }
            }
            "ping" -> {
                if (msgType == MessageType.INVALID) {
                    event.message.channel.sendMessage("Please choose a valid category. List valid categories with ${prefix}categories").queue()
                }
                val result = Channels.channels.find { it.id == channelId && it.type == msgType}
                if (result != null) {
                    if (command.size > 2) {
                        val roles = event.message.guild.getRolesByName(command[2], false)
                        result.mentionedRole = when {
                            command[2] == "everyone" -> {
                                event.message.channel.sendMessage("Now pinging everyone.").queue()
                                "everyone"
                            }
                            command[2] == "none" -> {
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
                        event.message.channel.sendMessage("Usage: `${prefix}ping [type] [everyone|none|roleName]`")
                    }
                } else {
                    event.message.channel.sendMessage("Channel is not registered.").queue()
                }
            }
            "setMessage" -> {
                if (msgType == MessageType.INVALID) {
                    event.message.channel.sendMessage("Please choose a valid category. List valid categories with ${prefix}categories").queue()
                }
                val result = Channels.channels.find { it.id == channelId && it.type == msgType }
                if (result != null) {
                    if (command.size > 2) {
                        val message = command.subList(2, command.size).toString().trim()
                        result.message = CustomMessage(message)
                        Channels.saveChannels()
                        event.message.channel.sendMessage("Set `$message` as message.").queue()
                    } else {
                        event.message.channel.sendMessage("Usage: `${prefix}setMessage [type] [message]`")
                    }
                } else {
                    event.message.channel.sendMessage("Channel is not registered.").queue()
                }
            }
            "resetMessage" -> {
                if (msgType == MessageType.INVALID) {
                    event.message.channel.sendMessage("Please choose a valid category. List valid categories with ${prefix}categories").queue()
                }
                val result = Channels.channels.find { it.id == channelId && it.type == msgType }
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
                if (msgType == MessageType.INVALID) {
                    event.message.channel.sendMessage("Please choose a valid category. List valid categories with ${prefix}categories").queue()
                }
                val result = Channels.channels.find { it.id == channelId && it.type == msgType }
                if (result != null) {
                    if (result.message != null) {
                        if (command.size > 2 && listOf("on", "off").contains(command[2])) {
                            result.message?.pushAnnouncement = command[2] == "on"
                            Channels.saveChannels()

                            event.message.channel.sendMessage("Auto publish (message) is now ${command[1]}").queue()
                        } else {
                            event.message.channel.sendMessage("Usage: `${prefix}publishMessage [type] [on|off]`")
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
                            **${prefix}add [type]**
                            Add this channel to the notified list
                            **${prefix}serviceChannel [add|remove]**
                            Add or remove this channel to receive service message from the bot developer (recommended)
                            **${prefix}remove [type]**
                            Remove this channel to the notified list
                            **${prefix}publish [type] [on|off]**
                            [Community|Partner|Verified only] Auto publish the message if in an announcement channel
                            **${prefix}ping [type] [none|everyone|roleName]**
                            What role to ping
                            **${prefix}setMessage [type] [message]**
                            Set a custom message to show
                            **${prefix}resetMessag [type]e**
                            Reset the message
                            **${prefix}info**
                            Show an overview about all channels registered on this server
                            **${prefix}categories**
                            Show a list of categories available for alert types
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
    }
}