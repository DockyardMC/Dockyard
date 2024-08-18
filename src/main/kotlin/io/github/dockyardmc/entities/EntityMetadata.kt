package io.github.dockyardmc.entities

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.utils.*
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.*
import kotlin.experimental.or

data class EntityMetadata(
    val type: EntityMetadataType,
    val writer: EntityMetadataByteBufWriter,
    val value: Any?
) {
    override fun toString(): String =
        "EntityMetadata(${type.name}[${type.index}, ${writer.name}[${writer.ordinal}], $value)"
}

fun ByteBuf.writeMetadata(metadata: EntityMetadata) {
    this.writeByte(metadata.type.index)
    this.writeVarInt(metadata.writer.ordinal)
    val valuePresent = metadata.value != null
    val v = metadata.value
    when(metadata.writer) {
        EntityMetadataByteBufWriter.BYTE -> this.writeByte((v as Byte).toInt())
        EntityMetadataByteBufWriter.VAR_INT -> this.writeVarInt(v as Int)
        EntityMetadataByteBufWriter.VAR_LONG -> this.writeVarLong(v as Long)
        EntityMetadataByteBufWriter.FLOAT -> this.writeFloat(v as Float)
        EntityMetadataByteBufWriter.STRING -> this.writeUtf(v as String)
        EntityMetadataByteBufWriter.TEXT_COMPONENT -> this.writeNBT((metadata.value as Component).toNBT())
        EntityMetadataByteBufWriter.OPTIONAL_TEXT_COMPONENT -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeNBT((metadata.value as Component).toNBT()) }
        EntityMetadataByteBufWriter.SLOT -> TODO()
        EntityMetadataByteBufWriter.BOOLEAN -> this.writeBoolean(v as Boolean)
        EntityMetadataByteBufWriter.ROTATION -> this.writeVector3f(v as Vector3f)
        EntityMetadataByteBufWriter.POSITION -> this.writeLocation(v as Location)
        EntityMetadataByteBufWriter.OPTIONAL_POSITION -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeLocation(v as Location)}
        EntityMetadataByteBufWriter.DIRECTION -> this.writeVarInt((v as Direction).ordinal)
        EntityMetadataByteBufWriter.OPTIONAL_UUID -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeUUID(v as UUID)}
        EntityMetadataByteBufWriter.BLOCK_STATE -> this.writeVarInt(v as Int)
        EntityMetadataByteBufWriter.OPTIONAL_BLOCK_STATE -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeVarInt(v as Int)}
        EntityMetadataByteBufWriter.NBT -> this.writeNBT(v as NBTCompound)
        EntityMetadataByteBufWriter.PARTICLE -> TODO()
        EntityMetadataByteBufWriter.VILLAGER_DATA -> this.writeVector3(v as Vector3)
        EntityMetadataByteBufWriter.OPTIONAL_VAR_INT -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeVarInt(v as Int)}
        EntityMetadataByteBufWriter.POSE -> this.writeVarInt((v as EntityPose).ordinal)
        EntityMetadataByteBufWriter.CAT_VARIANT -> this.writeVarInt(v as Int)
        EntityMetadataByteBufWriter.FROG_VARIANT -> this.writeVarInt(v as Int)
        EntityMetadataByteBufWriter.OPTIONAL_GLOBAL_POSITION -> TODO()
        EntityMetadataByteBufWriter.PAINTING_VARIANT -> this.writeVarInt(v as Int)
        EntityMetadataByteBufWriter.SNIFFER_STATE -> this.writeVarInt(v as Int)
        EntityMetadataByteBufWriter.VECTOR3 -> this.writeVector3f(v as Vector3f)
        EntityMetadataByteBufWriter.QUATERNION -> this.writeQuaternion(v as Quaternion)
        else -> throw Exception("noop in entity meta")
    }
}


class EntityStateMetadataBuilder(entity: Entity) {
    var isOnFire = entity.isOnFire.value
    var isCrouching = if(entity is Player) entity.isSneaking else false
    var isSprinting = if(entity is Player) entity.isSprinting else false
    //TODO is swimming
    var isSwimming = false
    var isInvisible = entity.isInvisible.value
    var isGlowing = entity.isGlowing.value
    //TODO elytra
    var isFlying = false
}

fun getEntityMetadataState(entity: Entity, builder: (EntityStateMetadataBuilder.() -> Unit) = {}): EntityMetadata {

    val stateMetadata = EntityStateMetadataBuilder(entity)
    builder.invoke(stateMetadata)

    var bitMask: Byte = 0x00
    if (stateMetadata.isOnFire) bitMask = (bitMask or 0x01)
    if (stateMetadata.isCrouching) bitMask = (bitMask + 0x02).toByte()
    if (stateMetadata.isSprinting) bitMask = (bitMask + 0x08).toByte()
    if (stateMetadata.isSwimming) bitMask = (bitMask + 0x10).toByte()
    if (stateMetadata.isInvisible) bitMask = (bitMask + 0x20).toByte()
    if (stateMetadata.isGlowing) bitMask = (bitMask + 0x40).toByte()
    if (stateMetadata.isFlying) bitMask = (bitMask or 0x80.toByte())

    return EntityMetadata(EntityMetadataType.STATE, EntityMetadataByteBufWriter.BYTE, bitMask)
}

enum class EntityMetadataType(var index: Int) {
    STATE(0),
    AIR_TICKS(1),
    CUSTOM_NAME(2),
    IS_CUSTOM_NAME_VISIBLE(3),
    SILENT(4),
    HAS_NO_GRAVITY(5),
    POSE(6),
    FROZEN_TICKS(7),
    HAND_STATE(8),
    MOB(16),
    WARDEN_ANGER_LEVEL(16),
    DISPLAY_SKIN_PARTS(17),
    MAIN_HAND(18),
    PARROT_VARIANT(19)
}

enum class EntityMetadataByteBufWriter {
    BYTE,
    VAR_INT,
    VAR_LONG,
    FLOAT,
    STRING,
    TEXT_COMPONENT,
    OPTIONAL_TEXT_COMPONENT,
    SLOT,
    BOOLEAN,
    ROTATION,
    POSITION,
    OPTIONAL_POSITION,
    DIRECTION,
    OPTIONAL_UUID,
    BLOCK_STATE,
    OPTIONAL_BLOCK_STATE,
    NBT,
    PARTICLE,
    PARTICLE_LIST,
    VILLAGER_DATA,
    OPTIONAL_VAR_INT,
    POSE,
    CAT_VARIANT,
    FROG_VARIANT,
    OPTIONAL_GLOBAL_POSITION,
    PAINTING_VARIANT,
    SNIFFER_STATE,
    ARMADILLO_STATE,
    VECTOR3,
    QUATERNION
}