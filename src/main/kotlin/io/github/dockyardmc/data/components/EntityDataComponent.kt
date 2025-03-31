package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readNBT
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBT

class EntityDataComponent(val nbt: NBT): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeNBT(nbt)
    }

    companion object: NetworkReadable<EntityDataComponent> {
        override fun read(buffer: ByteBuf): EntityDataComponent {
            return EntityDataComponent(buffer.readNBT())
        }
    }

}