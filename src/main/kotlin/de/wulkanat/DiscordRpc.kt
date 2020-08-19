package de.wulkanat

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity

object DiscordRpc {
    var jda: JDA? = null

    fun updatePresence(available: Boolean) {
        jda ?: return

        jda!!.presence.activity = Activity.watching(if (available) Admin.message else Admin.offlineMessage)
        jda!!.presence.isIdle = !available
    }
}