package io.github.dockyard.tests.nbt

import cz.lukynka.prettylog.log
import io.github.dockyardmc.codec.transcoder.BinaryTagTranscoder
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import net.kyori.adventure.nbt.TagStringIO
import org.junit.jupiter.api.Test

class BinaryTagTranscoderTest {

    data class User(val username: String, val permissions: List<String>, val boolianing: Boolean, val map: Map<String, Float>) {
        companion object {
            val CODEC = StructCodec.of(
                "username", Codec.STRING, User::username,
                "permissions", Codec.STRING.list(), User::permissions,
                "boolianing", Codec.BOOLEAN.default(true), User::boolianing,
                "map", Codec.STRING.mapTo(Codec.FLOAT), User::map,
                ::User
            )
        }
    }

    @Test
    fun testBinaryTagTranscoder() {
        val user = User("LukynkaCZE", listOf("dockyard.*"), false, mapOf("x" to 1f))

        val result = User.CODEC.encode(BinaryTagTranscoder, user)
        val string = TagStringIO.builder().build().asString(result)
        log(string)
    }
}