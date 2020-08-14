package de.wulkanat

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import java.sql.Time
import java.util.concurrent.TimeUnit

object Admin {
    val userId: Long
    val token: String
    val updateMs: Long

    init {
        val admin = Json(JsonConfiguration.Stable).parse(AdminFile.serializer(), ADMIN_FILE.readText())
        userId = admin.adminId
        token = admin.token
        updateMs = admin.updateMs
    }

    var jda: JDA? = null
    set(value) {
        field = value

        admin = value?.retrieveUserById(userId)?.complete()
        if (admin == null) {
            kotlin.io.println("Connection to de.wulkanat.Admin failed!")
        } else {
            kotlin.io.println("Connected to ${admin!!.name}. No further errors will be printed here.")
        }
    }
    private var admin: User? = null

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

    fun error(msg: String, error: Exception) {
        sendDevMessage(
            EmbedBuilder()
                .setTitle(msg)
                .setDescription(error.message)
                .setColor(Color.RED)
                .build()
            , "$msg\n\n${error.message}"
        )
    }

    fun errorBlocking(msg: String, error: Exception) {
        senDevMessageBlocking(
            EmbedBuilder()
                .setTitle(msg)
                .setDescription(error.message)
                .setColor(Color.RED)
                .build()
            , "$msg\n\n${error.message}"
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
                .setDescription(Channels.getServerNames().joinToString("\n"))
                .setColor(Color.GREEN)
                .build(),
            "Now watching for new Hytale BlogPosts"
        )
    }

    fun silent(msg: String) {
        kotlin.io.println(msg)
    }

    private fun senDevMessageBlocking(messageEmbed: MessageEmbed, fallback: String) {
        admin = jda!!.retrieveUserById(userId).complete()
        val devChannel = admin?.openPrivateChannel() ?: kotlin.run {
            kotlin.io.println(fallback)
            return
        }

        devChannel.complete()
            .sendMessage(messageEmbed).complete()
    }

    private fun sendDevMessage(messageEmbed: MessageEmbed, fallback: String) {
        val devChannel = admin?.openPrivateChannel() ?: kotlin.run {
            kotlin.io.println(fallback)
            return
        }

        devChannel.queue {
            it.sendMessage(messageEmbed).queue()
        }
    }
}