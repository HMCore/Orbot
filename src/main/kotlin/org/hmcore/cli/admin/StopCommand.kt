@file:OptIn(ExperimentalCli::class)

package org.hmcore.cli.admin

import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlin.system.exitProcess

class StopCommand : Subcommand("stop", "Stop the bot") {
    override fun execute() {
        exitProcess(1)
    }
}