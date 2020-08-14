package de.wulkanat.extensions

import Inaccessibles
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.internal.requests.Method
import net.dv8tion.jda.internal.requests.Route
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl
import net.dv8tion.jda.internal.utils.Checks

fun MessageChannel.crosspostById(messageId: String): MessageAction {
    Checks.isSnowflake(messageId, "Message ID")

    val route = CROSSPOST_MESSAGE.compile(id, messageId)
    return MessageActionImpl(jda, route, this).append("This is not of your interest.")
}

fun Message.crosspost(): MessageAction {
    val messageId = Inaccessibles.toUnsignedString(idLong)

    return channel.crosspostById(messageId)
}

val CROSSPOST_MESSAGE: Route = Inaccessibles.getRoute(
    Method.POST,
    "channels/{channel_id}/messages/{message_id}/crosspost"
)