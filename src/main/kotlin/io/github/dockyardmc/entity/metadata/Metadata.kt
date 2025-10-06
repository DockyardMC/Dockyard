package io.github.dockyardmc.entity.metadata

import io.github.dockyardmc.entity.metadata.Metadata.MetadataDefinition
import io.github.dockyardmc.entity.metadata.MetadataType.MetadataSerializer
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.Quaternion
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.registry.PaintingVariants
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.world.block.Block
import java.util.concurrent.atomic.AtomicInteger

object Metadata : MetadataGroup() {

    val ENTITY_FLAGS = define(MetadataType.BYTE, 0)
    val IS_ON_FIRE = bitmask<Boolean>(ENTITY_FLAGS, 0x01, false)
    val IS_CROUCHING = bitmask<Boolean>(ENTITY_FLAGS, 0x02, false)
    val IS_SPRINTING = bitmask<Boolean>(ENTITY_FLAGS, 0x03, false)
    val IS_SWIMMING = bitmask<Boolean>(ENTITY_FLAGS, 0x04, false)
    val IS_INVISIBLE = bitmask<Boolean>(ENTITY_FLAGS, 0x05, false)
    val HAS_GLOWING_EFFECT = bitmask<Boolean>(ENTITY_FLAGS, 0x06, false)
    val AIR_TICKS = define(MetadataType.VAR_INT, 300)
    val CUSTOM_NAME = define(MetadataType.OPTIONAL_COMPONENT, null)
    val CUSTOM_NAME_VISIBLE = define(MetadataType.BOOLEAN, false)
    val IS_SILENT = define(MetadataType.BOOLEAN, false)
    val HAS_NO_GRAVITY = define(MetadataType.BOOLEAN, false)
    val POSE = define(MetadataType.POSE, EntityPose.STANDING)
    val TICKS_FROZEN = define(MetadataType.VAR_INT, 0)

    object Interaction : MetadataGroup(Metadata) {
        val WIDTH = define(MetadataType.FLOAT, 1f)
        val HEIGHT = define(MetadataType.FLOAT, 1f)
        val RESPONSIVE = define(MetadataType.BOOLEAN, false)
    }

    object Display : MetadataGroup(Metadata) {
        val INTERPOLATION_DELAY = define(MetadataType.VAR_INT, 0)
        val TRANSFORMATION_INTERPOLATION_DURATION = define(MetadataType.VAR_INT, 0)
        val POSITION_ROTATION_INTERPOLATION_DURATION = define(MetadataType.VAR_INT, 0)
        val TRANSLATION = define(MetadataType.VECTOR_3F, Vector3f.ZERO)
        val SCALE = define(MetadataType.VECTOR_3F, Vector3f.ZERO)
        val ROTATION_LEFT = define(MetadataType.QUATERNION, Quaternion.DEFAULT)
        val ROTATION_RIGHT = define(MetadataType.QUATERNION, Quaternion.DEFAULT)
        val BILLBOARD_CONSTRAINTS = define(MetadataType.BYTE, 0)
        val BRIGHTNESS_OVERRIDE = define(MetadataType.VAR_INT, -1)
        val VIEW_RANGE = define(MetadataType.FLOAT, 1f)
        val SHADOW_RADIUS = define(MetadataType.FLOAT, 0f)
        val SHADOW_STRENGHT = define(MetadataType.FLOAT, 1f)
        val WIDTH = define(MetadataType.FLOAT, 0f)
        val HEIGHT = define(MetadataType.FLOAT, 0f)
        val GLOW_COLOR_OVERRIDE = define(MetadataType.VAR_INT, -1)
    }

    object BlockDisplay : MetadataGroup(Display) {
        val DISPLAYED_BLOCK_STATE = define(MetadataType.BLOCK_STATE, Block.AIR)
    }

    object ItemDisplay : MetadataGroup(Display) {
        val DISPLAYED_ITEM = define(MetadataType.ITEM_STACK, ItemStack.AIR)
    }

    object TextDisplay : MetadataGroup(Display) {
        val TEXT = define(MetadataType.COMPONENT, Component())
        val LINE_WIDTH = define(MetadataType.VAR_INT, 200)
        val BACKGROUND_COLOR = define(MetadataType.VAR_INT, 0x40000000)
        val TEXT_OPACITY = define(MetadataType.BYTE, -1)
        val TEXT_DISPLAY_FLAGS = define(MetadataType.BYTE, 0)
        //TODO bitmask
    }

    object ExperienceOrb : MetadataGroup(Metadata) {
        val VALUE = define(MetadataType.VAR_INT, 0)
    }

    object ThrowItemProjectile : MetadataGroup(Metadata) {
        val ITEM = define(MetadataType.ITEM_STACK, ItemStack.AIR)
    }

    object EyeOfEnder : MetadataGroup(Metadata) {
        val ITEM = define(MetadataType.ITEM_STACK, ItemStack.AIR)
    }

    object FallingBlock : MetadataGroup(Metadata) {
        val SPAWN_POSITION = define(MetadataType.BLOCK_POSITION, Vector3.ZERO)
    }

    object AreaEffectCloud : MetadataGroup(Metadata) {
        val RADIUS = define(MetadataType.FLOAT, 0.5f)
        val COLOR = define(MetadataType.VAR_INT, 0)
        val IGNORE_RADIUS_AND_SINGLE_POINT = define(MetadataType.BOOLEAN, false)
        val PARTICLE = define(MetadataType.PARTICLE, Particles.EFFECT)
    }

