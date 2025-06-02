package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readNBT
import io.github.dockyardmc.extentions.readNBTCompound
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf
import net.kyori.adventure.nbt.CompoundBinaryTag

class EntityDataComponent(val nbt: CompoundBinaryTag) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeNBT(nbt)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofNbt(nbt))
    }

    companion object : NetworkReadable<EntityDataComponent> {
        override fun read(buffer: ByteBuf): EntityDataComponent {
            return EntityDataComponent(buffer.readNBTCompound())
        }
    }

}