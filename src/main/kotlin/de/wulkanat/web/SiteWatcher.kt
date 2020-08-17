package de.wulkanat.web

import de.wulkanat.Admin
import de.wulkanat.model.BlogPostPreview
import org.jsoup.Jsoup
import java.io.IOException

object SiteWatcher {
    private const val BLOG_INDEX_URL = "https://www.hytale.com/news"
    var newestBlog: BlogPostPreview? = null

    fun hasNewBlogPost(): Boolean {
        try {
            val doc = Jsoup.connect(BLOG_INDEX_URL).get()
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

            return false
        }

        return true
    }
}