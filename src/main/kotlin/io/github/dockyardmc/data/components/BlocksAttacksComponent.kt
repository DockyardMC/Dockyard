package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
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

    companion object : NetworkReadable<BlocksAttacksComponent> {
        const val BLOCKS_DELAY_SECONDS_DEFAULT = 0f
        const val DISABLE_COOLDOWN_SCALE_DEFAULT = 1f

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

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(blocksDelaySeconds)
        buffer.writeFloat(disableCooldownScale)
        buffer.writeList(damageReductions, DamageReduction::write)
        itemDamageFunction.write(buffer)
        buffer.writeOptionalList(bypassedBy?.map { type -> type.getProtocolId() }, ByteBuf::writeVarInt)
        buffer.writeOptional(blockSound?.toSoundEvent(), CustomSoundEvent::write)
        buffer.writeOptional(disableSound?.toSoundEvent(), CustomSoundEvent::write)
    }

    override fun hashStruct(): HashHolder {
        return unsupported(this::class)
//        return CRC32CHasher.of {
//            default<Float>("block_delay_seconds", BLOCKS_DELAY_SECONDS_DEFAULT, blocksDelaySeconds, CRC32CHasher::ofFloat)
//            default<Float>("disable_cooldown_scale", DISABLE_COOLDOWN_SCALE_DEFAULT, disableCooldownScale, CRC32CHasher::ofFloat)
//            defaultStructList<DamageReduction>("damage_reductions", listOf(DamageReduction.DEFAULT), damageReductions, DamageReduction::hashStruct)
//            defaultStruct<ItemDamageFunction>("item_damage", ItemDamageFunction.DEFAULT, itemDamageFunction, ItemDamageFunction::hashStruct)
//            optionalList<String>("bypassed_by", bypassedBy?.map { type -> type.getEntryIdentifier() }, CRC32CHasher::ofString)
//            optional("block_sound")
    }
}


data class ItemDamageFunction(val threshold: Float, val base: Float, val factor: Float) : NetworkWritable, DataComponentHashable {

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

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("threshold", CRC32CHasher.ofFloat(threshold))
            static("base", CRC32CHasher.ofFloat(base))
            static("factor", CRC32CHasher.ofFloat(factor))
        }
    }
}

data class DamageReduction(
    val horizontalBlockingAngle: Float,
    val type: List<EntityType>?,
    val base: Float,
    val factor: Float
) : NetworkWritable, DataComponentHashable {

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

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            default("horizontal_blocking_angle", DEFAULT.horizontalBlockingAngle, horizontalBlockingAngle, CRC32CHasher::ofFloat)
            optionalList("type", type?.map { entityType -> entityType.getEntryIdentifier() }, CRC32CHasher::ofString)
            static("base", CRC32CHasher.ofFloat(base))
            static("factor", CRC32CHasher.ofFloat(factor))
        }
    }
}
