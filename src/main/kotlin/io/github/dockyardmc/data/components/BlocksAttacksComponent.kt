package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.toSoundEvent
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.*
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.registry.registries.EntityTypeRegistry
import io.github.dockyardmc.sounds.CustomSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.netty.buffer.ByteBuf

class BlocksAttacksComponent(
    val blocksDelaySeconds: Float,
    val disableCooldownScale: Float,
    val damageReductions: List<DamageReduction>,
    val itemDamageFunction: ItemDamageFunction,
    val bypassedBy: List<EntityType>?,
    val blockSound: String?,
    val disableSound: String?
) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(blocksDelaySeconds)
        buffer.writeFloat(disableCooldownScale)
        buffer.writeList(damageReductions, DamageReduction::write)
        itemDamageFunction.write(buffer)
        buffer.writeOptionalList(bypassedBy?.map { type -> type.getProtocolId() }, ByteBuf::writeVarInt)
        buffer.writeOptional(blockSound?.toSoundEvent(), CustomSoundEvent::write)
        buffer.writeOptional(disableSound?.toSoundEvent(), CustomSoundEvent::write)
    }

    companion object : NetworkReadable<BlocksAttacksComponent> {
        override fun read(buffer: ByteBuf): BlocksAttacksComponent {
            return BlocksAttacksComponent(
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readList(DamageReduction::read),
                ItemDamageFunction.read(buffer),
                buffer.readOptional { b -> b.readList(ByteBuf::readVarInt).map { int -> EntityTypeRegistry.getByProtocolId(int) } },
                SoundEvent.read(buffer).identifier,
                SoundEvent.read(buffer).identifier,
            )
        }
    }

    data class ItemDamageFunction(val threshold: Float, val base: Float, val factor: Float) : NetworkWritable {

        override fun write(buffer: ByteBuf) {
            buffer.writeFloat(threshold)
            buffer.writeFloat(base)
            buffer.writeFloat(factor)
        }

        companion object : NetworkReadable<ItemDamageFunction> {

            val DEFAULT = ItemDamageFunction(1f, 0f, 1f)

            override fun read(buffer: ByteBuf): ItemDamageFunction {
                return ItemDamageFunction(buffer.readFloat(), buffer.readFloat(), buffer.readFloat())
            }
        }
    }

    data class DamageReduction(
        val horizontalBlockingAngle: Float,
        val type: List<EntityType>?,
        val base: Float,
        val factor: Float
    ) : NetworkWritable {

        override fun write(buffer: ByteBuf) {
            buffer.writeFloat(horizontalBlockingAngle)
            buffer.writeOptionalList(type?.map { entityType -> entityType.getProtocolId() }, ByteBuf::writeVarInt)
            buffer.writeFloat(base)
            buffer.writeFloat(factor)
        }

        companion object : NetworkReadable<DamageReduction> {

            val DEFAULT = DamageReduction(90.0f, null, 0.0f, 1.0f)

            override fun read(buffer: ByteBuf): DamageReduction {
                return DamageReduction(
                    buffer.readFloat(),
                    buffer.readOptional { b -> b.readList(ByteBuf::readVarInt).map { int -> EntityTypeRegistry.getByProtocolId(int) } },
                    buffer.readFloat(),
                    buffer.readFloat()
                )
            }
        }
    }
}