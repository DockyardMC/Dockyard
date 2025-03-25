package io.github.dockyardmc.entity

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeLocation
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.maths.Quaternion
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.maths.writeQuaternion
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.*
import kotlin.experimental.and
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
        EntityMetaValue.STRING -> this.writeString(v as String)
        EntityMetaValue.TEXT_COMPONENT -> this.writeNBT((metadata.value as Component).toNBT())
        EntityMetaValue.OPTIONAL_TEXT_COMPONENT -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeNBT((metadata.value as Component).toNBT()) }
        EntityMetaValue.ITEM_STACK -> (v as ItemStack).write(this)
        EntityMetaValue.BOOLEAN -> this.writeBoolean(v as Boolean)
        EntityMetaValue.ROTATION -> (v as Vector3f).write(this)
        EntityMetaValue.POSITION -> this.writeLocation(v as Location)
        EntityMetaValue.OPTIONAL_POSITION -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeLocation(v as Location)}
        EntityMetaValue.DIRECTION -> this.writeVarInt((v as Direction).ordinal)
        EntityMetaValue.OPTIONAL_UUID -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeUUID(v as UUID)}
        EntityMetaValue.BLOCK_STATE -> this.writeVarInt((v as io.github.dockyardmc.world.block.Block).getProtocolId())
        EntityMetaValue.OPTIONAL_BLOCK_STATE -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeVarInt(v as Int)}
        EntityMetaValue.NBT -> this.writeNBT(v as NBTCompound)
        EntityMetaValue.PARTICLE -> TODO()
        EntityMetaValue.VILLAGER_DATA -> (v as Vector3).write(this)
        EntityMetaValue.OPTIONAL_VAR_INT -> { this.writeBoolean(valuePresent); if(valuePresent) this.writeVarInt(v as Int)}
        EntityMetaValue.POSE -> this.writeVarInt((v as EntityPose).ordinal)
        EntityMetaValue.CAT_VARIANT -> this.writeVarInt(v as Int)
        EntityMetaValue.FROG_VARIANT -> this.writeVarInt(v as Int)
        EntityMetaValue.OPTIONAL_GLOBAL_POSITION -> TODO()
        EntityMetaValue.PAINTING_VARIANT -> this.writeVarInt(v as Int)
        EntityMetaValue.SNIFFER_STATE -> this.writeVarInt(v as Int)
        EntityMetaValue.VECTOR3 -> (v as Vector3f).write(this)
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

