package de.wulkanat.web

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.IOException

/**
 * Removes the first element of a saved JSON list file
 */
inline fun <reified T> removeFromSiteSave(fileName: String, amount: Int = 1) =
    File(fileName).takeIf { it.exists() }?.let {
        it.writeText(
            if (amount >= 0) Json.encodeToString(
                Json.decodeFromString<List<T>>(it.readText()).subList(0, amount)
            )
            else "[]"
        )
    }

inline fun <reified T> updateSite(url: String, fileName: String, parser: (Document) -> List<T>) = try {
    val currentStateFile = File(fileName)

    val retrievedElements = parser(Jsoup.connect(url).get())
    var currentElements = if (currentStateFile.exists())
        Json.decodeFromString(currentStateFile.readText()) else retrievedElements

    val newElements = retrievedElements - currentElements
    currentElements = retrievedElements
    currentStateFile.writeText(Json.encodeToString(currentElements))

    newElements
} catch (e: IOException) {
    // TODO: put this somewhere else
    // Admin.error("""Fetching "$url" failed!""", e.message ?: e.localizedMessage)
    // DiscordRpc.updatePresence(canUpdate.also { canUpdate = false })

    null
}
