package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readNBT
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBT

class CustomDataComponent(val nbt: NBT) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeNBT(nbt)
    }

    //TODO(1.21.5): NBT Hashing
    override fun hashStruct(): HashHolder {
        return unsupported(this::class)
    }

    companion object : NetworkReadable<CustomDataComponent> {

        override fun read(buffer: ByteBuf): CustomDataComponent {
            return CustomDataComponent(buffer.readNBT())
        }
    }
}