@file:OptIn(ExperimentalCli::class)

package org.hmcore.cli.admin

import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import org.hmcore.Admin

class InfoCommand : Subcommand("info", "Print an overview of all servers") {
    override fun execute() {
        Admin.info()
    }
}