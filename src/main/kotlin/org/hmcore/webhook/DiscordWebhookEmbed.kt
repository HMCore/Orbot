package org.hmcore.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@Serializable
data class Footer(
    val text: String? = null,
    @SerialName("icon_url")
    val iconUrl: String? = null,
)

@Serializable
data class Thumbnail(
    val url: String? = null,
)

@Serializable
data class Image(
    val url: String? = null,
)

@Serializable
data class Author(
    val name: String? = null,
    val url: String? = null,
    @SerialName("icon_url")
    val iconUrl: String? = null,
)

@Serializable
data class Field(
    val name: String? = null,
    val value: String? = null,
    val inline: Boolean? = null,
)

@Serializable
data class DiscordWebhookEmbed(
    var title: String? = null,
    var description: String? = null,
    var url: String? = null,
    var color: Int? = null,

    var footer: Footer? = null,
    var thumbnail: Thumbnail? = null,
    var image: Image? = null,
    var author: Author? = null,
    var fields: MutableList<Field> = mutableListOf(),
)

@Serializable
data class DiscordWebhook(
    var content: String? = null,
    var username: String? = null,
    var avatarUrl: String? = null,
    var tts: Boolean = false,
    var embeds: MutableList<DiscordWebhookEmbed> = mutableListOf(),
) {
    fun send(url: String): Boolean {
        var connection: HttpsURLConnection? = null
        var stream: OutputStream? = null

        return try {
            connection = URL(url).openConnection() as HttpsURLConnection
            connection.addRequestProperty("Content-Type", "application/json")
            connection.addRequestProperty("User-Agent", "Kotlin-DiscordWebhook")
            connection.doOutput = true
            connection.requestMethod = "POST"

            stream = connection.outputStream
            stream.write(Json.encodeToString(this).toByteArray())
            stream.flush()

            true
        } catch (e: IOException) {
            e.printStackTrace()

            false
        } finally {
            stream?.close()
            connection?.inputStream?.close()
            connection?.disconnect()
        }
    }
}