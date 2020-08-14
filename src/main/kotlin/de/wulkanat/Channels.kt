package de.wulkanat

import de.wulkanat.extensions.crosspost
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel

object Channels {
    var jda: JDA? = null
    val json = Json(JsonConfiguration.Stable)

    /**
     * List of (ServerID, ChannelID)
     */
    var channels: MutableList<DiscordChannel> = refreshFromDisk()

    fun sentToAll(messageEmbed: MessageEmbed) {
        if (jda == null)
            return

        for (channel_pair in channels) {
            val channel = jda!!.getTextChannelById(channel_pair.id) ?: continue

            if (channel_pair.mentionedRole != null) {
                val message = if (channel_pair.mentionedRole == "everyone") {
                    "New Blogpost @everyone"
                } else {
                    "New Blogpost <@&${channel_pair.mentionedRole}>"
                }
                channel.sendMessage(message).queue()
            }
            channel.sendMessage(messageEmbed).queue {
                if (channel_pair.autoPublish) {
                    it.crosspost().queue()
                }
            }
        }
    }

    fun checkEveryonePermission() {
        for (channel_pair in channels) {
            val channel = jda!!.getTextChannelById(channel_pair.id) ?: continue

            if (channel_pair.mentionedRole == "everyone" &&
                channel.guild.selfMember.hasPermission(Permission.MESSAGE_MENTION_EVERYONE)
            ) {
                Admin.warning("Cannot mention everyone on ${channel.guild.name}")
            } else if (channel.guild.selfMember.hasPermission(Permission.MESSAGE_WRITE)) {
                Admin.warning("Cannot send any messages on ${channel.guild.name}")
            }

        }
    }

    fun refreshFromDisk(): MutableList<DiscordChannel> {
        return json.parse(
            DiscordChannel.serializer().list, (if (Admin.testModeEnabled) {
                TEST_FILE
            } else {
                SERVERS_FILE
            }).readText()
        ).toMutableList()
    }

    fun getServerNames(): List<String> {
        if (jda == null)
            return listOf()

        return channels.map {
            val channel = jda!!.getTextChannelById(it.id)
            if (channel == null) {
                Admin.warning("Channel ${it.id} is no longer active!")
                return@map "**${it.id}** *(inactive)*"
            }

            val role = when (it.mentionedRole) {
                null -> ""
                "everyone" -> " @everyone"
                else -> " @${channel.guild.getRoleById(it.mentionedRole)?.name}"
            }
            "**${channel.guild.name}**\n#${channel.name}${role}"
        }
    }

    fun testServerId(id: Long): TextChannel? {
        return jda?.getTextChannelById(id)
    }

    fun addChannel(id: Long, role: String?) {
        channels.add(DiscordChannel(id, role))
        saveChannels()
    }

    private fun saveChannels() {
        SERVERS_FILE.writeText(
            json.stringify(
                DiscordChannel.serializer().list,
                channels
            )
        )
    }
}