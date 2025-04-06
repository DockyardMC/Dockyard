package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class FoodComponent(val nutrition: Int, val saturationModifier: Float, val canAlwaysEat: Boolean): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(nutrition)
        buffer.writeFloat(saturationModifier)
        buffer.writeBoolean(canAlwaysEat)
    }

    companion object: NetworkReadable<FoodComponent> {
        override fun read(buffer: ByteBuf): FoodComponent {
            return FoodComponent(buffer.readVarInt(), buffer.readFloat(), buffer.readBoolean())
        }
    }
}