package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class RepairableComponent(val materials: List<Item>): DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(materials.map { material -> material.getProtocolId() }, ByteBuf::writeVarInt)
    }

    companion object: NetworkReadable<RepairableComponent> {
        override fun read(buffer: ByteBuf): RepairableComponent {
            return RepairableComponent(buffer.readList(ByteBuf::readVarInt).map { int -> ItemRegistry.getByProtocolId(int) })
        }
    }
}