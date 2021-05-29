package org.hmcore

object DiscordRpc {

    fun updatePresence(available: Boolean) {
        // jda ?: return

        // jda!!.presence.activity = Activity.watching(if (available) Admin.message else Admin.offlineMessage)
        // jda!!.presence.isIdle = !available
        // noop
        if (available) Admin.println("Back online") else Admin.error("Gone offline", "Can't reach Hytale server")
    }
}