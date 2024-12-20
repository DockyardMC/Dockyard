package io.github.dockyardmc.attributes

import io.github.dockyardmc.extentions.*
import io.netty.buffer.ByteBuf

data class Attribute(
    val id: Int
) {
    companion object {
        val template = NetworkTemplate.of<Attribute> {
            read { buffer ->
                Attribute(buffer.readVarInt())
            }

            write { attribute, buffer ->
                buffer.writeVarInt(attribute.id)
            }
        }
    }
}

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

class NetworkTemplate<T>(private val read: (ByteBuf) -> T, private val write: (T, ByteBuf) -> Unit) {

    companion object {
        inline fun <reified T> of(builder: NetworkTemplateBuilder<T>.() -> Unit): NetworkTemplate<T> {
            val networkTemplateBuilder = NetworkTemplateBuilder<T>()
            builder.invoke(networkTemplateBuilder)
            if(networkTemplateBuilder.codecReader == null) throw IllegalArgumentException("Reader is null for network template of class ${T::class.simpleName}")
            if(networkTemplateBuilder.codecWriter == null) throw IllegalArgumentException("Writer is null for network template of class ${T::class.simpleName}")
            return NetworkTemplate<T>(networkTemplateBuilder.codecReader!!, networkTemplateBuilder.codecWriter!!)
        }
    }

    fun read(buffer: ByteBuf): T {
        return read.invoke(buffer)
    }

    fun write(buffer: ByteBuf, value: T) {
        write.invoke(value, buffer)
    }
}

class NetworkTemplateBuilder<T> {
    var codecReader: ((ByteBuf) -> T)? = null
    var codecWriter: ((T, ByteBuf) -> Unit)? = null

    fun read(builder: (ByteBuf) -> T) {
        codecReader = builder
    }

    fun write(builder: (T, ByteBuf) -> Unit) {
        codecWriter = builder
    }

}

fun <T> ByteBuf.writeList(list: Collection<T>, writer: (buffer: ByteBuf, value: T) -> Unit) {
    this.writeVarInt(list.size)
    list.forEach { item ->
        writer.invoke(this, item)
    }
}

fun <T> ByteBuf.readList(reader: (buffer: ByteBuf) -> T): List<T> {
    val size = this.readVarInt()
    val list = mutableListOf<T>()
    for (i in 0 until size) {
        list.add(reader.invoke(this))
    }
    return list
}


data class Modifier(
    val attribute: Attribute,
    val attributeModifier: AttributeModifier,
    val equipmentSlot: EquipmentSlotGroup
) {

    companion object {
        val template = NetworkTemplate.of<Modifier> {
            write { modifier, buffer ->
                Attribute.template.write(buffer, modifier.attribute)
                modifier.attributeModifier.write(buffer)
                buffer.writeVarIntEnum<EquipmentSlotGroup>(modifier.equipmentSlot)
            }

            read { buffer ->
                val attribute = Attribute(buffer.readVarInt())
                val attributeModifier = AttributeModifier.read(buffer)
                val slot = buffer.readVarIntEnum<EquipmentSlotGroup>()

                Modifier(attribute, attributeModifier, slot)
            }
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
    BODY
}