    object FishingHook : MetadataGroup(Metadata) {
        val HOOKED = define(MetadataType.VAR_INT, 0)
        val IS_CATCHABLE = define(MetadataType.BOOLEAN, false)
    }

    object AbstractArrow : MetadataGroup(Metadata) {
        val ARROW_FLAGS = define(MetadataType.BYTE, 0)
        //TODO bitmask
    }

    object Arrow : MetadataGroup(AbstractArrow) {
        val COLOR = define(MetadataType.VAR_INT, -1)
    }

    object ThrownTrident : MetadataGroup(AbstractArrow) {
        val LOYALTY_LEVEL = define(MetadataType.BYTE, 0)
        val HAS_ENCHANTMENT_GLINT = define(MetadataType.BOOLEAN, false)
    }

    object AbstractVehicle : MetadataGroup(Metadata) {
        val SHAKING_POWER = define(MetadataType.VAR_INT, 0)
        val SHAKING_DIRECTION = define(MetadataType.VAR_INT, 1)
        val SHAKING_MULTIPLIER = define(MetadataType.FLOAT, 0f)
    }

    object Boat : MetadataGroup(AbstractVehicle) {
        val IS_LEFT_PADDLE_TURNING = define(MetadataType.BOOLEAN, false)
        val IS_RIGHT_PADDLE_TURNING = define(MetadataType.BOOLEAN, false)
        val SPLASH_TIMER = define(MetadataType.VAR_INT, 0)
    }

    object AbstractMinecart : MetadataGroup(AbstractVehicle) {
        val CUSTOM_BLOCK_STATE = define(MetadataType.OPTIONAL_BLOCK_STATE, null)
        val CUSTOM_BLOCK_Y_POSITION = define(MetadataType.VAR_INT, 6)
    }

    object MinecartFurnace : MetadataGroup(AbstractMinecart) {
        val HAS_FUEL = define(MetadataType.BOOLEAN, false)
    }

    // minceart commadn block?? they dont even work here bro

    object EndCrystal : MetadataGroup(Metadata) {
        val BEAM_TARGET = define(MetadataType.OPTIONAL_BLOCK_POSITION, null)
        val SHOW_BOTTOM = define(MetadataType.BOOLEAN, true)
    }

    object SmartFireball : MetadataGroup(Metadata) {
        val ITEM = define(MetadataType.ITEM_STACK, ItemStack.AIR)
    }

    object Fireball : MetadataGroup(Metadata) {
        val ITEM = define(MetadataType.ITEM_STACK, ItemStack.AIR)
    }

    object WitherSkull : MetadataGroup(Metadata) {
        val IS_INVULNERABLE = define(MetadataType.BOOLEAN, false)
    }

    object FireworkRocketEntity : MetadataGroup(Metadata) {
        val ITEM = define(MetadataType.ITEM_STACK, ItemStack.AIR)
        val SHOOTER_ENTITY_ID = define(MetadataType.OPTIONAL_VAR_INT, null)
        val IS_SHOT_AT_ANGLE = define(MetadataType.BOOLEAN, false)
    }

    object Hanging : MetadataGroup(Metadata) {
        val DIRECTION = define(MetadataType.DIRECTION, Direction.SOUTH)
    }

    object ItemFrame : MetadataGroup(Hanging) {
        val ITEM = define(MetadataType.ITEM_STACK, ItemStack.AIR)
        val ROTATION = define(MetadataType.VAR_INT, 0)
    }

    object Painting : MetadataGroup(Hanging) {
        val VARIANT = define(MetadataType.PAINTING_VARIANT, PaintingVariants.KEBAB)
    }

    object ItemEntity : MetadataGroup(Metadata) {
        val ITEM = define(MetadataType.ITEM_STACK, ItemStack.AIR)
    }

    //next: LivingEntity

    interface MetadataDefinitionEntry<T>

    data class MetadataDefinition<T>(val index: Int, val type: MetadataSerializer<T>, val default: T) : MetadataDefinitionEntry<T>

    data class BitmaskFlagDefinition<T>(
        val parent: MetadataDefinition<Byte>,
        val bitMask: Byte,
        val defaultValue: T
    ) : MetadataDefinitionEntry<T> {
//        fun isSet(value: Byte): Boolean = (value and bitMask) != 0.toByte()
//        fun set(value: Byte): Byte = (value.toInt() or bitMask.toInt()).toByte()
//        fun unset(value: Byte): Byte = (value.toInt() and bitMask.toInt().inv()).toByte()
    }

}

abstract class MetadataGroup(initialValue: Int = 0) {
    constructor(parent: MetadataGroup) : this(parent.counter.get())

    protected val counter = AtomicInteger(initialValue)

    protected fun <T> define(type: MetadataSerializer<T>, default: T): MetadataDefinition<T> {
        return MetadataDefinition(counter.getAndIncrement(), type, default)
    }

    protected fun <T> bitmask(parent: MetadataDefinition<Byte>, bitMask: Byte, defaultValue: T): Metadata.BitmaskFlagDefinition<T> {
        return Metadata.BitmaskFlagDefinition<T>(parent, bitMask, defaultValue)
    }
}