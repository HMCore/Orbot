package de.wulkanat.cli

fun <T>makeCli(prefix: String = "!", func: Cli<T>.() -> Unit): Cli<T> {
    val cli = Cli<T>(prefix)
    cli.func()
    return cli
}
