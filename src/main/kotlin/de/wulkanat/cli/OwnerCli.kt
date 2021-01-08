@file:AutoWired

package de.wulkanat.cli

import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.primitive.BooleanArgument
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.argument.RoleArgument
import com.gitlab.kordlib.kordx.commands.kord.model.precondition.precondition
import com.gitlab.kordlib.kordx.commands.kord.model.respondEmbed
import com.gitlab.kordlib.kordx.commands.kord.module.module
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import de.wulkanat.Admin
import de.wulkanat.CustomMessage
import de.wulkanat.ServiceChannel
import de.wulkanat.files.ServiceChannels
import java.awt.Color

// TODO: channel argument?
fun ownerCommands() = module("owner-commands") {
    precondition {
        message.getAuthorAsMember()?.getPermissions()?.contains(Permission.Administrator) ?: false
    }

    command("add") {
        invoke {
            if (ServiceChannels.addChannel(channel.id.longValue, null) == null) {
                respond("Already added.")
            } else {
                respond("Added.")
                Admin.info()
            }
        }
    }

    command("remove") {
        invoke {
            val result = ServiceChannels.channels.removeAll { it.id == channel.id.longValue }
            ServiceChannels.saveChannels()
            if (result) {
                respond("Removed.")
            } else {
                respond("This channel is not registered.")
            }
        }
    }

    command("publish") {
        invoke(BooleanArgument(trueValue = "on", falseValue = "off")) { doAutoPublish ->
            ServiceChannels.channels.find { it.id == channel.id.longValue }?.also {
                it.autoPublish = doAutoPublish
                ServiceChannels.saveChannels()
            } ?: respond("Channel not registered")
        }
    }

    command("ping") {
        invoke(RoleArgument) { role ->
            ServiceChannels.channels.find { it.id == channel.id.longValue }?.also {
                // TODO: @everyone
                it.mentionedRole = role.id.longValue
                ServiceChannels.saveChannels()
            } ?: respond("Channel not registered")
        }
    }

    command("setMessage") {
        invoke(StringArgument) { message ->
            ServiceChannels.channels.find { it.id == channel.id.longValue}?.also {
                it.message = CustomMessage(message)
                respond("Set `$message` as a message.")
            } ?: respond("Channel not registered!")
        }
    }

    command("resetMessage") {
        invoke {
            ServiceChannels.channels.find { it.id == channel.id.longValue }?.also {
                it.message = null
                respond("Reset to no message")
            } ?: respond("Channel not registered!")
        }
    }

    command("serviceChannel") {
        invoke(BooleanArgument(trueValue = "add", falseValue = "remove")) { addChannel ->
            if (addChannel) {
                ServiceChannels.serviceChannels.find { it.id == channel.id.longValue }?.also {
                    respond("Already a service channel")
                } ?: run {
                    ServiceChannels.serviceChannels.add(ServiceChannel(channel.id.longValue))
                    respond("Added as a service channel")
                }
            } else {
                respond(if (ServiceChannels.serviceChannels.removeAll { it.id == channel.id.longValue })
                    "Channel removed" else "Not a service channel")
            }
        }
    }

    command("publishMessage") {
        invoke(BooleanArgument(trueValue = "on", falseValue = "off")) { doAutoPublish ->
            ServiceChannels.channels.find { it.id == channel.id.longValue }?.also {
                it.message?.pushAnnouncement = doAutoPublish
                ServiceChannels.saveChannels()
                respond("Auto publish is now ${if (doAutoPublish) "on" else "off"}")
            } ?: respond("Channel not registered!")
        }
    }

    command("info") {
        invoke {
            respondEmbed {
                title = "Server Overview"
                color = Color.GREEN
                description = """
                    ${ServiceChannels.getServerNames(guild?.id?.longValue).joinToString("\n")}
                    
                    **_Service Channels_**
                    ${ServiceChannels.getServiceChannelServers(guild?.id?.longValue).joinToString("\n")}
                """.trimIndent()
                Admin.admin?.let {
                    author {
                        name = it.username
                        icon = it.avatar.url
                        url = "https://github.com/wulkanat/BlogShot"
                    }
                }
            }
        }
    }

    command("report") {
        invoke {
            respond("What is the error you encountered?")
            val errorReport = read(StringArgument)
            respondEmbed {
                title = "Error Report Preview"
                color = Color.RED
                description = errorReport
                message.author?.let {
                    author {
                        name = it.username
                        icon = it.avatar.url
                    }
                }

            }
            respond("Send? [y/n]")
            if (read(BooleanArgument(trueValue = "y", falseValue = "n"))) {
                respond("Sent")
                Admin.error(guild?.asGuildOrNull()?.name ?: "Unknown Guild", errorReport, author)
            } else {
                respond("Aborting")
            }
        }
    }
}
