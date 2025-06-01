package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.fromRGBInt
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.extentions.writePackedInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.ByteBuf

class FireworkExplosionComponent(
    val shape: Shape,
    val colors: List<CustomColor>,
    val fadeColors: List<CustomColor>,
    val hasTrail: Boolean,
    val hasTwinkle: Boolean
) : DataComponent() {

    override fun hashStruct(): HashHolder {
        return unsupported(this)
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(shape)
        buffer.writeList(colors, CustomColor::writePackedInt)
        buffer.writeList(fadeColors, CustomColor::writePackedInt)
        buffer.writeBoolean(hasTrail)
        buffer.writeBoolean(hasTwinkle)
    }

    companion object : NetworkReadable<FireworkExplosionComponent> {
        override fun read(buffer: ByteBuf): FireworkExplosionComponent {
            return FireworkExplosionComponent(
                buffer.readEnum(),
                buffer.readList(ByteBuf::readInt).map { int -> CustomColor.fromRGBInt(int) },
                buffer.readList(ByteBuf::readInt).map { int -> CustomColor.fromRGBInt(int) },
                buffer.readBoolean(),
                buffer.readBoolean()
            )
        }
    }

    enum class Shape {
        SMALL_BALL,
        LARGE_BALL,
        STAR,
        CREEPER,
        BURST
    }
}


