package org.hmcore.extensions

import java.awt.Color

fun hex2Rgb(colorStr: String): Color {
    return Color(
        Integer.valueOf(colorStr.substring(1, 3), 16),
        Integer.valueOf(colorStr.substring(3, 5), 16),
        Integer.valueOf(colorStr.substring(5, 7), 16)
    )
}