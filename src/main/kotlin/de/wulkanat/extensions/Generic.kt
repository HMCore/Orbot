package de.wulkanat.extensions

inline fun <T> Boolean.alsoIf(other: T, body: () -> Unit): Boolean {
    if (this == other) {
        body()
    }
    return this
}
