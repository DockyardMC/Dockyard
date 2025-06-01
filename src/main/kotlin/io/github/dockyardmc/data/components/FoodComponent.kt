package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import io.netty.buffer.ByteBuf

class FoodComponent(val nutrition: Int, val saturationModifier: Float, val canAlwaysEat: Boolean) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        CODEC.writeNetwork(buffer, this)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {

        }
    }

    companion object : NetworkReadable<FoodComponent> {
        val CODEC = Codec.of(
            "nutrition", Codecs.VarInt, FoodComponent::nutrition,
            "saturation", Codecs.Float, FoodComponent::saturationModifier,
            "can_always_eat", Codecs.Boolean, FoodComponent::canAlwaysEat,
            ::FoodComponent
        )

        override fun read(buffer: ByteBuf): FoodComponent {
            return CODEC.readNetwork(buffer)
        }
    }
}