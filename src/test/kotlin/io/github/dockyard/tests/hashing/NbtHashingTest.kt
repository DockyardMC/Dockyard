package io.github.dockyard.tests.hashing

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.data.NbtHasher
import net.kyori.adventure.nbt.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NbtHashingTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testHashingCompound() {
        val byteArray = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F)

        val innerCompound = CompoundBinaryTag.builder()
            .putBoolean("gay", true)
            .putByte("testing", 1.toByte())
            .putString("id", "minecraft:estrogen_potion")
            .putByteArray("bytes", byteArray)
            .putDouble("gay_factor", 3.3)
            .putFloat("gay_offset", 5f)
            .build()

        val compound = CompoundBinaryTag.builder()
            .put("test", IntBinaryTag.intBinaryTag(5))
            .putBoolean("gay", true)
            .putString("maya", "lukynka")
            .put("list", ListBinaryTag.from(listOf()))
            .putIntArray("yas", intArrayOf(1, 2, 3, 4))
            .put(innerCompound)
            .put("named", innerCompound)
            .build()

        val hashed = NbtHasher.hashCompound(compound)
        assertEquals(-376937653, hashed)
    }

    @Test
    fun testHashing() {
        val byteArray = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F)
        val list = listOf(
            IntBinaryTag.intBinaryTag(6),
            IntBinaryTag.intBinaryTag(9)
        )

        val expectedHashes = mapOf<BinaryTag, Int>(
            IntBinaryTag.intBinaryTag(5) to 645064431,
            StringBinaryTag.stringBinaryTag("gay") to -1787378533,
            ByteBinaryTag.byteBinaryTag(1.toByte()) to 1791337955,
            DoubleBinaryTag.doubleBinaryTag(4.0) to 1686600224,
            FloatBinaryTag.floatBinaryTag(3.3f) to 1657948734,
            ShortBinaryTag.shortBinaryTag(69.toShort()) to -1230243652,
            LongBinaryTag.longBinaryTag(420L) to -2018045660,
            ByteArrayBinaryTag.byteArrayBinaryTag(*byteArray) to 1390915034,
            IntArrayBinaryTag.intArrayBinaryTag(1, 2, 3, 4) to 439006708,
            LongArrayBinaryTag.longArrayBinaryTag(1L, 2L, 3L, 4L) to -375226994,
            ListBinaryTag.from(list) to -1644605023
        )

        expectedHashes.forEach { (nbt, hash) ->
            assertEquals(hash, NbtHasher.hashTag(nbt))
        }
    }
}