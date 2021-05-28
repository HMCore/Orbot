package org.hmcore;

import de.wulkanat.DataIO;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;

public class WebhookCaller {

    public static void sendToGuildedNews(Message message) {

        DiscordWebhook webhook = new DiscordWebhook(DataIO.getWEBHOOKS().getBlogPostsWebhookUrl());

        webhook.setContent(message.getContentRaw());
        webhook.setTts(false);
        MessageEmbed embed = message.getEmbeds().get(0);
        DiscordWebhook.EmbedObject newEmbed = new DiscordWebhook.EmbedObject();
        if(embed.getAuthor() != null) newEmbed.setAuthor(embed.getAuthor().getName(), embed.getAuthor().getUrl(), embed.getAuthor().getIconUrl());
        newEmbed.setColor(embed.getColor());
        newEmbed.setDescription(embed.getDescription());
        if(embed.getFooter() != null) newEmbed.setFooter(embed.getFooter().getText(), embed.getFooter().getIconUrl());
        newEmbed.setTitle(embed.getTitle());
        if(embed.getImage() != null) newEmbed.setImage(embed.getImage().getUrl());
        if(embed.getThumbnail() != null) newEmbed.setThumbnail(embed.getThumbnail().getUrl());
        newEmbed.setUrl(embed.getUrl());
        for (MessageEmbed.Field field:
                embed.getFields()) {
            newEmbed.addField(field.getName(), field.getValue(), field.isInline());
        }
        webhook.addEmbed(newEmbed);

        try {
            webhook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
