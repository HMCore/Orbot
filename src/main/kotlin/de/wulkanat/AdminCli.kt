package de.wulkanat

import de.wulkanat.model.BlogPostPreview
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import de.wulkanat.web.SiteWatcher
import net.dv8tion.jda.api.events.ExceptionEvent
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
        val command = msg.removePrefix("!").split(Regex("\\s+"))

        when (command[0]) {
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
            "refreshList" -> {
                Channels.channels = Channels.refreshFromDisk()
                Admin.info()
            }
            "testMode" -> {
                Admin.testModeEnabled = true
            }
            "productionMode" -> {
                Admin.testModeEnabled = false
            }
        }
    }
}