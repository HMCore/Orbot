package de.wulkanat.web

import de.wulkanat.model.BlogPostPreview
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

object BlogPostParser {
    fun getFistBlog(doc: Document): BlogPostPreview {
        val posts = doc.getElementsByClass("postWrapper")
        return parseBlog(posts.first())
    }

    private fun parseBlog(elm: Element): BlogPostPreview {

        return BlogPostPreview(
            title = elm.getElementsByClass("post__details__heading").first().text(),
            imgUrl = elm.getElementsByClass("post__image__frame").first().child(0).attr("src"),
            fullPostUrl = elm.child(0).absUrl("href"),
            date = elm.getElementsByClass("post__details__meta__date").first().text(),
            author = elm.getElementsByClass("post__details__meta__author").first().text(),
            description = elm.getElementsByClass("post__details__body").first().text()
        )
    }
}