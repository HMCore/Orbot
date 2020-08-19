package de.wulkanat

import de.wulkanat.model.BlogPostPreview
import net.dv8tion.jda.api.hooks.ListenerAdapter
import de.wulkanat.web.SiteWatcher
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import kotlin.system.exitProcess

class AdminCli : ListenerAdapter() {
    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        val msg = event.message.contentRaw
        if (event.author.idLong != Admin.userId ||
            !msg.startsWith("!")
        ) {
            return
        }
        val command = Regex("[^\\s`]+|`[^`]*`").findAll(msg.removePrefix("!")).toList()

        when (command[0].value) {
            "stop" -> exitProcess(1)
            "fakeUpdate" -> {
                SiteWatcher.newestBlog = BlogPostPreview(
                    title = "FakePost",
                    imgUrl = "",
                    fullPostUrl = "",
                    author = "wulkanat",
                    date = "now",
                    description = "Lorem Ipsum"
                )

                Admin.println("Posting on next update cycle.")
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
        }
    }
}