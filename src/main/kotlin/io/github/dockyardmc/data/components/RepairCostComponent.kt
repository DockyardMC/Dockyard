package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class RepairCostComponent(val cost: Int): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(cost)
    }

    companion object: NetworkReadable<RepairCostComponent> {
        override fun read(buffer: ByteBuf): RepairCostComponent {
            return RepairCostComponent(buffer.readVarInt())
        }
    }
}