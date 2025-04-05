package io.github.dockyardmc.attributes

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.registry.registries.Attribute
import io.github.dockyardmc.registry.registries.AttributeRegistry
import io.netty.buffer.ByteBuf

enum class AttributeOperation {
    ADD,
    MULTIPLY_BASE,
    MULTIPLY_TOTAL
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
) {
    fun write(buffer: ByteBuf) {
        buffer.writeVarInt(attribute.getProtocolId())
        attributeModifier.write(buffer)
        buffer.writeEnum<EquipmentSlotGroup>(equipmentSlot)
    }

    companion object {
        fun read(buffer: ByteBuf): Modifier {
            val attribute = AttributeRegistry.getByProtocolId(buffer.readVarInt())
            val attributeModifier = AttributeModifier.read(buffer)
            val slot = buffer.readEnum<EquipmentSlotGroup>()

            return Modifier(attribute, attributeModifier, slot)
        }
    }
}

data class AttributeModifier(
    val id: String,
    val amount: Double,
    val operation: AttributeOperation
) {
    fun write(buffer: ByteBuf) {
        buffer.writeString(id)
        buffer.writeDouble(amount)
        buffer.writeEnum<AttributeOperation>(operation)
    }

    companion object {
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