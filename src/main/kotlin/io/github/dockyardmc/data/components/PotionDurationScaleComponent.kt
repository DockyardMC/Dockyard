package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class PotionDurationScaleComponent(val duration: Float) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(duration)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofFloat(duration))
    }

    companion object : NetworkReadable<PotionDurationScaleComponent> {
        override fun read(buffer: ByteBuf): PotionDurationScaleComponent {
            return PotionDurationScaleComponent(buffer.readFloat())
        }
    }
}