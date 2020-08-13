package de.wulkanat

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Bot : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val message = event.message

        if (message.contentRaw == "!ping") {
            val channel = message.channel
            val time = System.currentTimeMillis()

            channel.sendMessage("Pong!")
                .queue {
                    it.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time)
                        .queue()
                }
        }
    }
}