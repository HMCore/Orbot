package org.hmcore.model

import org.hmcore.extensions.hex2Rgb
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BlogPostPreviewTest {
    @Test
    fun `Blog post should be correctly parsed to Embed`() {
        val embed = BlogPostPreview(
            title = "Title",
            description = "Description",
            date = "01.01.2000",
            author = "Nobody",
            imgUrl = "https://a.b.c",
            fullPostUrl = "https://d.e.f"
        ).toMessageEmbed()

        assertEquals("Title", embed.title)
        assertEquals("https://d.e.f", embed.url)
        assertEquals("Description", embed.description)
        assertEquals(hex2Rgb("#337FB0"), embed.color)
        assertNotNull(embed.thumbnail)
        assertEquals("https://a.b.c", embed.thumbnail!!.url)

        assertNotNull(embed.footer)
        assertEquals("01.01.2000", embed.footer!!.text)
        assertEquals("https://www.hytale.com/static/images/logo-h.png", embed.footer!!.iconUrl)

        assertNotNull(embed.author)
        assertEquals(embed.author!!.name, "Nobody")
    }
}