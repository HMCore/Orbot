package org.hmcore

import org.hmcore.web.fakeUpdateBlogPost
import org.hmcore.web.fakeUpdateJobListings
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import java.awt.Color
import kotlin.system.exitProcess

class AdminCli : ListenerAdapter() {
    val prefix = "!"

    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        val msg = event.message.contentRaw
        if (event.author.idLong != Admin.userId ||
            !msg.startsWith(prefix)
        ) {
            return
        }
        val command = Regex("[^\\s`]+|`[^`]*`").findAll(msg.removePrefix("!")).toList()

        when (command[0].value) {
            "stop" -> exitProcess(1)
            "fakeUpdate" -> {
                if (command.size < 2) {
                    Admin.println("Specify type")
                } else {
                    val amount = command.getOrNull(2)?.value?.trim('`')?.toIntOrNull() ?: 1
                    when (command[1].value.trim('`')) {
                        "blog" -> fakeUpdateBlogPost(amount)
                        "twitter" -> TwitterJob.lastTweetID = "poggers"
                        "jobs" -> fakeUpdateJobListings(amount)
                        else -> Admin.println("Must be blog|twitter|jobs")
                    }

                    Admin.println("Posting $amount on next update cycle.")
                }
            }
            "info" -> {
                Admin.info()
            }
            "serviceMessage" -> {
                if (command.size != 3) {
                    Admin.println("Enclose message and title in backticks (`)")
                } else {
                    Channels.sendServiceMessage(command[1].value.trim('`'), command[2].value.trim('`'))
                }
            }
            "refreshList" -> {
                Channels.channels = Channels.refreshChannelsFromDisk()
                Channels.serviceChannels = Channels.refreshServiceChannelsFromDisk()
                Admin.info()
            }
            "removeInactive" -> {
                Channels.channels.removeAll { channel ->
                    Channels.testServerId(channel.id) ?: run {
                        Admin.println("Removed ${channel.id}")
                        null
                    } == null
                }
                Admin.info()
                Channels.saveChannels()
            }
            "help" -> {
                event.message.channel.sendMessage(
                    EmbedBuilder()
                        .setTitle("Help")
                        .setColor(Color.YELLOW)
                        .setAuthor(Admin.admin?.name, Admin.admin?.avatarUrl, Admin.admin?.avatarUrl)
                        .setDescription(
                            """
                            **${prefix}stop**
                            Stop the bot
                            **${prefix}fakeUpdate [blog|twitter|jobs] [amount]**
                            Post a fake update to every registered channel (can be used if bot missed the update)
                            **${prefix}info**
                            Show an overview over all registered channels
                            **${prefix}serviceMessage [title] [message]**
                            Show a service message (update info etc) to all registered service channels
                            **${prefix}refreshList**
                            Refresh server list from disk
                            **${prefix}removeInactive**
                            Remove inactive channels
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