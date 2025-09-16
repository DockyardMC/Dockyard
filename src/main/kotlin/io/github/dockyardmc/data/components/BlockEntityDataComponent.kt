package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readNBTCompound
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf
import net.kyori.adventure.nbt.CompoundBinaryTag

class BlockEntityDataComponent(val nbt: CompoundBinaryTag) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeNBT(nbt)
    }

    override fun hashStruct(): HashHolder {
        return unsupported(this)
    }

    companion object : NetworkReadable<BlockEntityDataComponent> {
        override fun read(buffer: ByteBuf): BlockEntityDataComponent {
            return BlockEntityDataComponent(buffer.readNBTCompound())
        }
    }

}