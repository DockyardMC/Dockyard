package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.netty.buffer.ByteBuf

class RepairableComponent(val materials: List<Item>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(materials.map { material -> material.getProtocolId() }, ByteBuf::writeVarInt)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofList(materials.map { material -> CRC32CHasher.ofRegistryEntry(material) }))
    }

    companion object : NetworkReadable<RepairableComponent> {
        override fun read(buffer: ByteBuf): RepairableComponent {
            return RepairableComponent(buffer.readList(ByteBuf::readVarInt).map { int -> ItemRegistry.getByProtocolId(int) })
        }
    }
}