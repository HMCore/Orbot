package org.hmcore.extensions

import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

class EmbedBuilderBuilder {
    val _embed = EmbedBuilder()

    var title: String?
        set(value) {
            _embed.setTitle(value)
        }
        get() = null
    var description: String?
        set(value) {
            _embed.setDescription(value)
        }
        get() = null

    var color: Color
        set(value) {
            _embed.setColor(value)
        }
        get() = Color.BLACK

    var thumbnail: String?
        set(value) {
            _embed.setThumbnail(value)
        }
        get() = null

    fun field(builder: FieldBuilderBuilder.() -> Unit) =
        FieldBuilderBuilder().apply { builder() }.let {
            _embed.addField(it.name, it.value, it.inline)
        }

    fun author(builder: AuthorBuilderBuilder.() -> Unit) =
        AuthorBuilderBuilder().apply { builder() }.let {
            _embed.setAuthor(it.name, it.url, it.icon)
        }

    fun title(builder: TitleBuilderBuilder.() -> Unit) =
        TitleBuilderBuilder().apply { builder() }.let {
            _embed.setTitle(it.title, it.url)
        }

    fun footer(builder: FooterBuilderBuilder.() -> Unit) =
        FooterBuilderBuilder().apply { builder() }.let {
            _embed.setFooter(it.value, it.iconUrl)
        }
}

class FieldBuilderBuilder {
    var name: String? = null
    var value: String? = null
    var inline = false
}

class TitleBuilderBuilder {
    var title: String? = null
    var url: String? = null
}

class AuthorBuilderBuilder {
    var name: String? = null
    var url: String? = null
    var icon: String? = null
}

class FooterBuilderBuilder {
    var value: String? = null
    var iconUrl: String? = null
}

fun embed(builder: EmbedBuilderBuilder.() -> Unit) =
    EmbedBuilderBuilder().apply { builder() }._embed.build()