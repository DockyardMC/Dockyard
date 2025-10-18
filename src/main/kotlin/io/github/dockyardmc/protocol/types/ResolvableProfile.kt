package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.tide.types.Either
import java.util.*

data class ResolvableProfile(val profile: Either<GameProfile, Partial>, val patch: PlayerSkin.Patch) {

    constructor(profile: GameProfile) : this(Either.left(profile), PlayerSkin.Patch.EMPTY)

    constructor(profile: GameProfile, patch: PlayerSkin.Patch) : this(Either.left(profile), patch)

    constructor(partial: Partial) : this(Either.right(partial), PlayerSkin.Patch.EMPTY)

    constructor(partial: Partial, patch: PlayerSkin.Patch) : this(Either.right(partial), patch)

    companion object {
        val EMPTY = ResolvableProfile(Either.right(Partial.EMPTY), PlayerSkin.Patch.EMPTY)

        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.either(GameProfile.STREAM_CODEC, Partial.STREAM_CODEC), ResolvableProfile::profile,
            PlayerSkin.Patch.STREAM_CODEC, ResolvableProfile::patch,
            ::ResolvableProfile
        )

        val CODEC = StructCodec.of(
            StructCodec.INLINE, Codec.either(GameProfile.CODEC, Partial.CODEC), ResolvableProfile::profile,
            StructCodec.INLINE, PlayerSkin.Patch.CODEC, ResolvableProfile::patch,
            ::ResolvableProfile
        )
    }

    data class Partial(val name: String?, val uuid: UUID?, val properties: List<GameProfile.Property>) {
        companion object {
            val EMPTY = Partial(null, null, listOf())
            val STREAM_CODEC = StreamCodec.of(
                StreamCodec.STRING.optional(), Partial::name,
                StreamCodec.UUID.optional(), Partial::uuid,
                GameProfile.Property.STREAM_CODEC.list(), Partial::properties,
                ::Partial
            )

            val CODEC = StructCodec.of(
                "name", Codec.STRING.optional(), Partial::name,
                "uuid", Codec.UUID.optional(), Partial::uuid,
                "properties", GameProfile.Property.CODEC.list(), Partial::properties,
                ::Partial
            )
        }
    }
}