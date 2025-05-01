package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class PotionDurationScaleComponent(val duration: Float) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(duration)
    }

    companion object : NetworkReadable<PotionDurationScaleComponent> {
        override fun read(buffer: ByteBuf): PotionDurationScaleComponent {
            return PotionDurationScaleComponent(buffer.readFloat())
        }
    }
}