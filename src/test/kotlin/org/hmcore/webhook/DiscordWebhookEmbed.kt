package org.hmcore.webhook

import org.junit.Test
import kotlin.test.assertEquals

class DiscordWebhookEmbed {
    /*@Test TODO: Test against JSON Schema or something
    fun `Webhook class should comply with Discord specification`() {
        val exampleWebhook = """

        """.trimIndent()
    }*/

    @Test
    fun `Webhook should not throw and return false if supplied invalid URL`() {
        assertEquals(false, DiscordWebhook().send("not a valid url"))
    }

    @Test
    fun `Webhook should return false if connection throws`() {

    }

    @Test
    fun `Webhook should send correctly`() {
        DiscordWebhook(
            content = "Test"
        )
    }
}