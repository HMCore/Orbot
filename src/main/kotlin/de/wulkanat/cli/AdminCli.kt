@file:AutoWired

package de.wulkanat.cli

import com.gitlab.kordlib.core.entity.channel.DmChannel
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.primitive.BooleanArgument
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.model.precondition.precondition
import com.gitlab.kordlib.kordx.commands.kord.model.prefix.kord
import com.gitlab.kordlib.kordx.commands.kord.model.prefix.mention
import com.gitlab.kordlib.kordx.commands.kord.model.respondEmbed
import com.gitlab.kordlib.kordx.commands.kord.module.module
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import com.gitlab.kordlib.kordx.commands.model.prefix.literal
import com.gitlab.kordlib.kordx.commands.model.prefix.or
import com.gitlab.kordlib.kordx.commands.model.prefix.prefix
import de.wulkanat.Admin
import de.wulkanat.extensions.alsoIf
import de.wulkanat.extensions.isBotAdmin
import de.wulkanat.files.ServiceChannels
import de.wulkanat.model.BlogPostPreview
import de.wulkanat.web.SiteWatcher

val prefixes = prefix {
    kord { mention() or literal("%!") }
}

fun adminCommands() = module("admin-commands") {
    precondition { author.isBotAdmin && channel.asChannelOrNull() is DmChannel }

    command("stop") {
        invoke {
            respond("Shutting down...")
            kord.shutdown()
        }
    }

    command("info") {
        invoke {
            Admin.info()
        }
    }

    command("fakeUpdate") {
        invoke {
            respond("THIS WILL CAUSE A MESSAGE ON **ALL** SERVERS.\nContinue? [y/n]")
            if (read(BooleanArgument(trueValue = "y", falseValue = "n"))) {
                respond("Sending fake update on next cycle")

                SiteWatcher.newestBlog = BlogPostPreview(
                    title = "FakePost",
                    imgUrl = "",
                    fullPostUrl = "",
                    author = "wulkanat",
                    date = "now",
                    description = "Lorem Ipsum"
                )
            } else {
                respond("Aborting")
            }
        }
    }

    command("serviceMessage") {
        invoke {
            respond("What's the title?")
            val title = read(StringArgument)
            respond("What's the message?")
            val message = read(StringArgument)
            respondEmbed {
                this.title = title
                description = message
                footer {
                    text = "Is that correct? [y/n]"
                }
            }
            if (read(BooleanArgument(trueValue = "y", falseValue = "n"))) {
                respond("Sending")
                ServiceChannels.sendServiceMessage(title, message)
            } else {
                respond("Aborting")
            }
        }
    }

    command("refreshList") {
        invoke {
            ServiceChannels.channels = ServiceChannels.refreshChannelsFromDisk()
            ServiceChannels.serviceChannels = ServiceChannels.refreshServiceChannelsFromDisk()
            Admin.info()
        }
    }

    command("removeInactive") {
        invoke {
            respondEmbed {
                title = "Channels removed"

                ServiceChannels.channels.removeAll { channel ->
                    (ServiceChannels.testServerId(channel.id) == null).alsoIf(true) {
                        field { name = channel.id.toString() }
                    }
                }
            }
            ServiceChannels.saveChannels()
        }
    }
}
