package io.github.dockyard.tests.hashing

import io.github.dockyardmc.codec.transcoder.CRC32CTranscoder
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.components.UseCooldownComponent
import io.github.dockyardmc.tide.codec.Codec
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TranscoderTest {

    data class TestHash(val component: DataComponent, val codec: Codec<out DataComponent>, val expectedHash: Int)

    val expectedHashes = listOf<TestHash>(
        TestHash(UseCooldownComponent(1.6f, "minecraft:test"), UseCooldownComponent.CODEC, 493336604),
    )

    @Suppress("UNCHECKED_CAST")
    @Test
    fun test() {
        expectedHashes.forEach { testHash ->
            val codec = testHash.codec as Codec<DataComponent>
            assertEquals(testHash.expectedHash, codec.encode(CRC32CTranscoder, testHash.component))
        }
    }
}