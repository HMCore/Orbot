package org.hmcore.model

import org.hmcore.extensions.hex2Rgb
import kotlinx.serialization.Serializable
import org.hmcore.extensions.embed

@Serializable
data class JobListingPreview(
    val title: String,
    val department: String,
    val location: String,
    val fullListingUrl: String
) {
    fun toMessageEmbed() = embed {
        title {
            value = this@JobListingPreview.title
            url = fullListingUrl
        }

        description = department
        color = hex2Rgb("#337fb0")
        author {
            name = location
        }
    }
}