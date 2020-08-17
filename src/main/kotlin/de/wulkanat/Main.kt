package de.wulkanat

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import de.wulkanat.web.SiteWatcher
import kotlin.concurrent.timer

fun main() {
    val builder = JDABuilder.createLight(
        Admin.token,
        GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
        .setActivity(Activity.watching("for new Blogposts"))
        .build()

    builder.addEventListener(AdminCli())
    builder.addEventListener(ErrorHandler())
    builder.addEventListener(OwnerCli())
    builder.awaitReady()

    Channels.jda = builder
    Admin.jda = builder
    Admin.info()

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            println("Shutting down...")
            println("Sending shutdown notice to Admin, waiting 5s...")
            Admin.println("Shutting down")
            sleep(5000)
        }
    })

    timer("Updater", daemon = true, initialDelay = 0L, period = Admin.updateMs) {
        if (SiteWatcher.hasNewBlogPost()) {
            Channels.sentToAll(SiteWatcher.newestBlog!!.toMessageEmbed())
        }
    }
}