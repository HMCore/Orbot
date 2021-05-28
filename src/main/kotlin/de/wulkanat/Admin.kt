@file:JvmName("Admin")

package de.wulkanat

import de.wulkanat.extensions.embed
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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

    var admin: User? = null

    fun connectToUser() {
        Main.jdas.find { jda ->
            jda.retrieveUserById(userId).complete()?.also { admin = it } != null
        } ?: return kotlin.io.println("Connection to de.wulkanat.Admin failed!")

        kotlin.io.println("Connected to ${admin!!.name}. No further errors will be printed here.")
    }

    fun println(msg: String) = sendDevMessage(
        embed {
            title = msg
            color = Color.WHITE
        }, msg
    )

    fun printlnBlocking(msg: String) = sendDevMessageBlocking(
        embed {
            title = msg
            color = Color.WHITE
        }, msg
    )

    fun error(msg: String, error: String, author: User? = null) = sendDevMessage(
        embed {
            title = msg
            description = error
            color = Color.RED

            author {
                name = author?.asTag
                url = author?.avatarUrl
                icon = author?.avatarUrl
            }
        }, "$msg\n\n${error}"
    )

    fun errorBlocking(msg: String, error: Exception) = sendDevMessageBlocking(
        embed {
            title = msg
            description = error.message
            color = Color.RED
        }, "$msg\n\n${error.message}"
    )

    fun warning(msg: String) = sendDevMessage(
        embed {
            title = msg
            color = Color.YELLOW
        }, msg
    )

    fun info() {
        sendDevMessage(
            embed {
                title = "Now watching for new Hytale Blogposts every ${updateMs / 1000}s"
                description = """
                    ${Channels.getServerNames().joinToString("\n")}
                    
                    **_Service Channels_**
                    ${Channels.getServiceChannelServers().joinToString("\n")}
                """.trimIndent()
                color = Color.GREEN

            }, "Now watching for new Hytale BlogPosts"
        )
    }

    fun silent(msg: String) {
        kotlin.io.println(msg)
    }

    private fun sendDevMessageBlocking(messageEmbed: MessageEmbed, fallback: String) {
        (admin?.openPrivateChannel() ?: return kotlin.io.println(fallback))
            .complete().sendMessage(messageEmbed).complete()
    }

    fun sendDevMessage(messageEmbed: MessageEmbed, fallback: String) {
        (admin?.openPrivateChannel() ?: return kotlin.io.println(fallback)).queue {
            it.sendMessage(messageEmbed).queue()
        }
    }
}