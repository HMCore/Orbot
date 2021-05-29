package org.hmcore.extensions

import io.mockk.*
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class FileTest {
    @Test
    fun `Ensure exists should create a file with default content if it does not exist`() {
        val file = mockk<File>()
        mockkStatic("kotlin.io.FilesKt__FileReadWriteKt")

        every { file.exists() } returns false
        every { file.createNewFile() } returns true
        every { file.writeText(any()) } just Runs

        file.ensureExists("Default Text")

        verifySequence {
            file.exists()
            file.createNewFile()
            file.writeText("Default Text")
        }
    }

    @Test
    fun `Ensure exists should create a file if supplied with null but not write text`() {
        val file = mockk<File>()
        mockkStatic("kotlin.io.FilesKt__FileReadWriteKt")

        every { file.exists() } returns false
        every { file.createNewFile() } returns true
        every { file.writeText(any()) } just Runs

        file.ensureExists(null)

        verifySequence {
            file.exists()
            file.createNewFile()
        }
    }

    @Test
    fun `Ensure exists should not touch the file if it exists`() {
        val file = mockk<File>()
        mockkStatic("kotlin.io.FilesKt__FileReadWriteKt")

        every { file.exists() } returns true
        every { file.createNewFile() } returns true
        every { file.writeText(any()) } just Runs

        file.ensureExists(null)

        verifySequence {
            file.exists()
        }
    }

    @Test
    fun `Ensure exists should return itself`() {
        val file = mockk<File>()
        mockkStatic("kotlin.io.FilesKt__FileReadWriteKt")

        every { file.exists() } returns true
        every { file.createNewFile() } returns true
        every { file.writeText(any()) } just Runs

        val f2 = file.ensureExists(null)

        assertEquals(file, f2)
    }
}