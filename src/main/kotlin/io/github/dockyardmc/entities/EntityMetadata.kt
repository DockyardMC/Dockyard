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
    val writer: EntityMetaValue,
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
        EntityMetaValue.BYTE -> when(v) {
            is Int -> this.writeByte(v)
            is Byte -> this.writeByte(v.toInt())
        }
        EntityMetaValue.VAR_INT -> this.writeVarInt(v as Int)
        EntityMetaValue.VAR_LONG -> this.writeVarLong(v as Long)
        EntityMetaValue.FLOAT -> this.writeFloat(v as Float)
        EntityMetaValue.STRING -> this.writeUtf(v as String)
        EntityMetaValue.TEXT_COMPONENT -> this.writeNBT((metadata.value as Component).toNBT())
        EntityMetaValue.OPTIONAL_TEXT_COMPONENT -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeNBT((metadata.value as Component).toNBT()) }
        EntityMetaValue.SLOT -> TODO()
        EntityMetaValue.BOOLEAN -> this.writeBoolean(v as Boolean)
        EntityMetaValue.ROTATION -> this.writeVector3f(v as Vector3f)
        EntityMetaValue.POSITION -> this.writeLocation(v as Location)
        EntityMetaValue.OPTIONAL_POSITION -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeLocation(v as Location)}
        EntityMetaValue.DIRECTION -> this.writeVarInt((v as Direction).ordinal)
        EntityMetaValue.OPTIONAL_UUID -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeUUID(v as UUID)}
        EntityMetaValue.BLOCK_STATE -> this.writeVarInt(v as Int)
        EntityMetaValue.OPTIONAL_BLOCK_STATE -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeVarInt(v as Int)}
        EntityMetaValue.NBT -> this.writeNBT(v as NBTCompound)
        EntityMetaValue.PARTICLE -> TODO()
        EntityMetaValue.VILLAGER_DATA -> this.writeVector3(v as Vector3)
        EntityMetaValue.OPTIONAL_VAR_INT -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeVarInt(v as Int)}
        EntityMetaValue.POSE -> this.writeVarInt((v as EntityPose).ordinal)
        EntityMetaValue.CAT_VARIANT -> this.writeVarInt(v as Int)
        EntityMetaValue.FROG_VARIANT -> this.writeVarInt(v as Int)
        EntityMetaValue.OPTIONAL_GLOBAL_POSITION -> TODO()
        EntityMetaValue.PAINTING_VARIANT -> this.writeVarInt(v as Int)
        EntityMetaValue.SNIFFER_STATE -> this.writeVarInt(v as Int)
        EntityMetaValue.VECTOR3 -> this.writeVector3f(v as Vector3f)
        EntityMetaValue.QUATERNION -> this.writeQuaternion(v as Quaternion)
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

    return EntityMetadata(EntityMetadataType.STATE, EntityMetaValue.BYTE, bitMask)
}

class TextDisplayFormattingBuilder(textDisplay: TextDisplay) {
    var hasShadow = textDisplay.hasShadow.value
    var isSeeThrough = textDisplay.isSeeThrough.value
    var useDefaultBackgroundColor = textDisplay.useDefaultBackgroundColor.value
    var alignment = textDisplay.alignment.value
}

fun getTextDisplayFormatting(entity: TextDisplay, builder: (TextDisplayFormattingBuilder.() -> Unit) = {}): EntityMetadata {

    val formatting = TextDisplayFormattingBuilder(entity)
    builder.invoke(formatting)

    var bitMask: Byte = 0x00
    if (formatting.hasShadow) bitMask = (bitMask or 0x01)
    if (formatting.isSeeThrough) bitMask = (bitMask or 0x02)
    if (formatting.useDefaultBackgroundColor) bitMask = (bitMask or 0x04)

    bitMask = (bitMask or formatting.alignment.mask).toByte()

    return EntityMetadata(EntityMetadataType.TEXT_DISPLAY_FORMATTING, EntityMetaValue.BYTE, bitMask)
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
    PARROT_VARIANT(19),
    DISPLAY_INTERPOLATION_DELAY(8),
    DISPLAY_TRANSFORM_INTERPOLATION(9),
    DISPLAY_TRANSLATION_INTERPOLATION(10),
    DISPLAY_TRANSLATION(11),
    DISPLAY_SCALE(12),
    DISPLAY_ROTATION_LEFT(13),
    DISPLAY_ROTATION_RIGHT(14),
    DISPLAY_BILLBOARD(15),
    DISPLAY_BRIGHTNESS(16),
    DISPLAY_VIEW_RANGE(17),
    DISPLAY_SHADOW_RADIUS(18),
    DISPLAY_SHADOW_STRENGTH(19),
    DISPLAY_WIDTH(20),
    DISPLAY_HEIGHT(21),
    DISPLAY_GLOW_COLOR(22),
    TEXT_DISPLAY_TEXT(23),
    TEXT_DISPLAY_LINE_WIDTH(24),
    TEXT_DISPLAY_BACKGROUND_COLOR(25),
    TEXT_DISPLAY_TEXT_OPACITY(26),
    TEXT_DISPLAY_FORMATTING(27)
}


enum class EntityMetaValue {
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
    QUATERNION,
    VECTOR3,
}