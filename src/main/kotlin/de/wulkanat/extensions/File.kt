package de.wulkanat.extensions

import java.io.File

fun File.ensureExists(defaultText: String? = null): File {
    if (!this.exists()) {
        this.createNewFile()
        this.writeText(defaultText ?: return this)
    }
    return this
}