package de.wulkanat.files

import de.wulkanat.files.concept.SerializableObject
import kotlinx.serialization.Serializable

object Config : SerializableObject<Config.Data>("config.json", Data(), Data.serializer()) {
    val adminId: Long
        get() = instance.adminId
    val token: String
        get() = instance.token
    val updateMs: Long
        get() = instance.updateMs
    val watchingMessage: String
        get() = instance.watchingMessage
    val offlineMessage: String
        get() = instance.offlineMessage

    @Serializable
    data class Data(
        val adminId: Long = 12345,
        val token: String = "12345",
        val updateMs: Long = 30000,
        val watchingMessage: String = "for new Blogposts",
        val offlineMessage: String = "CONNECTION FAILED"
    )
}
