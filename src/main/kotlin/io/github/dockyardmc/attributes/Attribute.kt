package io.github.dockyardmc.attributes

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.HashStruct
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.registry.registries.Attribute
import io.github.dockyardmc.registry.registries.AttributeRegistry
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
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
    val equipmentSlot: EquipmentSlotGroup,
    val display: Display
) : DataComponentHashable {

    fun write(buffer: ByteBuf) {
        buffer.writeVarInt(attribute.getProtocolId())
        attributeModifier.write(buffer)
        buffer.writeEnum<EquipmentSlotGroup>(equipmentSlot)
        display.write(buffer)
    }

    companion object {

        fun read(buffer: ByteBuf): Modifier {
            val attribute = AttributeRegistry.getByProtocolId(buffer.readVarInt())
            val attributeModifier = AttributeModifier.STREAM_CODEC.read(buffer)
            val slot = buffer.readEnum<EquipmentSlotGroup>()
            val display = Display.read(buffer)

            return Modifier(attribute, attributeModifier, slot, display)
        }
    }

    override fun hashStruct(): HashStruct {
        return CRC32CHasher.of {
            static("type", CRC32CHasher.ofString(attribute.identifier))
            inline(attributeModifier.hashStruct())
            default("slot", EquipmentSlotGroup.ANY, equipmentSlot, CRC32CHasher::ofEnum)
            defaultStruct("display", Display.Default.INSTANCE, display, Display::hashStruct)
        }
    }

    interface Display : NetworkWritable, DataComponentHashable {

        val type: Type

        enum class Type {
            DEFAULT,
            HIDDEN,
            OVERRIDE
        }

        override fun write(buffer: ByteBuf) {
            buffer.writeEnum(type)
        }

        companion object : NetworkReadable<Display> {
            override fun read(buffer: ByteBuf): Display {
                val type = buffer.readEnum<Type>()
                return when (type) {
                    Type.DEFAULT -> Default.read(buffer)
                    Type.HIDDEN -> Hidden.read(buffer)
                    Type.OVERRIDE -> Override.read(buffer)
                }
            }

        }

        class Default : Display {
            override val type: Type = Type.DEFAULT

            companion object : NetworkReadable<Default> {
                val INSTANCE = Default()
                override fun read(buffer: ByteBuf): Default {
                    return INSTANCE
                }
            }

            override fun hashStruct(): HashHolder {
                return CRC32CHasher.of {
                    static("type", CRC32CHasher.ofEnum(type))
                }
            }
        }

        class Hidden : Display {
            override val type: Type = Type.HIDDEN

            companion object : NetworkReadable<Hidden> {
                val INSTANCE = Hidden()
                override fun read(buffer: ByteBuf): Hidden {
                    return INSTANCE
                }
            }

            override fun hashStruct(): HashHolder {
                return CRC32CHasher.of {
                    static("type", CRC32CHasher.ofEnum(type))
                }
            }
        }

        class Override(val component: Component) : Display {
            override val type: Type = Type.OVERRIDE

            override fun write(buffer: ByteBuf) {
                super.write(buffer)
                buffer.writeTextComponent(component)
            }

            companion object : NetworkReadable<Override> {
                override fun read(buffer: ByteBuf): Override {
                    return Override(buffer.readTextComponent())
                }
            }

            override fun hashStruct(): HashHolder {
                return CRC32CHasher.of {
                    static("type", CRC32CHasher.ofEnum(type))
                    static("value", CRC32CHasher.ofComponent(component))
                }
            }
        }
    }
}

data class AttributeModifier(
    val id: String,
    val amount: Double,
    val operation: AttributeOperation
) : DataComponentHashable {

    fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }

    override fun hashStruct(): HashStruct {
        return CRC32CHasher.of {
            static("id", CRC32CHasher.ofString(id))
            static("amount", CRC32CHasher.ofDouble(amount))
            static("operation", CRC32CHasher.ofEnum(operation))
        }
    }

    companion object {
        val CODEC = StructCodec.of(
            "id", Codec.STRING, AttributeModifier::id,
            "amount", Codec.DOUBLE, AttributeModifier::amount,
            "operation", Codec.enum<AttributeOperation>(), AttributeModifier::operation,
            ::AttributeModifier
        )

        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.STRING, AttributeModifier::id,
            StreamCodec.DOUBLE, AttributeModifier::amount,
            StreamCodec.enum<AttributeOperation>(), AttributeModifier::operation,
            ::AttributeModifier
        )
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