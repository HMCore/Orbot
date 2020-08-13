package de.wulkanat

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import de.wulkanat.web.SiteWatcher
import kotlin.concurrent.timer

fun main() {
    // TODO: move toke into file
    val builder = JDABuilder.createLight(
        Admin.token,
        GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
        .addEventListeners(Bot())
        .setActivity(Activity.watching("for new Blogposts"))
        .build()

    builder.addEventListener(Cli())
    builder.awaitReady()

    Channels.jda = builder
    Admin.jda = builder
    Admin.ready()

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            println("Shutting down...")
            Admin.printlnBlocking("Shutting down")
        }
    })

    timer("Updater", daemon = true, initialDelay = 0L, period = Admin.updateMs) {
        if (SiteWatcher.hasNewBlogPost()) {
            Channels.sentToAll(SiteWatcher.newestBlog!!.toMessageEmbed())
        }
    }
}