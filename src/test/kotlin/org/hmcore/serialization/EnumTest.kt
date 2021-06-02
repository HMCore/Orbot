package org.hmcore.serialization

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hmcore.MessageType
import org.junit.Test
import kotlin.test.assertNotNull

class EnumTest {

    @Test
    fun `Enum serialization`() {
        println(Json.encodeToString(MessageType.BLOGPOST))
        assertNotNull(MessageType.INVALID)
    }

}