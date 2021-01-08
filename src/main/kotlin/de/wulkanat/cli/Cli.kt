package de.wulkanat.cli

class Cli<T>(var prefix: String = ".") {
    val commands = mutableMapOf<String, Command<T>>()

    fun parse(
        command: String,
        passThrough: T,
        helpMessage: (Cli<T>) -> Unit = {},
        commandMisuse: (Command<T>, String) -> Unit = { _, _ -> }
    ): Boolean? {
        if (!command.startsWith(prefix)) return false // not a command

        val msg =
            Regex("[^\\s`]+|`[^`]*`").findAll(command.removePrefix(prefix)).toList().map { it.value }

        if (msg[0] == "help") {
            helpMessage(this)
            return true
        }

        val realCommand = commands[msg[0]] ?: return false // command not found
        val (required, optional) = realCommand.arguments
        if (msg.size < required.list.size + 1) {
            commandMisuse(realCommand, "Too few arguments!")
            return null
        }

        val requiredOut: MutableList<String> = mutableListOf()
        val optionalOut: MutableMap<String, String> = mutableMapOf()

        for (i in 1..required.list.size) {
            val (name, type) = required.list[i - 1]

            requiredOut.add(
                when (type) {
                    ArgumentType.LITERAL -> required.literals[name]?.find { it == msg[i] }?.toString()
                    else -> type.match.matchEntire(msg[i])?.value
                } ?: kotlin.run {
                    commandMisuse(realCommand, "Argument '${msg[i]}' is not of type ${type.stringName}!")
                    return@parse null
                }
            )
        }

        var i = required.list.size + 1
        while (i < required.list.size + 1) {
            val key = optional.shorts[msg[i]] ?: msg[i]
            val value = optional.list[optional.shorts[msg[i]] ?: msg[i]] ?: kotlin.run {
                commandMisuse(realCommand, "Unknown optional argument '$key'")
                return@parse null
            }

            optionalOut[key] = when (value) {
                ArgumentType.LITERAL -> optional.literals[key]?.find { it == msg[i] }?.toString()
                else -> value.match.matchEntire(msg[i])?.value
            } ?: kotlin.run {
                commandMisuse(realCommand, "Argument '$key' is not of type ${value.stringName}!")
                return@parse null
            }

            i += if (value == ArgumentType.EXISTS) 1 else 2
        }

        realCommand.action(requiredOut, optionalOut, passThrough)
        return true // success
    }

    fun usage(): String {
        return commands.map { "$prefix${it.value.usage()}" }.joinToString("\n")
    }

    infix fun prefix(func: Cli<T>.() -> Unit): Cli<T> {
        func()
        return this
    }

    inner class CommandBuilder {
        infix fun name(name: String): CommandBuilder2 {
            return CommandBuilder2(name)
        }
    }

    inner class CommandBuilder2(val name: String) {
        val argumentBuilder = ArgumentBuilder()
        var descriptionLocal = ""

        infix fun does(description: String): DoesHelper {
            descriptionLocal = description
            return DoesHelper()
        }

        inner class DoesHelper {
            infix fun through(action: (required: List<String>, optional: MutableMap<String, String>, passthrough: T) -> Unit): Command<T> {
                return Command(name, descriptionLocal, action, argumentBuilder).also { commands[name] = it }
            }
        }

        infix fun with(action: ArgumentBuilder.() -> Unit): CommandBuilder2 {
            argumentBuilder.action()
            return this
        }

        inner class ArgumentBuilder {
            val required = RequiredArgHelper()
            val optional = OptionalArgHelper()

            operator fun component1() = required
            operator fun component2() = optional

            inner class RequiredArgHelper {
                val list: MutableList<Pair<String, ArgumentType>> = mutableListOf()
                val literals: MutableMap<String, MutableList<String>> = mutableMapOf()

                infix fun int(name: String) {
                    list.add(Pair(name, ArgumentType.INT))
                }

                infix fun float(name: String) {
                    list.add(Pair(name, ArgumentType.FLOAT))
                }

                infix fun string(name: String) {
                    list.add(Pair(name, ArgumentType.STRING))
                }

                infix fun literal(name: String): LiteralHelper {
                    list.add(Pair(name, ArgumentType.LITERAL))
                    return LiteralHelper(name)
                }

                infix fun bool(name: String) {
                    list.add(Pair(name, ArgumentType.BOOLEAN))
                }

                inner class LiteralHelper(val name: String) {
                    infix fun with(literalsList: String): LiteralHelperHelper {
                        val list = mutableListOf(literalsList)
                        literals[name] = list

                        return LiteralHelperHelper(list)
                    }

                    inner class LiteralHelperHelper(private val listListList: MutableList<String>) {
                        infix fun or(other: String): LiteralHelperHelper {
                            listListList.add(other)
                            return this
                        }
                    }
                }
            }

            inner class OptionalArgHelper {
                val list: MutableMap<String, ArgumentType> = mutableMapOf()
                val shorts: MutableMap<String, String> = mutableMapOf()
                val literals: MutableMap<String, List<String>> = mutableMapOf()

                inner class ShortsHelper(val name: String, val shortsMap: MutableMap<String, String>) {
                    infix fun short(shortName: String) {
                        shortsMap[shortName] = name
                    }
                }

                infix fun int(name: String): ShortsHelper {
                    list[name] = ArgumentType.INT
                    return ShortsHelper(name, shorts)
                }

                infix fun float(name: String): ShortsHelper {
                    list[name] = ArgumentType.FLOAT
                    return ShortsHelper(name, shorts)
                }

                infix fun string(name: String): ShortsHelper {
                    list[name] = ArgumentType.STRING
                    return ShortsHelper(name, shorts)
                }

                infix fun bool(name: String): ShortsHelper {
                    list[name] = ArgumentType.BOOLEAN
                    return ShortsHelper(name, shorts)
                }

                infix fun literal(name: String): LiteralHelper {
                    list[name] = ArgumentType.LITERAL
                    return LiteralHelper(name)
                }

                infix fun existence(name: String): ShortsHelper {
                    list[name] = ArgumentType.EXISTS
                    return ShortsHelper(name, shorts)
                }

                inner class LiteralHelper(val name: String) {
                    infix fun with(literalsList: String): LiteralHelperHelper {
                        val list = mutableListOf<String>()
                        literals[name] = list

                        return LiteralHelperHelper(list)
                    }

                    inner class LiteralHelperHelper(private val listListList: MutableList<String>) {
                        infix fun or(other: String): LiteralHelperHelper {
                            listListList.add(other)
                            return this
                        }
                    }
                }
            }
        }
    }

    val argument = "REQUIRED_TYPE"
    val command = CommandBuilder()
    val nothing: (List<String>, MutableMap<String, String>, T) -> Unit = { _, _, _ -> }
}
