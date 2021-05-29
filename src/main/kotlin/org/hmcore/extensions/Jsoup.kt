package org.hmcore.extensions

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

operator fun Element.get(className: String): Elements =
    this.getElementsByClass(className)

val Elements.text get() = text().trim()
val Element.absUrl get(): String = child(0).absUrl("href")
val Element.imgSrc get(): String = child(0).attr("src")