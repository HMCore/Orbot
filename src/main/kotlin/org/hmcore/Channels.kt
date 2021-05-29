@file:JvmName("Channels")

package org.hmcore

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import org.hmcore.extensions.toWebhook
import java.awt.Color

object Channels {
    /**
     * List of (ServerID, ChannelID)
     */
    var channels: MutableList<DiscordChannel> = refreshChannelsFromDisk()
    var serviceChannels: MutableList<ServiceChannel> = refreshServiceChannelsFromDisk()

    fun sentToAll(messageEmbed: Message) {
        messageEmbed.toWebhook().send(WEBHOOKS.blogPostsWebhookUrl)

        Main.jdas.forEach { jda ->
            for (channel_pair in channels) {
                try {
                    val channel = jda.getTextChannelById(channel_pair.id) ?: continue
                    val customMessage = channel_pair.message?.message ?: ""

                    if (channel_pair.mentionedRole != null) {
                        val message = if (channel_pair.mentionedRole == "everyone") {
                            "@everyone $customMessage"
                        } else {
                            "<@&${channel_pair.mentionedRole}> $customMessage"
                        }
                        channel.sendMessage(message).queue {
                            if (channel_pair.message?.pushAnnouncement == true) {
                                it.crosspost().queue()
                            }
                        }
                    } else if (channel_pair.message != null) {
                        channel.sendMessage(customMessage).queue {
                            if (channel_pair.message?.pushAnnouncement == true) {
                                it.crosspost().queue()
                            }
                        }
                    }
                    channel.sendMessage(messageEmbed).queue {
                        if (channel_pair.autoPublish) {
                            it.crosspost().queue()
                        }
                    }
                } catch (e: Exception) {
                    Admin.error("Error in server ${channel_pair.id}", e.message ?: e.localizedMessage)
                }
            }
        }
    }

    fun sendServiceMessage(title: String, message: String) {
        val serviceMessage = EmbedBuilder()
            .setTitle(title)
            .setDescription(message)
            .setColor(Color.WHITE)
            .setAuthor(Admin.admin?.name, Admin.admin?.avatarUrl, Admin.admin?.avatarUrl)
            .setFooter("This was sent by a human.")
            .build()

        for (channelInfo in serviceChannels) {
            Main.jdas.forEach {
                val channel = it.getTextChannelById(channelInfo.id)

                channel?.sendMessage(serviceMessage)?.queue()
            }

        }

        Admin.println("Service message distributed to ${serviceChannels.size} channels.")
        Admin.sendDevMessage(
            serviceMessage, """
            ***************
            SERVICE MESSAGE
            
            $title
            -------
            $message
            ***************
        """.trimIndent()
        )
    }

    fun checkEveryonePermission() {
        Main.jdas.forEach {
            for (channel_pair in channels) {
                val channel = it.getTextChannelById(channel_pair.id) ?: continue

                if (channel_pair.mentionedRole == "everyone" &&
                    channel.guild.selfMember.hasPermission(Permission.MESSAGE_MENTION_EVERYONE)
                ) {
                    Admin.warning("Cannot mention everyone on ${channel.guild.name}")
                } else if (channel.guild.selfMember.hasPermission(Permission.MESSAGE_WRITE)) {
                    Admin.warning("Cannot send any messages on ${channel.guild.name}")
                }
            }

        }
    }

    fun refreshChannelsFromDisk() =
        Json.decodeFromString<List<DiscordChannel>>(SERVERS_FILE.readText()).toMutableList()

    fun refreshServiceChannelsFromDisk() =
        Json.decodeFromString<List<ServiceChannel>>(SERVICE_CHANNELS_FILE.readText()).toMutableList()

    fun getServerNames(server: Long? = null) = Main.jdas.flatMap { jda ->
        channels.filter { server == null || (jda.getTextChannelById(it.id)?.guild?.idLong == server) }.map {
            val channel = jda.getTextChannelById(it.id)
            if (channel == null) {
                Admin.warning("Channel ${it.id} is no longer active!")
                return@map "**${it.id}** *(inactive)*"
            }

            val role = when (it.mentionedRole) {
                null -> ""
                "everyone" -> " @everyone"
                else -> " @${channel.guild.getRoleById(it.mentionedRole ?: "")?.name}"
            }
            val publish = if (it.autoPublish) " (publish)" else ""
            "**${channel.guild.name}** #${channel.name}${role}${publish}${
                if (it.message == null) {
                    ""
                } else {
                    "\n*${it.message!!.message}*${if (it.message!!.pushAnnouncement) " (publish)" else ""}"
                }
            }"
        }
    }

    fun getServiceChannelServers(server: Long? = null): List<String> {

        return Main.jdas.flatMap { jda ->
            serviceChannels.filter { server == null || (jda.getTextChannelById(it.id)?.guild?.idLong == server) }
                .map {
                    val channel = jda.getTextChannelById(it.id)
                    "**${channel?.guild?.name ?: it.id}** #${channel?.name ?: "(inactive)"}"
                }
        }
    }

    fun testServerId(id: Long) =
        Main.jdas.map { it.getTextChannelById(id) }.firstOrNull()

    fun addChannel(id: Long, role: String?): DiscordChannel? {
        if (channels.find { it.id == id } != null) {
            return null
        }
        val out = DiscordChannel(id, role)
        channels.add(out)
        saveChannels()
        return out
    }

    fun saveChannels() {
        SERVERS_FILE.writeText(Json.encodeToString(channels))
        SERVICE_CHANNELS_FILE.writeText(Json.encodeToString(serviceChannels))
    }
}