@file:OptIn(ExperimentalCli::class)

package org.hmcore.cli.admin

import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.optional
import org.hmcore.TwitterJob
import org.hmcore.web.fakeUpdateBlogPost
import org.hmcore.web.fakeUpdateJobListings

class FakeUpdateCommand : Subcommand("fakeUpdate", "Cause a fake update. Use with great caution.") {
    private val twitter by option(ArgType.Boolean,
        "twitter",
        "Cause a fake update for Twitter")
    private val jobs by argument(ArgType.Int,
        "jobs",
        "Cause a fake update for Job Listings for the x amount of last jobs").optional()
    private val blog by argument(ArgType.Int,
        "blogs",
        "Cause a fake update for Blogs for the x amount of last blogs").optional()

    override fun execute() {
        blog?.let { fakeUpdateBlogPost(it) }
        jobs?.let { fakeUpdateJobListings(it) }
        if (twitter == true) TwitterJob.lastTweetID = "empty"
    }
}