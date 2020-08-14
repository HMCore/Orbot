package de.wulkanat

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class DiscordChannel(
    val id: Long,
    val mentionedRole: String? = null,
    val autoPublish: Boolean = false
)

@Serializable
data class AdminFile(
    val adminId: Long,
    val token: String,
    val updateMs: Long
)

val SERVERS_FILE = File("servers.json")
val TEST_FILE = File("test.json")
val ADMIN_FILE = File("admin.json")
