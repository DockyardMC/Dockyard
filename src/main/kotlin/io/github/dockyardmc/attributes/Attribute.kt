package io.github.dockyardmc.attributes

import io.github.dockyardmc.codec.INLINE
import io.github.dockyardmc.codec.RegistryCodec
import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashStruct
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.registry.registries.Attribute
import io.github.dockyardmc.registry.registries.AttributeRegistry
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import io.netty.buffer.ByteBuf

enum class AttributeOperation {
    ADD_VALUE,
    ADD_MULTIPLY_BASE,
    ADD_MULTIPLY_TOTAL
}

enum class AttributeSlot {
    ANY,
    MAIN_HAND,
    OFF_HAND,
    HAND,
    FEET,
    LEGS,
    CHEST,
    HEAD,
    ARMOR,
    BODY
}

data class Modifier(
    val attribute: Attribute,
    val attributeModifier: AttributeModifier,
    val equipmentSlot: EquipmentSlotGroup
) : DataComponentHashable {
    fun write(buffer: ByteBuf) {
        buffer.writeVarInt(attribute.getProtocolId())
        attributeModifier.write(buffer)
        buffer.writeEnum<EquipmentSlotGroup>(equipmentSlot)
    }

    companion object {
        val NETWORK_CODEC = Codec.of(
            "attribute", RegistryCodec.NetworkType<Attribute>(AttributeRegistry), Modifier::attribute,
            Codec.INLINE, AttributeModifier.CODEC, Modifier::attributeModifier,
            "slot", Codec.enum<EquipmentSlotGroup>(), Modifier::equipmentSlot,
            ::Modifier
        )

        fun read(buffer: ByteBuf): Modifier {
            val attribute = AttributeRegistry.getByProtocolId(buffer.readVarInt())
            val attributeModifier = AttributeModifier.read(buffer)
            val slot = buffer.readEnum<EquipmentSlotGroup>()

            return Modifier(attribute, attributeModifier, slot)
        }
    }

    override fun hashStruct(): HashStruct {
        return CRC32CHasher.of {
            static("type", CRC32CHasher.ofString(attribute.identifier))
            inline(attributeModifier.hashStruct())
            static("slot", CRC32CHasher.ofEnum(equipmentSlot))
        }
    }
}

data class AttributeModifier(
    val id: String,
    val amount: Double,
    val operation: AttributeOperation
) : DataComponentHashable {
    fun write(buffer: ByteBuf) {
        buffer.writeString(id)
        buffer.writeDouble(amount)
        buffer.writeEnum<AttributeOperation>(operation)
    }

    override fun hashStruct(): HashStruct {
        return CRC32CHasher.of {
            static("id", CRC32CHasher.ofString(id))
            static("amount", CRC32CHasher.ofDouble(amount))
            static("operation", CRC32CHasher.ofEnum(operation))
        }
    }

    companion object {
        val CODEC = Codec.of(
            "id", Codecs.String, AttributeModifier::id,
            "amount", Codecs.Double, AttributeModifier::amount,
            "operation", Codec.enum<AttributeOperation>(), AttributeModifier::operation,
            ::AttributeModifier
        )

        fun read(buffer: ByteBuf): AttributeModifier {
            return AttributeModifier(
                buffer.readString(),
                buffer.readDouble(),
                buffer.readEnum<AttributeOperation>()
            )
        }
    }
}

enum class EquipmentSlotGroup {
    ANY,
    MAIN_HAND,
    OFF_HAND,
    HAND,
    FEET,
    LEGS,
    CHEST,
    HEAD,
    ARMOR,
    BODY,
    SADDLE,
}