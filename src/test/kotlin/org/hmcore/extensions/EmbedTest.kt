package org.hmcore.extensions

import java.awt.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EmbedTest {
    @Test
    fun `Embed Title DSL should work`() {
        val dslEmbed = embed {
            title {
                value = "Title"
                url = "https://a.b.c"
            }
        }

        assertEquals("Title", dslEmbed.title)
        assertEquals("https://a.b.c", dslEmbed.url)
    }

    @Test
    fun `Embed Thumbnail should work`() {
        val dslEmbed = embed {
            thumbnail = "https://a.b.c"
        }

        assertNotNull(dslEmbed.thumbnail)
        assertEquals("https://a.b.c", dslEmbed.thumbnail!!.url)
    }

    @Test
    fun `Embed Footer DSL should work`() {
        val dslEmbed = embed {
            footer {
                value = "Value"
                iconUrl = "https://a.b.c"
            }
        }

        assertNotNull(dslEmbed.footer)
        assertEquals("Value", dslEmbed.footer!!.text)
        assertEquals("https://a.b.c", dslEmbed.footer!!.iconUrl)
    }

    @Test
    fun `Embed DSL should produce same result as native`() {
        val dslEmbed = embed {
            title = "A Title"
            description = "A Description"
            color = Color.YELLOW

            author {
                name = "An author"
                icon = "https://d.e.f"
                url = "https://a.b.c"
            }

            field {
                inline = true
                name = "Field1"
                value = "Body1"
            }

            field {
                inline = false
                name = "Field2"
                value = "Body2"
            }
        }

        assertEquals("A Title", dslEmbed.title)
        assertEquals("A Description", dslEmbed.description)
        assertEquals(Color.YELLOW, dslEmbed.color)

        assertNotNull(dslEmbed.author)
        assertEquals("An author", dslEmbed.author!!.name)
        assertEquals("https://d.e.f", dslEmbed.author!!.iconUrl)
        assertEquals("https://a.b.c", dslEmbed.author!!.url)

        assertEquals(2, dslEmbed.fields.size)
        assertEquals("Field1", dslEmbed.fields[0].name)
        assertEquals("Body1", dslEmbed.fields[0].value)
        assertEquals(true, dslEmbed.fields[0].isInline)

        assertEquals("Field2", dslEmbed.fields[1].name)
        assertEquals("Body2", dslEmbed.fields[1].value)
        assertEquals(false, dslEmbed.fields[1].isInline)
    }
}