fun getEntityMetadataStateBuilder(bitMask: Byte, entity: Entity): EntityStateMetadataBuilder {
    val stateMetadata = EntityStateMetadataBuilder(entity)

    stateMetadata.isOnFire = (bitMask and 0x01).toInt() != 0
    stateMetadata.isCrouching = (bitMask and 0x02).toInt() != 0
    stateMetadata.isSprinting = (bitMask and 0x08).toInt() != 0
    stateMetadata.isSwimming = (bitMask and 0x10).toInt() != 0
    stateMetadata.isInvisible = (bitMask and 0x20).toInt() != 0
    stateMetadata.isGlowing = (bitMask and 0x40).toInt() != 0
    stateMetadata.isFlying = (bitMask and 0x80.toByte()).toInt() != 0

    return stateMetadata
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
    INTERACTION_WIDTH(8),
    INTERACTION_HEIGHT(9),
    INTERACTION_RESPONSIVE(10),
    AREA_EFFECT_CLOUD_RADIUS(8),
    AREA_EFFECT_CLOUD_COLOR(9),
    AREA_EFFECT_CLOUD_PARTICLE(10),
    END_CRYSTAL_BEAM_TARGET(8),
    END_CRYSTAL_SHOW_BOTTOM(9),
    EYE_OF_ENDER_ITEM_STACK(8),
    FALLING_BLOCK_POSITION(8),
    FIREBALL_ITEM_STACK(8),
    FIREWORK_ROCKET_ITEM_STACK(8),
    FIREWORK_ROCKET_USED_BY_ENTITY_ID(9),
    FIREWORK_ROCKET_IS_SHOT_AT_ANGLE(10),
    HAND_STATE(8),
    ITEM_DROP_ITEM_STACK(8),
    ITEM_FRAME_ITEM_STACK(8),
    ITEM_FRAME_ROTATION(9),
    OMINOUS_ITEM_SPAWNER_ITEM_STACK(8),
    SMALL_FIREBALL_ITEM_STACK(8),
    PRIMED_TNT_FUSE_TIME(8),
    PRIMED_TNT_BLOCK_STATE(9),
    WITHER_SKULL_IS_INVULNERABLE(8),
    FISHING_BOBBER_HOOKED_ENTITY_ID(8),
    FISHING_BOBBER_IS_CATCHABLE(9),
    ABSTRACT_ARROW_BITMASK(8),
    ABSTRACT_ARROW_PIERCING_LEVEL(9),
    ABSTRACT_ARROW_IS_IN_GROUND(10),
    ARROW_COLOR(11),
    TRIDENT_LOYALTY_LEVEL(11),
    TRIDENT_HAS_ENCHANTMENT_GLINT(12),
    BOAT_TYPE(8),
    BOAT_IS_LEFT_PADDLE_TURNING(9),
    BOAT_IS_RIGHT_PADDLE_TURNING(9),
    LIVING_ENTITY_HAND_STATE(8),
    LIVING_ENTITY_HEALTH(9),
    LIVING_ENTITY_PARTICLES(10),
    LIVING_ENTITY_IS_POTION_EFFECT_AMBIENT(11),
    LIVING_ENTITY_NUMBER_OF_ARROWS(12),
    LIVING_ENTITY_NUMBER_OF_BEE_STRINGERS(13),
    LIVING_ENTITY_LOCATION_OF_BED(14),
    ARMOR_STAND_BITMASK(15),
    ARMOR_STAND_HEAD_ROTATION(16),
    ARMOR_STAND_BODY_ROTATION(17),
    ARMOR_STAND_LEFT_ARM_ROTATION(18),
    ARMOR_STAND_RIGHT_ARM_ROTATION(19),
    ARMOR_STAND_LEFT_LEG_ROTATION(20),
    ARMOR_STAND_RIGHT_LEG_ROTATION(21),
    MOB(16),
    BAT_IS_HANGING(16),
    ENDER_DRAGON_PHASE(16),
    GHAST_IS_ATTACKING(16),
    PHANTOM_SIZE(16),
    SLIME_SIZE(16),
    ALLAY_IS_DANCING(16),
    ALLAY_CAN_DUPLICATE(17),
    IRON_GOLEM_IS_PLAYER_CREATED(16),
    PUFFERFISH_PUFF_STATE(16),
    SHULKER_ATTACH_FACE(16),
    SHULKER_SHIELD_HEIGHT(17),
    SHULKER_COLOR(18),
    SNOW_GOLEM_BITMASK(16),
    TADPOLE_IS_FROM_BUCKET(16),
    AGEABLE_MOB_IS_BABY(16),
    DOLPHIN_TREASURE_POSITION(17),
    DOLPHIN_HAS_FISH(18),
    DOLPHIN_MOISTURE_LEVEL(19),
    GLOW_SQUID_DARK_TICKS_REMAINING(17),
    ARMADILLO_STATE(17),
    AXOLOTL_VARIANT(17),
    AXOLOTL_IS_PLAYING_DEAD(18),
    AXOLOTL_SPAWNED_FROM_BUCKET(19),
    BEE_BIT_MASK(17),
    BEE_ANGER_TICKS(18),
    MOOSHROOM_VARIANT(17),
    FOX_TYPE(17),
    FOX_BITMASK(18),
    FOX_FIRST_UUID(19),
    FOX_SECOND_UUID(20),
    FROG_VARIANT(17),
    FROG_TONGUE_TARGET(18),
    GOAT_IS_SCREAMING_GOAT(17),
    GOAT_HAS_LEFT_HORN(18),
    GOAT_HAS_RIGHT_HORN(19),
    HOGLIN_IS_IMMUNE_TO_ZOMBIFICATION(17),
    OCELOT_IS_TRUSTING(17),
    PANDA_BREED_TIMER(17),
    PANDA_SNEEZE_TIMER(18),
    PANDA_EAT_TIMER(19),
    PANDA_MAIN_GENE(20),
    PANDA_HIDDEN_GENE(21),
    PANDA_BITMASK(22),
    PIG_HAS_SADDLE(17),
    PIG_BOOST_TIME(18),
    POLAR_BEAR_IS_STANDING_UP(17),
    RABBIT_TYPE(17),
    SHEEP_BITMASK(17),
    SNIFFER_STATE(17),
    SNIFFER_DROP_SEED_AT_TICK(18),
    STRIDER_BOOST_TIME(17),
    STRIDER_IS_SHAKING(18),
    STRIDER_HAS_SADDLE(19),
    TURTLE_HOME_POSITION(17),
    TURTLE_HAS_EGG(18),
    TURTLE_IS_LAYING_EGG(19),
    TURTLE_TRAVEL_POSITION(20),
    TURTLE_IS_GOING_HOME(22),
    TURTLE_IS_TRAVELLING(22),
    ABSTRACT_HORSE_BITMASK(17),
    CAMEL_IS_DASHING(18),
    CAMEL_LAST_POS_CHANGE_TICK(19),
    HORSE_VARIANT(18),
    CHESTED_HORSE_HAS_CHEST(18),
    LLAMA_STRENGTH(19),
    LLAMA_CARPET_COLOR(20),
    TAMABLE_ANIMAL_STATE(17),
    TAMABLE_ANIMAL_OWNER(18),
    CAT_VARIANT(19),
    CAT_IS_LYING(20),
    CAT_IS_RELAXED(21),
    CAT_COLLAR_COLOR(22),
    WOLF_IS_BEGGING(19),
    WOLF_COLLAR_COLOR(20),
    WOLF_ANGER_TIME(21),
    WOLF_VARIANT(22),
    CREAKING_CAN_MOVE(16),
    CREAKING_IS_ACTIVE(17),
    CREAKING_IS_TEARING_DOWN(18),
    CREAKING_HOME_POSITION(19),
    CREEPER_STATE(16),
    CREEPER_IS_CHARGED(17),
    CREEPER_IS_IGNITED(18),
    ENDERMAN_CARRIED_BLOCK(16),
    ENDERMAN_IS_SCREAMING(17),
    ENDERMAN_IS_STARING(18),
    PIGLIN_IS_BABY(16),
    PIGLIN_IS_CHARGING_CROSSBOW(17),
    PIGLIN_IS_DANCING(18),
    PIGLIN_BRUTE_IMMUNE_TO_ZOMBIFICATION(16),
    SKELETON_IS_BEING_CONVERTED_INTO_STRAY(16),
    SPIDER_IS_CLIMBING(16),
    VEX_ANGER_LEVEL(16),
    WITHER_CENTER_HEAD_TARGET(16),
    WITHER_LEFT_HEAD_TARGET(17),
    WITHER_RIGHT_HEAD_TARGET(18),
    WITHER_INVULNERABLE_TIME(19),
    ZOMBIE_IS_BABY(16),
    ZOMBIE_IS_BECOMING_DROWNED(18),
    ZOMBIE_VILLAGER_IS_CONVERTING(19),
    ZOMBIE_VILLAGER_DATA(20),
    RAIDER_IS_CELEBRATING(16),
    PILLAGER_IS_CHARGING(17),
    WITCH_IS_DRINKING_POTION(17),
    SPELLCASTER_ILLAGER_TYPE(17),
    ABSTRACT_MINECART_CUSTOM_BLOCK_ID(8),
    ABSTRACT_MINECART_CUSTOM_BLOCK_Y(9),
    ABSTRACT_MINECART_SHOW_CUSTOM_BLOCK(10),
    MINECART_WITH_COMMAND_BLOCK_COMMAND(11),
    MINECART_WITH_COMMAND_BLOCK_LAST_OUTPUT(12),
    MINECART_WITH_FURNACE_HAS_FUEL(11),
    THROW_ITEM_PROJECTILE_ITEM_STACK(8),
    WARDEN_ANGER_LEVEL(16),
    PLAYER_ADDITION_HEARTS(15),
    PLAYER_SCORE(16),
    PLAYER_LEFT_SHOULDER_ENTITY_DATA(19),
    PLAYER_RIGHT_SHOULDER_ENTITY_DATA(19),
    PLAYER_DISPLAY_SKIN_PARTS(17),
    PLAYER_MAIN_HAND(18),
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
    TEXT_DISPLAY_FORMATTING(27),
    ITEM_DISPLAY_ITEM(23),
    ITEM_DISPLAY_RENDER_TYPE(24),
    BLOCK_DISPLAY_BLOCK(23),
    GUARDIAN_RETRACTING_SPIKES(16),
    GUARDIAN_TARGET_ENTITY_ID(17),
}


enum class EntityMetaValue {
    BYTE,
    VAR_INT,
    VAR_LONG,
    FLOAT,
    STRING,
    TEXT_COMPONENT,
    OPTIONAL_TEXT_COMPONENT,
    ITEM_STACK,
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
    WOLF_VARIANT,
    FROG_VARIANT,
    OPTIONAL_GLOBAL_POSITION,
    PAINTING_VARIANT,
    SNIFFER_STATE,
    ARMADILLO_STATE,
    VECTOR3,
    QUATERNION,
}