package io.github.dockyardmc.attributes

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.netty.buffer.ByteBuf
import java.util.UUID

data class Attribute(
    val id: Int,
    val uuid: UUID,
    val name: String,
    val value: Double,
    val operation: AttributeOperation,
    val slot: AttributeSlot,
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

fun ByteBuf.readAttribute(): Attribute {
    val id = this.readVarInt()
    val uuid = this.readUUID()
    val name = this.readString()
    val value = this.readDouble()
    val operation = this.readVarIntEnum<AttributeOperation>()
    val slot = this.readVarIntEnum<AttributeSlot>()
    return Attribute(id, uuid, name, value, operation, slot)
}