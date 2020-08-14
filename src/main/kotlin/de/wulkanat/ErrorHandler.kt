package de.wulkanat

import net.dv8tion.jda.api.events.ExceptionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ErrorHandler : ListenerAdapter() {
    override fun onException(event: ExceptionEvent) {
        Admin.error(event.cause.message ?: event.cause.localizedMessage, event.cause.stackTrace.toString())
    }
}