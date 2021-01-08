package de.wulkanat

import de.wulkanat.extensions.ensureExists
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import java.io.File

@Serializable
data class DiscordChannel(
    val id: Long,
    var mentionedRole: Long? = null,
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

val json = Json(JsonConfiguration.Stable)

val SERVERS_FILE = File("servers.json").ensureExists(json.stringify(DiscordChannel.serializer().list, listOf()))
val SERVICE_CHANNELS_FILE =
    File("service_channels.json").ensureExists(json.stringify(ServiceChannel.serializer().list, listOf()))
val ADMIN_FILE = File("admin.json").ensureExists(json.stringify(AdminFile.serializer(), AdminFile()))
