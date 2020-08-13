package de.wulkanat

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class DiscordChannel(
    val id: Long,
    val mentionedRole: String? = null
)

@Serializable
data class AdminFile(
    val adminId: Long,
    val token: String,
    val updateMs: Long
)

val SERVERS_FILE = File("servers.json")
val ADMIN_FILE = File("admin.json")
