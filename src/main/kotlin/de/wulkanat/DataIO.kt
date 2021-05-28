@file:JvmName("DataIO")
package de.wulkanat

import de.wulkanat.extensions.ensureExists
import de.wulkanat.model.BlogPostPreview
import de.wulkanat.model.JobListingPreview
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
    @Required val adminId: Long = 12345,
    @Required val token: String = "12345",
    @Required val updateMs: Long = 30000,
    @Required val shards: Int = 6,
    @Required val watchingMessage: String = "for new Blogposts",
    @Required val offlineMessage: String = "CONNECTION FAILED",
    @Required var twitterApi: TwitterApi? = TwitterApi()
)

@Serializable
data class TwitterApi(
    @Required val accessToken: String = "accessTokenHere",
    @Required val accessTokenSecret: String = "accessTokenSecretHere",
    @Required val apiKey: String = "apiKeyHere",
    @Required val apiKeySecret: String = "Api Key secret here"
)

@Serializable
data class Webhooks(
    @Required val blogPostsWebhookUrl: String = "https://...",
    @Required val jobListingsWebhookUrl: String = "https://...",
)

val WEBHOOKS_FILE = File("webhooks.json").ensureExists(Json.encodeToString(Webhooks()))
val WEBHOOKS = Json.decodeFromString<Webhooks>(WEBHOOKS_FILE.readText())

val SERVERS_FILE = File("servers.json").ensureExists(Json.encodeToString(listOf<DiscordChannel>()))
val SERVICE_CHANNELS_FILE =
    File("service_channels.json").ensureExists(Json.encodeToString(listOf<ServiceChannel>()))
val ADMIN_FILE = File("admin.json").ensureExists(Json.encodeToString(AdminFile()))
