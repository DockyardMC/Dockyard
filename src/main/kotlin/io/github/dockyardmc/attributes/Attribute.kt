package io.github.dockyardmc.attributes

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.item.AttributeModifiersItemComponent
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

fun ByteBuf.readModifierList(): AttributeModifiersItemComponent {
    val size = this.readVarInt()
    val list = mutableListOf<Modifier>()
    for (i in 0 until size) {
        val modifier = Modifier.read(this)
        list.add(modifier)
    }
    val showInTooltip = this.readBoolean()
    return AttributeModifiersItemComponent(list, showInTooltip)
}


data class Modifier(
    val attribute: Attribute,
    val attributeModifier: AttributeModifier,
    val equipmentSlot: EquipmentSlotGroup
) {
    fun write(buffer: ByteBuf) {
        buffer.writeVarInt(attribute.getProtocolId())
        attributeModifier.write(buffer)
        buffer.writeVarIntEnum<EquipmentSlotGroup>(equipmentSlot)
    }

    companion object {
        fun read(buffer: ByteBuf): Modifier {
            val attribute = AttributeRegistry.getByProtocolId(buffer.readVarInt())
            val attributeModifier = AttributeModifier.read(buffer)
            val slot = buffer.readVarIntEnum<EquipmentSlotGroup>()

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
        buffer.writeVarIntEnum<AttributeOperation>(operation)
    }

    companion object {
        fun read(buffer: ByteBuf): AttributeModifier {
            return AttributeModifier(
                buffer.readString(),
                buffer.readDouble(),
                buffer.readVarIntEnum<AttributeOperation>()
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