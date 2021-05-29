package org.hmcore.model

import org.hmcore.extensions.hex2Rgb
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JobListingPreviewTest {
    @Test
    fun `Job listings should be correctly parsed to Embed`() {
        val embed = JobListingPreview(
            title = "Title",
            department = "Department",
            location = "Null Island",
            fullListingUrl = "https://d.e.f"
        ).toMessageEmbed()

        assertEquals("Title", embed.title)
        assertEquals("https://d.e.f", embed.url)
        assertEquals("Department", embed.description)
        assertEquals(hex2Rgb("#337FB0"), embed.color)

        assertNotNull(embed.author)
        assertEquals(embed.author!!.name, "Null Island")
    }
}