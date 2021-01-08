package de.wulkanat.cli

enum class ArgumentType(val match: Regex, val stringName: String) {
    INT(Regex("\\d+"), "int"),
    FLOAT(Regex("\\d+(?:.\\d+)?"), "float"),
    STRING(Regex("[\\s\\S]+"), "string"),
    BOOLEAN(Regex("true|false"), "bool"),
    LITERAL(Regex("[\\s\\S]+"), "literal"),
    EXISTS(Regex("[\\s\\S]+"), "existence");

    fun usage(literals: Map<String, List<String>>, name: String): String {
        return when (this) {
            LITERAL -> "${literals[name]?.joinToString(separator = "|")}"
            EXISTS -> ""
            else -> stringName
        }
    }
}