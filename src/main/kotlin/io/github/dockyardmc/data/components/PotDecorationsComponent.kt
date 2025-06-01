package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.netty.buffer.ByteBuf

data class PotDecorationsComponent(val back: Item, val left: Item, val right: Item, val front: Item) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(back.getProtocolId())
        buffer.writeVarInt(left.getProtocolId())
        buffer.writeVarInt(right.getProtocolId())
        buffer.writeVarInt(front.getProtocolId())
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofList(listOf(back, left, right, front).map { face -> CRC32CHasher.ofRegistryEntry(face) }))
    }

    companion object : NetworkReadable<PotDecorationsComponent> {

        val DEFAULT_ITEM = Items.BRICK
        val EMPTY = PotDecorationsComponent(DEFAULT_ITEM, DEFAULT_ITEM, DEFAULT_ITEM, DEFAULT_ITEM)

        override fun read(buffer: ByteBuf): PotDecorationsComponent {
            return PotDecorationsComponent(
                buffer.readVarInt().let { int -> ItemRegistry.getByProtocolId(int) },
                buffer.readVarInt().let { int -> ItemRegistry.getByProtocolId(int) },
                buffer.readVarInt().let { int -> ItemRegistry.getByProtocolId(int) },
                buffer.readVarInt().let { int -> ItemRegistry.getByProtocolId(int) },
            )
        }
    }
}