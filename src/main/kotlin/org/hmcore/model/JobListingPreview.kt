package org.hmcore.model

import org.hmcore.extensions.hex2Rgb
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

@Serializable
data class JobListingPreview(
    val title: String,
    val department: String,
    val location: String,
    val fullListingUrl: String
) {
    fun toMessageEmbed(): MessageEmbed {
        return EmbedBuilder()
            .setTitle(this.title, this.fullListingUrl)
            .setDescription(this.department)
            .setAuthor(this.location)
            .setColor(hex2Rgb("#337fb0"))
            .build()
    }
}