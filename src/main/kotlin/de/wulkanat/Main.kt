package de.wulkanat

import de.wulkanat.web.SiteWatcher
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.hmcore.TwitterJob
import org.quartz.CronScheduleBuilder.cronSchedule
import org.quartz.JobBuilder.newJob
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import javax.security.auth.login.LoginException
import kotlin.concurrent.timer

object Main {
    @JvmField
    var jdas = mutableListOf<JDA>()

    @JvmStatic
    fun main(args: Array<String>) {
        val builder = JDABuilder.createLight(
            Admin.token,
            GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES
        )
            .setActivity(Activity.watching(Admin.message))

        configureMemoryUsage(builder)

        for (i in 0 until 6) {
            try {
                jdas.add(
                    builder.useSharding(i, 6)
                        .build()
                )
            } catch (loginException: LoginException) {
                println("!!! Shard $i could not login !!!")
            }
        }

        val jda = builder.build()

        jda.addEventListener(AdminCli())
        jda.addEventListener(ErrorHandler())
        jda.addEventListener(OwnerCli())
        jda.awaitReady()

        Admin.connectToUser()

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
                Channels.sentToAll(MessageBuilder().setEmbed(SiteWatcher.newestBlog!!.toMessageEmbed()).build())
            }
        }

        // Grab the Scheduler instance from the Factory
        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        // and start it off
        scheduler.start();
        // define the job and tie it to our TwitterJob class
        val job: JobDetail = newJob(TwitterJob::class.java)
            .withIdentity("job1", "group1")
            .build()

        // Trigger the job to run now, and then repeat every 5 minutes
        val trigger: Trigger = newTrigger()
            .withIdentity("trigger1", "group1")
            .startNow()
            .withSchedule(cronSchedule("0 0/5 * 1/1 * ? *"))
            .build()

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, trigger);


    }

    fun configureMemoryUsage(builder: JDABuilder) {
        // Disable cache for member activities (streaming/games/spotify)
        builder.disableCache(CacheFlag.ACTIVITY)

        // Only cache members who are either in a voice channel or owner of the guild
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER))

        // Disable member chunking on startup
        builder.setChunkingFilter(ChunkingFilter.NONE)

        // Disable presence updates and typing events
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING)

        // Consider guilds with more than 50 members as "large".
        // Large guilds will only provide online members in their setup and thus reduce bandwidth if chunking is disabled.
        builder.setLargeThreshold(50)
    }
}