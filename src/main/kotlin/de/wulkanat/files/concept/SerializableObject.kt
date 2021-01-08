package de.wulkanat.files.concept

import de.wulkanat.extensions.ensureExists
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File

abstract class SerializableObject<T>(
    fileName: String,
    defaultText: T? = null,
    private val childSerializer: KSerializer<T>
) {
    private val json = Json { allowStructuredMapKeys = true }
    private val file = File(fileName).ensureExists(defaultText?.let { json.encodeToString(childSerializer, it) })
    var instance: T = json.decodeFromString(childSerializer, file.readText())

    fun refresh() {
        instance = json.decodeFromString(childSerializer, file.readText())
    }

    fun save() {
        file.writeText(json.encodeToString(childSerializer, instance))
    }
}
