package io.github.dockyard.tests.network

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class VarIntTest {

    @Test
    fun testBoundaryValues() {
        testValue(0)
        testValue(127)
        testValue(128)
        testValue(16383)
        testValue(16384)
        testValue(2097151)
        testValue(2097152)
        testValue(268435455)
        testValue(268435456)
        testValue(Int.MAX_VALUE)
        testValue(Int.MIN_VALUE)
    }

    private fun testValue(value: Int) {
        val buffer = Unpooled.buffer()
        buffer.writeVarInt(value)
        val read = buffer.readVarInt()
        assertEquals(value, read)
    }
}