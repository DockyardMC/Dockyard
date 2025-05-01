package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readNBT
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBT

class BlockEntityDataComponent(val nbt: NBT): DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeNBT(nbt)
    }

    companion object: NetworkReadable<BlockEntityDataComponent> {
        override fun read(buffer: ByteBuf): BlockEntityDataComponent {
            return BlockEntityDataComponent(buffer.readNBT())
        }
    }

}