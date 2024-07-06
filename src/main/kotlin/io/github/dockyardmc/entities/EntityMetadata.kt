package io.github.dockyardmc.entities

import io.github.dockyardmc.bindables.BindableList
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.utils.*
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.*

class EntityMetadata(
    val index: EntityMetaIndex,
    val type: EntityMetadataType,
    val value: Any?
)

fun ByteBuf.writeMetadata(metadata: EntityMetadata) {
    this.writeVarInt(metadata.type.ordinal)
    val valuePresent = metadata.value != null
    val v = metadata.value
    when(metadata.type) {
        EntityMetadataType.BYTE -> this.writeByte(v as Int)
        EntityMetadataType.VAR_INT -> this.writeVarInt(v as Int)
        EntityMetadataType.VAR_LONG -> this.writeVarLong(v as Long)
        EntityMetadataType.FLOAT -> this.writeFloat(v as Float)
        EntityMetadataType.STRING -> this.writeUtf(v as String)
        EntityMetadataType.TEXT_COMPONENT -> this.writeNBT((metadata.value as Component).toNBT())
        EntityMetadataType.OPTIONAL_TEXT_COMPONENT -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeNBT((metadata.value as Component).toNBT()) }
        EntityMetadataType.SLOT -> TODO()
        EntityMetadataType.BOOLEAN -> this.writeBoolean(v as Boolean)
        EntityMetadataType.ROTATION -> this.writeVector3f(v as Vector3f)
        EntityMetadataType.POSITION -> this.writeLocation(v as Location)
        EntityMetadataType.OPTIONAL_POSITION -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeLocation(v as Location)}
        EntityMetadataType.DIRECTION -> this.writeVarInt((v as Direction).ordinal)
        EntityMetadataType.OPTIONAL_UUID -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeUUID(v as UUID)}
        EntityMetadataType.BLOCK_STATE -> this.writeVarInt(v as Int)
        EntityMetadataType.OPTIONAL_BLOCK_STATE -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeVarInt(v as Int)}
        EntityMetadataType.NBT -> this.writeNBT(v as NBTCompound)
        EntityMetadataType.PARTICLE -> TODO()
        EntityMetadataType.VILLAGER_DATA -> this.writeVector3(v as Vector3)
        EntityMetadataType.OPTIONAL_VAR_INT -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeVarInt(v as Int)}
        EntityMetadataType.POSE -> this.writeVarInt((v as EntityPose).ordinal)
        EntityMetadataType.CAT_VARIANT -> this.writeVarInt(v as Int)
        EntityMetadataType.FROG_VARIANT -> this.writeVarInt(v as Int)
        EntityMetadataType.OPTIONAL_GLOBAL_POSITION -> TODO()
        EntityMetadataType.PAINTING_VARIANT -> this.writeVarInt(v as Int)
        EntityMetadataType.SNIFFER_STATE -> this.writeVarInt(v as Int)
        EntityMetadataType.VECTOR3 -> this.writeVector3f(v as Vector3f)
        EntityMetadataType.QUATERNION -> this.writeQuaternion(v as Quaternion)
    }
}

enum class EntityMetaIndex(var index: Int) {
    STATE(0),
    AIR_TICKS(1),
    CUSTOM_NAME(2),
    IS_CUSTOM_NAME_VISIBLE(3),
    SILENT(4),
    HAS_NO_GRAVITY(5),
    POSE(6),
    FROZEN_TICKS(7),
    DISPLAY_SKIN_PARTS(17)
}

enum class EntityMetadataType {
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
    VILLAGER_DATA,
    OPTIONAL_VAR_INT,
    POSE,
    CAT_VARIANT,
    FROG_VARIANT,
    OPTIONAL_GLOBAL_POSITION,
    PAINTING_VARIANT,
    SNIFFER_STATE,
    VECTOR3,
    QUATERNION
}

fun BindableList<EntityMetadata>.addOrUpdate(metadata: EntityMetadata) {
    val hasMeta = (this.values.firstOrNull { it.type == metadata.type } != null)
    if(hasMeta) {
        val index = this.values.indexOfFirst { it.type == metadata.type }
        this.setIndex(index, metadata)
    } else {
        this.add(metadata)
    }
}