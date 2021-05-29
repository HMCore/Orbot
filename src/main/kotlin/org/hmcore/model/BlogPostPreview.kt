package org.hmcore.model

import org.hmcore.extensions.hex2Rgb
import kotlinx.serialization.Serializable
import org.hmcore.extensions.embed

@Serializable
data class BlogPostPreview(
    val title: String,
    val description: String,
    val date: String,
    val author: String,
    val imgUrl: String,
    val fullPostUrl: String
) {
    fun toMessageEmbed() = embed {
        title {
            title = this@BlogPostPreview.title
            url = fullPostUrl
        }
        description = this@BlogPostPreview.description
        color = hex2Rgb("#337FB0")
        thumbnail = imgUrl

        author {
            name = author
        }
        footer {
            value = date
            iconUrl = "https://www.hytale.com/static/images/logo-h.png"
        }
    }
}