package io.github.dockyardmc.data.components

import io.github.dockyardmc.codec.RegistryCodec
import io.github.dockyardmc.codec.transcoder.CRC32CTranscoder
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.TrimMaterial
import io.github.dockyardmc.registry.registries.TrimMaterialRegistry
import io.github.dockyardmc.registry.registries.TrimPattern
import io.github.dockyardmc.registry.registries.TrimPatternRegistry
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

data class ArmorTrimComponent(val material: TrimMaterial, val pattern: TrimPattern) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CODEC.encode(CRC32CTranscoder, this))
    }

    companion object : NetworkReadable<ArmorTrimComponent> {
        val CODEC = StructCodec.of(
            "material", RegistryCodec.codec(TrimMaterialRegistry), ArmorTrimComponent::material,
            "pattern", RegistryCodec.codec(TrimPatternRegistry), ArmorTrimComponent::pattern,
            ::ArmorTrimComponent
        )

        val STREAM_CODEC = StreamCodec.of(
            RegistryCodec.stream(TrimMaterialRegistry), ArmorTrimComponent::material,
            RegistryCodec.stream(TrimPatternRegistry), ArmorTrimComponent::pattern,
            ::ArmorTrimComponent
        )

        override fun read(buffer: ByteBuf): ArmorTrimComponent {
            return STREAM_CODEC.read(buffer)
        }
    }
}