package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readNBT
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBT

class BlockEntityDataComponent(val nbt: NBT) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeNBT(nbt)
    }

    override fun hashStruct(): HashHolder {
        return unsupported(this::class)
    }

    companion object : NetworkReadable<BlockEntityDataComponent> {
        override fun read(buffer: ByteBuf): BlockEntityDataComponent {
            return BlockEntityDataComponent(buffer.readNBT())
        }
    }

}