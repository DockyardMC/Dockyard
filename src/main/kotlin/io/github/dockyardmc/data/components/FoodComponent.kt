package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

data class FoodComponent(val nutrition: Int, val saturationModifier: Float, val canAlwaysEat: Boolean) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("nutrition", CRC32CHasher.ofInt(nutrition))
            static("saturation", CRC32CHasher.ofFloat(saturationModifier))
            default("can_always_eat", false, canAlwaysEat, CRC32CHasher::ofBoolean)
        }
    }

    companion object : NetworkReadable<FoodComponent> {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.VAR_INT, FoodComponent::nutrition,
            StreamCodec.FLOAT, FoodComponent::saturationModifier,
            StreamCodec.BOOLEAN, FoodComponent::canAlwaysEat,
            ::FoodComponent
        )

        override fun read(buffer: ByteBuf): FoodComponent {
            return STREAM_CODEC.read(buffer)
        }
    }
}