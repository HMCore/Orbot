package de.wulkanat.model

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import de.wulkanat.extensions.hex2Rgb

data class BlogPostPreview(
    val title: String,
    val description: String,
    val date: String,
    val author: String,
    val imgUrl: String,
    val fullPostUrl: String
) {
    fun toMessageEmbed(): MessageEmbed {
        return EmbedBuilder()
            .setTitle(this.title, this.fullPostUrl)
            .setDescription(this.description)
            .setAuthor(this.author)
            .setThumbnail(this.imgUrl)
            .setFooter(this.date, "https://www.hytale.com/static/images/logo-h.png")
            .setColor(hex2Rgb("#337fb0"))
            .build()
    }
}