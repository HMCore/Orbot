package org.hmcore

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hmcore.web.getNewBlogPosts
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.hmcore.web.getNewJobListings
import org.quartz.CronScheduleBuilder.cronSchedule
import org.quartz.JobBuilder.newJob
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import java.io.File
import javax.security.auth.login.LoginException
import kotlin.concurrent.timer

object Main {
    @JvmField
    var jdas = mutableListOf<JDA>()

    @JvmStatic
    fun main(args: Array<String>) {

        if(args.isEmpty()) startBot() else
            when(args[0]) {
                "serverDataConvert1" -> serverDataConvert1()
                else -> startBot()
            }

    }

    fun serverDataConvert1() {
        var file = File("servers.json")
        if(!file.exists()) return
        var content = ""
        file.bufferedReader().readLines().forEach {
            content += it
                .replace("""mentionedRole":""".toRegex(),"""type":"BLOGPOST","mentionedRole":""")
        }
        file.writeBytes(content.encodeToByteArray())
    }

    fun startBot() {
        val builder = JDABuilder.createLight(
            Admin.token,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.DIRECT_MESSAGES
        ).setActivity(Activity.watching(Admin.message))

        configureMemoryUsage(builder)

        for (i in 0 until Admin.adFile.shards) {
            try {
                jdas.add(builder.useSharding(i, Admin.adFile.shards).build().apply {
                    addEventListener(AdminCli())
                    addEventListener(ErrorHandler())
                    addEventListener(OwnerCli())
                    awaitReady()
                })
            } catch (loginException: LoginException) {
                println("!!! Shard $i could not login !!!")
            }
        }

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

        timer("UpdaterBlogpost", daemon = true, initialDelay = 0L, period = Admin.updateMs) {
            getNewBlogPosts()?.forEach {
                Channels.sentToAll(MessageBuilder().setEmbed(it.toMessageEmbed()).build(), MessageType.BLOGPOST)
            }
        }

        timer("UpdaterJob", daemon = true, initialDelay = 0L, period = Admin.updateMs) {
            getNewJobListings()?.forEach {
                Channels.sentToAll(MessageBuilder().setEmbed(it.toMessageEmbed()).build(), MessageType.JOB_LISTING)
            }
        }

        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        scheduler.start()

        val job: JobDetail = newJob(TwitterJob::class.java)
            .withIdentity("job1", "group1")
            .build()

        // Trigger the job to run now, and then repeat every 5 minutes
        val trigger: Trigger = newTrigger()
            .withIdentity("trigger1", "group1")
            .startNow()
            .withSchedule(cronSchedule("0 0/5 * 1/1 * ? *"))
            .build()

        scheduler.scheduleJob(job, trigger)
    }

    private fun configureMemoryUsage(builder: JDABuilder) {
        builder.disableCache(CacheFlag.ACTIVITY)
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER))
        builder.setChunkingFilter(ChunkingFilter.NONE)
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING)

        // Consider guilds with more than 50 members as "large".
        // Large guilds will only provide online members in their setup and thus reduce bandwidth if chunking is disabled.
        builder.setLargeThreshold(50)
    }
}