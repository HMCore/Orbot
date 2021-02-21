package de.wulkanat.web

import de.wulkanat.Admin
import de.wulkanat.DiscordRpc
import de.wulkanat.model.BlogPostPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException

object SiteWatcher {
    private const val BLOG_INDEX_URL = "https://www.hytale.com/news"
    var newestBlog: BlogPostPreview? = null
    private var siteOnline = false

    suspend fun hasNewBlogPost(): Boolean {
        try {
            val doc = withContext(Dispatchers.IO) {
                // solved by `withContext`
                // https://stackoverflow.com/a/63332658
                @Suppress("BlockingMethodInNonBlockingContext")
                Jsoup.connect(BLOG_INDEX_URL).get()
            }
            val newBlog = BlogPostParser.getFistBlog(doc)

            if (newestBlog == newBlog) {
                return false
            }

            if (newestBlog == null) {
                newestBlog = newBlog
                return false
            } else {
                newestBlog = newBlog
            }
        } catch (e: IOException) {
            Admin.error("Connection to Hytale Server failed", e.message ?: e.localizedMessage)
            siteOnline = false
            DiscordRpc.updatePresence(siteOnline)

            return false
        }

        if (!siteOnline) {
            siteOnline = true
            DiscordRpc.updatePresence(siteOnline)
        }

        return true
    }
}