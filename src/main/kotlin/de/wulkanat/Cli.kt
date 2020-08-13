package de.wulkanat

import de.wulkanat.model.BlogPostPreview
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import de.wulkanat.web.SiteWatcher
import kotlin.system.exitProcess

class Cli : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val msg = event.message.contentRaw
        if (event.author.idLong != Admin.userId ||
            !msg.startsWith("%!")
        ) {
            return
        }
        val command = msg.removePrefix("%!").split(" ")

        try {
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
                "addChannel" -> {
                    val channel = command[1].toLong()
                    var role: String? = null
                    if (command.size == 3) {
                        role = command[2]
                    }
                    val serverChannel = Channels.testServerId(channel)
                    val roleName = serverChannel?.guild?.getRoleById(role ?: "")

                    if (serverChannel != null) {
                        if (roleName != null || role == null || role == "everyone") {
                            Channels.addChannel(channel, role)
                            Admin.println("Added server '${serverChannel.name}' for role '${roleName ?: role}'")
                        } else {
                            Admin.warning("Unknown Role ID")
                        }
                    } else {
                        Admin.warning("Unknown Channel ID")
                    }
                }
                "info" -> {
                    Admin.info()
                }
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            // noop
        }
    }
}