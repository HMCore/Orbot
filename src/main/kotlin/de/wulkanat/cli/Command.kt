package de.wulkanat.cli

class Command<T>(
    val name: String,
    val description: String,
    val action: (required: List<String>, optional: MutableMap<String, String>, passthrough: T) -> Unit,
    val arguments: Cli<T>.CommandBuilder2.ArgumentBuilder
) {
    fun usage(): String {
        return "$name ${arguments.required.list
            .joinToString(separator = " ") {
                "[${it.second.usage(
                    arguments.required.literals,
                    it.first
                )}]"
            }} ${arguments.optional.list
            .map {
                "--${it.key}${arguments.optional.shorts[name]?.let { short -> " -$short " } ?: ""
                }${if (it.value == ArgumentType.EXISTS) "" else " <${it.value.stringName}>"}"
            }
            .joinToString(separator = " ")}"
    }
}