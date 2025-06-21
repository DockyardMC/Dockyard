package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.types.EquipmentSlot
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.protocol.writeOptionalList
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.registry.registries.EntityTypeRegistry
import io.github.dockyardmc.sounds.BuiltinSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.netty.buffer.ByteBuf

class EquippableComponent(
    val equipmentSlot: EquipmentSlot,
    val equipSound: SoundEvent,
    val assetId: String?,
    val cameraOverlay: String?,
    val allowedEntities: List<EntityType>?,
    val dispensable: Boolean,
    val swappable: Boolean,
    val damageOnHurt: Boolean,
    val equipOnInteract: Boolean,
    val canBeSheared: Boolean,
    val shearingSounds: SoundEvent
) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(equipmentSlot)
        equipSound.write(buffer)
        buffer.writeOptional(assetId, ByteBuf::writeString)
        buffer.writeOptional(cameraOverlay, ByteBuf::writeString)
        buffer.writeOptionalList(allowedEntities?.map { type -> type.getProtocolId() }, ByteBuf::writeVarInt)
        buffer.writeBoolean(dispensable)
        buffer.writeBoolean(swappable)
        buffer.writeBoolean(damageOnHurt)
        buffer.writeBoolean(equipOnInteract)
        buffer.writeBoolean(canBeSheared)
        shearingSounds.write(buffer)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("slot", CRC32CHasher.ofEnum(equipmentSlot))
            defaultStruct<SoundEvent>("equip_sound", BuiltinSoundEvent.of(Sounds.ITEM_ARMOR_EQUIP_GENERIC), equipSound, SoundEvent::hashStruct)
            optional("asset_id", assetId, CRC32CHasher::ofString)
            optional("camera_overlay", cameraOverlay, CRC32CHasher::ofString)
            optionalList("allowed_entities", allowedEntities, CRC32CHasher::ofRegistryEntry)
            default("dispensable", true, dispensable, CRC32CHasher::ofBoolean)
            default("swappable", true, swappable, CRC32CHasher::ofBoolean)
            default("damage_on_hurt", true, damageOnHurt, CRC32CHasher::ofBoolean)
            default("equip_on_interact", false, equipOnInteract, CRC32CHasher::ofBoolean)
            default("can_be_sheared", false, canBeSheared, CRC32CHasher::ofBoolean)
            defaultStruct("shearing_sound", DEFAULT_SHEARING_SOUND, shearingSounds, SoundEvent::hashStruct)
        }
    }

    companion object : NetworkReadable<EquippableComponent> {

        val DEFAULT_SHEARING_SOUND = BuiltinSoundEvent.of(Sounds.ITEM_SHEARS_SNIP)

        override fun read(buffer: ByteBuf): EquippableComponent {
            return EquippableComponent(
                buffer.readEnum(),
                SoundEvent.read(buffer),
                buffer.readOptional(ByteBuf::readString),
                buffer.readOptional(ByteBuf::readString),
                buffer.readOptional { b -> b.readList(ByteBuf::readVarInt).map { int -> EntityTypeRegistry.getByProtocolId(int) } },
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                SoundEvent.read(buffer)
            )
        }
    }
}