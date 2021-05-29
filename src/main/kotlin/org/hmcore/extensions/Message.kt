package org.hmcore.extensions

import net.dv8tion.jda.api.entities.Message
import org.hmcore.WEBHOOKS
import org.hmcore.webhook.*

fun Message.toWebhook(): DiscordWebhook {
    val webhook = DiscordWebhook()
    webhook.content = contentRaw
    webhook.tts = false

    embeds.forEach { embed ->
        webhook.embeds.add(DiscordWebhookEmbed().apply {
            title = embed.title
            description = embed.description
            color = embed.color?.toRgb()
            image = Image(embed.image?.url)
            url = embed.url

            thumbnail = Thumbnail(embed.thumbnail?.url)
            author = Author(
                embed.author?.name,
                embed.author?.url,
                embed.author?.iconUrl
            )
            footer = Footer(
                embed.footer?.text,
                embed.footer?.iconUrl,
            )

            for (field in embed.fields) {
                fields.add(
                    Field(
                    field.name,
                    field.value,
                    field.isInline,
                )
                )
            }
        })
    }

    return webhook
}