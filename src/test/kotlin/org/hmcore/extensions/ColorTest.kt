package org.hmcore.extensions

import org.junit.Assert.assertEquals
import org.junit.Test
import java.awt.Color

class ColorTest {
    @Test
    fun `color should parse from hex correctly`() {
        assertEquals(hex2Rgb("#FFFFFF"), Color.WHITE)
        assertEquals(hex2Rgb("#000000"), Color.BLACK)
        assertEquals(hex2Rgb("#FF0000"), Color.RED)
        assertEquals(hex2Rgb("#00FF00"), Color.GREEN)
        assertEquals(hex2Rgb("#0000FF"), Color.BLUE)
    }

    @Test
    fun `color should convert to RGB correctly`() {
        assertEquals(16777215, Color.WHITE.toRgb())
        assertEquals(0, Color.BLACK.toRgb())
        assertEquals(16711680, Color.RED.toRgb())
        assertEquals(65280, Color.GREEN.toRgb())
        assertEquals(255, Color.BLUE.toRgb())
    }
}