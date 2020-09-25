package de.wulkanat

import de.wulkanat.extensions.ensureExists
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import kotlinx.serialization.stringify
import java.io.File

@Serializable
data class DiscordChannel(
    val id: Long,
    var mentionedRole: String? = null,
    var autoPublish: Boolean = false,
    var message: CustomMessage? = null
)

@Serializable
data class ServiceChannel(
    val id: Long
)

@Serializable
data class CustomMessage(
    var message: String,
    var pushAnnouncement: Boolean = false
)

@Serializable
data class AdminFile(
    val adminId: Long = 12345,
    val token: String = "12345",
    val updateMs: Long = 30000,
    val watchingMessage: String = "for new Blogposts",
    val offlineMessage: String = "CONNECTION FAILED"
)

@Serializable
data class TwitterFile(
    val accessToken: String = "",
    val accessTokenSecret: String = "",
    val apiKey: String = "",
    val apiSecretKey: String = "",
    val bearerToken: String = "",
    val env: String = "dev",
)

val json = Json { allowStructuredMapKeys = true }

val SERVERS_FILE =
    File("servers.json").ensureExists(json.encodeToString(ListSerializer(DiscordChannel.serializer()), listOf()))
val SERVICE_CHANNELS_FILE =
    File("service_channels.json").ensureExists(
        json.encodeToString(
            ListSerializer(ServiceChannel.serializer()),
            listOf()
        )
    )
val ADMIN_FILE = File("admin.json").ensureExists(json.encodeToString(AdminFile.serializer(), AdminFile()))
val TWITTER_FILE = File("twitter.json").ensureExists(json.encodeToString(TwitterFile.serializer(), TwitterFile()))
