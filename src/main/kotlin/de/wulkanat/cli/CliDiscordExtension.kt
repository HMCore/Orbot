package de.wulkanat.cli

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

fun <T> Command<T>.discordUsage(): String {
    return "${name}_ ${arguments.required.list
        .joinToString(separator = " ") {
            "**[**${it.first/*it.second.discordUsage(
                arguments.required.literals,
                it.first
            )*/}**]**"
        }} ${arguments.optional.list
        .map {
            "--${it.key}${arguments.optional.shorts[name]?.let { short -> " _-${short}_ " } ?: ""
            }${if (it.value == ArgumentType.EXISTS) "" else " **<**${it.value.stringName}**>**"}"
        }
        .joinToString(separator = " ")}"
}

fun ArgumentType.discordUsage(literals: Map<String, List<String>>, name: String): String {
    return when (this) {
        ArgumentType.LITERAL -> "${literals[name]?.joinToString(separator = "**|**")}"
        ArgumentType.EXISTS -> ""
        else -> stringName
    }
}

fun <T> Command<T>.discordUsageEmbed(footer: String?): MessageEmbed {
    return EmbedBuilder()
        .setTitle("Usage:")
        .setDescription("_${discordUsage()}")
        .also { builder -> footer?.let { builder.setFooter(footer) } }
        .build()
}

fun <T> Cli<T>.discordUsage(): String {
    return commands.map { "_$prefix${it.value.discordUsage()}" }.joinToString("\n")
}

fun <T> Cli<T>.discordUsageEmbed(): MessageEmbed {
    return EmbedBuilder()
        .setTitle("Help")
        .also {
            commands.map { Pair(it.value.description, "_$prefix${it.value.discordUsage()}") }
                .forEach { (title, description) ->
                    it.addField(title, description, false)
                }
        }
        .setFooter("Commands are case-sensitive.")
        .build()
}
