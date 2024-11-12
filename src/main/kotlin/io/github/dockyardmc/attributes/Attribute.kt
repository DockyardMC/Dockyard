package io.github.dockyardmc.attributes

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.item.AttributeModifiersItemComponent
import io.github.dockyardmc.item.EquipmentSlot
import io.netty.buffer.ByteBuf

data class Attribute(
    val id: Int
)

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

fun ByteBuf.readModifier(): Modifier {
    val attribute = Attribute(this.readVarInt())
    val attributeModifier = this.readAttributeModifier()
    val slot = this.readVarIntEnum<EquipmentSlotGroup>()

    return Modifier(attribute, attributeModifier, slot)
}

fun ByteBuf.readModifierList(): AttributeModifiersItemComponent {
    val size = this.readVarInt()
    val list = mutableListOf<Modifier>()
    for (i in 0 until size) {
        val modifier = this.readModifier()
        list.add(modifier)
    }
    val showInTooltip = this.readBoolean()
    return AttributeModifiersItemComponent(list, showInTooltip)
}


data class Modifier(
    val attribute: Attribute,
    val attributeModifier: AttributeModifier,
    val equipmentSlot: EquipmentSlotGroup
)

data class AttributeModifier(
    val id: String,
    val amount: Double,
    val operation: AttributeOperation
)

fun ByteBuf.readAttributeModifier(): AttributeModifier {
    return AttributeModifier(
        this.readString(),
        this.readDouble(),
        this.readVarIntEnum<AttributeOperation>()
    )
}

//fun ByteBuf.readAttribute(): Attribute {
//    val id = this.readVarInt()
//    val uuid = this.readUUID()
//    val name = this.readString()
//    val value = this.readDouble()
//    val operation = this.readVarIntEnum<AttributeOperation>()
//    val slot = this.readVarIntEnum<AttributeSlot>()
//    return Attribute(id, uuid, name, value, operation, slot)
//}
//
//fun ByteBuf.writeAttribute(attribute: Attribute) {
//    this.writeVarInt(attribute.id)
//    this.writeUUID(attribute.uuid)
//    this.writeString(attribute.name)
//    this.writeDouble(attribute.value)
//    this.writeVarIntEnum(attribute.operation)
//    this.writeVarIntEnum(attribute.slot)
//}


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
    BODY
}