package org.hmcore.webhook

import org.hmcore.WEBHOOKS
import net.dv8tion.jda.api.entities.Message
import org.hmcore.DiscordWebhook
import org.hmcore.DiscordWebhook.EmbedObject
import java.io.IOException

object WebhookCaller {
    fun sendToGuildedNews(message: Message) {
        val webhook = DiscordWebhook(WEBHOOKS.blogPostsWebhookUrl)
        webhook.setContent(message.contentRaw)
        webhook.setTts(false)

        message.embeds.forEach { embed ->
            webhook.addEmbed(EmbedObject().apply {
                setAuthor(embed.author?.name, embed.author?.url, embed.author?.iconUrl)
                color = embed.color
                description = embed.description
                setFooter(embed.footer?.text, embed.footer?.iconUrl)
                title = embed.title
                setImage(embed.image?.url)
                setThumbnail(embed.thumbnail?.url)
                url = embed.url

                for (field in embed.fields) {
                    addField(field.name, field.value, field.isInline)
                }
            })
        }

        try {
            webhook.execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}