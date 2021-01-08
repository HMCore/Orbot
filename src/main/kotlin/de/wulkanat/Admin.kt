package de.wulkanat

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.rest.builder.message.EmbedBuilder
import de.wulkanat.files.Config
import de.wulkanat.files.ServiceChannels
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.Color

object Admin {
    var jda: Kord? = null
    set(value) {
        field = value

        GlobalScope.launch {
            admin = value?.getUser(Snowflake(Config.adminId))
            if (admin == null) {
                kotlin.io.println("Connection to de.wulkanat.Admin failed!")
            } else {
                kotlin.io.println("Connected to ${admin!!.username}. No further errors will be printed here.")
            }
        }
    }
    var admin: User? = null

    suspend fun println(msg: String) {
        sendDevMessage(msg) {
            title = msg
            color = Color.WHITE
        }
    }

    suspend fun error(msg: String, error: String, author: User? = null) {
        sendDevMessage("$msg\n\n$error") {
            title = msg
            description = error
            color = Color.RED
            author?.let { author {
                name = it.tag
                icon = it.avatar.url
                url = it.avatar.url
            }}
        }
    }

    suspend fun warning(msg: String) {
        sendDevMessage(msg) {
            title = msg
            color = Color.YELLOW
        }
    }

    suspend fun info() {
        sendDevMessage("Now watching for new Hytale BlogPosts") {
            title = "Now watching for new Hytale Blogposts every ${Config.updateMs / 1000}s"
            description = """
                ${ServiceChannels.getServerNames().joinToString("\n")}
                
                **_Service Channels_**
                ${ServiceChannels.getServiceChannelServers().joinToString("\n")}
            """.trimIndent()
            color = Color.GREEN
        }
    }

    fun silent(msg: String) {
        kotlin.io.println(msg)
    }

    private suspend inline fun sendDevMessage(fallback: String, crossinline embed: EmbedBuilder.() -> Unit) {
        val devChannel = admin?.getDmChannel() ?: kotlin.run {
            kotlin.io.println(fallback)
            return
        }

        devChannel.createEmbed(embed)
    }
}