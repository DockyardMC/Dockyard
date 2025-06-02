package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readNBTCompound
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf
import net.kyori.adventure.nbt.CompoundBinaryTag

class LockComponent(val data: CompoundBinaryTag) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeNBT(data)
    }

    companion object : NetworkReadable<LockComponent> {
        override fun read(buffer: ByteBuf): LockComponent {
            return LockComponent(buffer.readNBTCompound())
        }
    }
}