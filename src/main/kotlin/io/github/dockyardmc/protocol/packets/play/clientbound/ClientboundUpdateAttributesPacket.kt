package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.attributes.AttributeModifier
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.readList
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.writeList
import io.github.dockyardmc.registry.registries.Attribute
import io.github.dockyardmc.registry.registries.AttributeRegistry
import io.netty.buffer.ByteBuf

class ClientboundUpdateAttributesPacket(val entity: Entity, val properties: List<Property>): ClientboundPacket() {

    init {
        buffer.writeVarInt(entity.id)
        buffer.writeList(properties, Property::write)
    }

    data class Property(val attribute: Attribute, val value: Double, val modifiers: List<AttributeModifier>): NetworkWritable {

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(attribute.getProtocolId())
            buffer.writeDouble(value)
            buffer.writeList(modifiers, AttributeModifier::write)
        }

        companion object: NetworkReadable<Property> {

            override fun read(buffer: ByteBuf): Property {
                return Property(
                    attribute = AttributeRegistry.getByProtocolId(buffer.readVarInt()),
                    value = buffer.readDouble(),
                    modifiers = buffer.readList(AttributeModifier::read)
                )
            }
        }
    }
}