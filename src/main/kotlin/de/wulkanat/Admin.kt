@file:JvmName("Admin")

package de.wulkanat

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import java.awt.Color

object Admin {
    @JvmField
    val adFile = Json.decodeFromString<AdminFile>(ADMIN_FILE.readText())
    val userId: Long = adFile.adminId
    val token: String = adFile.token
    val updateMs: Long = adFile.updateMs
    val message: String = adFile.watchingMessage
    val offlineMessage: String = adFile.offlineMessage

    fun connectToUser() {
        Main.jdas.forEach {
            if(admin != null) return@forEach
            admin = it.retrieveUserById(userId).complete()
        }
        if (admin == null) {
            kotlin.io.println("Connection to de.wulkanat.Admin failed!")
        } else {
            kotlin.io.println("Connected to ${admin!!.name}. No further errors will be printed here.")
        }
    }

    var admin: User? = null

    fun println(msg: String) {
        sendDevMessage(
            EmbedBuilder()
                .setTitle(msg)
                .setColor(Color.WHITE)
                .build(),
            msg
        )
    }

    fun printlnBlocking(msg: String) {
        senDevMessageBlocking(
            EmbedBuilder()
                .setTitle(msg)
                .setColor(Color.WHITE)
                .build(),
            msg
        )
    }

    fun error(msg: String, error: String, author: User? = null) {
        sendDevMessage(
            EmbedBuilder()
                .setTitle(msg)
                .setDescription(error)
                .setColor(Color.RED)
                .run {
                    if (author == null) {
                        this
                    } else {
                        this.setAuthor(author.asTag, author.avatarUrl, author.avatarUrl)
                    }
                }
                .build(), "$msg\n\n${error}"
        )
    }

    fun errorBlocking(msg: String, error: Exception) {
        senDevMessageBlocking(
            EmbedBuilder()
                .setTitle(msg)
                .setDescription(error.message)
                .setColor(Color.RED)
                .build(), "$msg\n\n${error.message}"
        )
    }

    fun warning(msg: String) {
        sendDevMessage(
            EmbedBuilder()
                .setTitle(msg)
                .setColor(Color.YELLOW)
                .build(),
            msg
        )
    }

    fun info() {
        sendDevMessage(
            EmbedBuilder()
                .setTitle("Now watching for new Hytale Blogposts every ${updateMs / 1000}s")
                .setDescription(
                    """
                    ${Channels.getServerNames().joinToString("\n")}
                    
                    **_Service Channels_**
                    ${Channels.getServiceChannelServers().joinToString("\n")}
                """.trimIndent()
                )
                .setColor(Color.GREEN)
                .build(),
            "Now watching for new Hytale BlogPosts"
        )
    }

    fun silent(msg: String) {
        kotlin.io.println(msg)
    }

    private fun senDevMessageBlocking(messageEmbed: MessageEmbed, fallback: String) {
        val devChannel = admin?.openPrivateChannel() ?: kotlin.run {
            kotlin.io.println(fallback)
            return
        }

        devChannel.complete()
            .sendMessage(messageEmbed).complete()
    }

    fun sendDevMessage(messageEmbed: MessageEmbed, fallback: String) {
        kotlin.io.println(admin!!.id)

        val devChannel = admin?.openPrivateChannel() ?: kotlin.run {
            kotlin.io.println(fallback)
            return
        }

        devChannel.queue {
            it.sendMessage(messageEmbed).queue()
        }
    }
}