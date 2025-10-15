package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.utils.MojangUtil
import java.util.*

data class PlayerSkin(val textures: String, val signature: String) {

    companion object {
        fun fetchFromUuid(uuid: UUID): PlayerSkin? {
            var returnValue: PlayerSkin? = null
            MojangUtil.getSkinFromUUID(uuid).thenAccept { data ->
                if (data == null) return@thenAccept
                if (data.signature == null) return@thenAccept
                returnValue = PlayerSkin(data.value, data.signature)
            }
            return returnValue
        }
    }

    data class Patch(
        val body: String?,
        val cape: String?,
        val elytra: String?,
        val slim: Boolean?
    ) {
        companion object {
            val EMPTY = Patch(null, null, null, null)

            val STREAM_CODEC = StreamCodec.of(
                StreamCodec.STRING.optional(), Patch::body,
                StreamCodec.STRING.optional(), Patch::cape,
                StreamCodec.STRING.optional(), Patch::elytra,
                StreamCodec.BOOLEAN.optional(), Patch::slim,
                ::Patch
            )

            val CODEC = StructCodec.of(
                "body", Codec.STRING.optional(), Patch::body,
                "cape", Codec.STRING.optional(), Patch::cape,
                "elytra", Codec.STRING.optional(), Patch::elytra,
                "slim", Codec.BOOLEAN.optional(), Patch::slim,
                ::Patch
            )
        }
    }
}