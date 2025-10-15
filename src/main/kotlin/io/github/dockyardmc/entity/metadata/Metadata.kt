package io.github.dockyardmc.entity.metadata

import io.github.dockyardmc.entity.metadata.Metadata.MetadataDefinition
import io.github.dockyardmc.entity.metadata.MetadataType.MetadataSerializer
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.Quaternion
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.protocol.types.ResolvableProfile
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.world.block.Block
import java.util.concurrent.atomic.AtomicInteger

object Metadata : MetadataGroup() {

    private val DEFAULT_MANEQUINN_DESCRIPTION = Component(translate = "entity.minecraft.mannequin.label")
    private val DEFAULT_PRIMED_TNT_BLOCK_STATE = Blocks.TNT.toBlock()

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
        val HAS_SHADOW = bitmask<Boolean>(TEXT_DISPLAY_FLAGS, 0x01, false)
        val IS_SEE_THROUGH = bitmask<Boolean>(TEXT_DISPLAY_FLAGS, 0x02, false)
        val USE_DEFAULT_BACKGROUND = bitmask<Boolean>(TEXT_DISPLAY_FLAGS, 0x04, false)
        val ALIGN_LEFT = bitmask<Boolean>(TEXT_DISPLAY_FLAGS, 0x08, false)
        val ALIGN_RIGHT = bitmask<Boolean>(TEXT_DISPLAY_FLAGS, 0x10, false)
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
        val IS_CRITICAL = bitmask(ARROW_FLAGS, 0x01, false)
        val IS_NO_CLIP = bitmask(ARROW_FLAGS, 0x02, false)
        val PIERCING_LEVEL = define(MetadataType.BYTE, 0)
        val IN_GROUND = define(MetadataType.BOOLEAN, false)
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

    object LivingEntity : MetadataGroup(Metadata) {
        val LIVING_ENTITY_FLAGS = define(MetadataType.BYTE, 0)
        val IS_HAND_ACTIVE = bitmask(LIVING_ENTITY_FLAGS, 0x01, false)
        val ACTIVE_HAND = bitmask(LIVING_ENTITY_FLAGS, 0x02, false)
        val IS_RIPTIDE_SPIN_ATTACK = bitmask(LIVING_ENTITY_FLAGS, 0x04, false)
        val HEALTH = define(MetadataType.FLOAT, 1f)
        val POTION_EFFECT_PARTICLES = define(MetadataType.PARTICLE_LIST, listOf())
        val IS_POTION_EFFECT_AMBIENT = define(MetadataType.BOOLEAN, false)
        val NUMBER_OF_ARROWS = define(MetadataType.VAR_INT, 0)
        val NUMBER_OF_BEE_STINGERS = define(MetadataType.VAR_INT, 0)
        val BED_LOCATION = define(MetadataType.OPTIONAL_BLOCK_POSITION, null)
    }

    object Avatar : MetadataGroup(LivingEntity) {
        val MAIN_HAND = define(MetadataType.BYTE, 1)
        val DISPLAYED_MODEL_PARTS_FLAG = define(MetadataType.BYTE, 1)
        val IS_CAPE_ENABLED = bitmask(DISPLAYED_MODEL_PARTS_FLAG, 0x01, false)
        val IS_JACKET_ENABLED = bitmask(DISPLAYED_MODEL_PARTS_FLAG, 0x02, false)
        val IS_LEFT_SLEEVE_ENABLED = bitmask(DISPLAYED_MODEL_PARTS_FLAG, 0x04, false)
        val IS_RIGHT_SLEEVE_ENABLED = bitmask(DISPLAYED_MODEL_PARTS_FLAG, 0x08, false)
        val IS_LEFT_PANTS_LEG_ENABLED = bitmask(DISPLAYED_MODEL_PARTS_FLAG, 0x10, false)
        val IS_RIGHT_PANTS_LEG_ENABLED = bitmask(DISPLAYED_MODEL_PARTS_FLAG, 0x20, false)
        val IS_HAT_ENABLED = bitmask(MAIN_HAND, 0x40, false)
    }

    object Player : MetadataGroup(Avatar) {
        val ADDITIONAL_HEARTS = define(MetadataType.FLOAT, 0f)
        val SCORE = define(MetadataType.VAR_INT, 0)
        val LEFT_SHOULDER_ENTITY_DATA = define(MetadataType.OPTIONAL_VAR_INT, null)
        val RIGHT_SHOULDER_ENTITY_DATA = define(MetadataType.OPTIONAL_VAR_INT, null)
    }

    object Mannequin : MetadataGroup(Avatar) {
        val PROFILE = define(MetadataType.RESOLVABLE_PROFILE, ResolvableProfile.EMPTY)
        val IMMOVABLE = define(MetadataType.BOOLEAN, false)
        val DESCRIPTION = define(MetadataType.OPTIONAL_COMPONENT, DEFAULT_MANEQUINN_DESCRIPTION)
    }

    object ArmorStand : MetadataGroup(LivingEntity) {
        val ARMOR_STAND_FLAGS = define(MetadataType.BYTE, 0)
        val IS_SMALL = bitmask(ARMOR_STAND_FLAGS, 0x01, false)
        val HAS_ARMS = bitmask(ARMOR_STAND_FLAGS, 0x04, false)
        val HAS_NO_BASE_PLATE = bitmask(ARMOR_STAND_FLAGS, 0x08, false)
        val IS_MARKER = bitmask(ARMOR_STAND_FLAGS, 0x10, false)
        val HEAD_ROTATION = define(MetadataType.ROTATION, Vector3f.ZERO)
        val BODY_ROTATION = define(MetadataType.ROTATION, Vector3f.ZERO)
        val LEFT_ARM_ROTATION = define(MetadataType.ROTATION, Vector3f(-10, 0, -10))
        val RIGHT_ARM_ROTATION = define(MetadataType.ROTATION, Vector3f(-15, 0, 10))
        val LEFT_LEG_ROTATION = define(MetadataType.ROTATION, Vector3f(-1, 0, -1))
        val RIGHT_LEG_ROTATION = define(MetadataType.ROTATION, Vector3f(1, 0, 1))
    }

    object Mob : MetadataGroup(LivingEntity) {
        val MOB_FLAGS = define(MetadataType.BYTE, 0)
        val NO_AI = bitmask(MOB_FLAGS, 0x01, false)
        val IS_LEFT_HANDED = bitmask(MOB_FLAGS, 0x02, false)
        val IS_AGGRESSIVE = bitmask(MOB_FLAGS, 0x04, false)
    }

    object Allay : MetadataGroup(Mob) {
        val IS_DANCING = define(MetadataType.BOOLEAN, false)
        val CAN_DUPLICATE = define(MetadataType.BOOLEAN, true)
    }

    object Armadillo : MetadataGroup(Mob) {
        val STATE = define(MetadataType.ARMADILLO_STATE, io.github.dockyardmc.entity.entities.Armadillo.State.IDLE)
    }

    object Bat : MetadataGroup(Mob) {
        val BAT_FLAGS = define(MetadataType.BYTE, 0)
        val IS_HANGING = bitmask(BAT_FLAGS, 0x01, false)
    }

    object Dolphin : MetadataGroup(Mob) {
        val TREASURE_LOCATION = define(MetadataType.BLOCK_POSITION, Vector3.ZERO)
        val HAS_FISH = define(MetadataType.BOOLEAN, false)
        val MOISTURE_LEVEL = define(MetadataType.VAR_INT, 2400)
    }

    object AbstractFish : MetadataGroup(Mob) {
        val FROM_BUCKET = define(MetadataType.BOOLEAN, false)
    }

    object PufferFish : MetadataGroup(AbstractFish) {
        val PUFF_STATE = define(MetadataType.VAR_INT, 0)
    }

    object Salmon : MetadataGroup(AbstractFish) {
        val SIZE = define(MetadataType.VAR_INT, io.github.dockyardmc.entity.entities.Salmon.Size.SMALL.ordinal)
    }

    object TropicalFish : MetadataGroup(AbstractFish) {
        val VARIANT = define(MetadataType.VAR_INT, 0)
    }

    object AgeableMob : MetadataGroup(Mob) {
        val IS_BABY = define(MetadataType.BOOLEAN, false)
    }

    object Sniffer : MetadataGroup(AgeableMob) {
        val STATE = define(MetadataType.SNIFFER_STATE, io.github.dockyardmc.entity.entities.Sniffer.State.IDLING)
        val DROP_SEED_AT_TICK = define(MetadataType.VAR_INT, 0)
    }

    object AbstractHorse : MetadataGroup(AgeableMob) {
        val ABSTRACT_HORSE_FLAGS = define(MetadataType.BYTE, 0)
        val IS_TAME = bitmask(ABSTRACT_HORSE_FLAGS, 0x02, false)

        // 0x04 saddle, no longer used
        val HAS_BRED = bitmask(ABSTRACT_HORSE_FLAGS, 0x08, false)
        val IS_EATING = bitmask(ABSTRACT_HORSE_FLAGS, 0x10, false)
        val IS_REARING = bitmask(ABSTRACT_HORSE_FLAGS, 0x20, false)
        val IS_MOUTH_OPEN = bitmask(ABSTRACT_HORSE_FLAGS, 0x40, false)
    }

    object Horse : MetadataGroup(AbstractHorse) {
        val VARIANT = define(MetadataType.VAR_INT, 0)
    }

    object Camel : MetadataGroup(AbstractHorse) {
        val DASHING = define(MetadataType.BOOLEAN, false)
        val LAST_POSE_CHANGE_TICK = define(MetadataType.VAR_LONG, 0)
    }

    object ChestedHorse : MetadataGroup(AbstractHorse) {
        val HAS_CHEST = define(MetadataType.BOOLEAN, false)
    }

    object Llama : MetadataGroup(ChestedHorse) {
        val STRENGHT = define(MetadataType.VAR_INT, 0)
        val CARPET_COLOR = define(MetadataType.VAR_INT, -1)
        val VARIANT = define(MetadataType.VAR_INT, 0)
    }

    object Axolotl : MetadataGroup(AgeableMob) {
        val VARIANT = define(MetadataType.VAR_INT, 0)
        val IS_PLAYING_DEAD = define(MetadataType.BOOLEAN, false)
        val IS_FROM_BUCKET = define(MetadataType.BOOLEAN, false)
    }

    object Bee : MetadataGroup(AgeableMob) {
        val BEE_FLAGS = define(MetadataType.BYTE, 0)
        val IS_ANGRY = bitmask(BEE_FLAGS, 0x02, false)
        val HAS_STUNG = bitmask(BEE_FLAGS, 0x04, false)
        val HAS_NECTAR = bitmask(BEE_FLAGS, 0x08, false)
        val ANGER_TIME_TICKS = define(MetadataType.VAR_INT, 0)
    }

    object GlowSquid : MetadataGroup(AgeableMob) {
        val DARK_TICKS_REMAINING = define(MetadataType.VAR_INT, 0)
    }

    object Fox : MetadataGroup(AgeableMob) {
        val VARIANT = define(MetadataType.VAR_INT, 0)
        val FOX_FLAGS = define(MetadataType.BYTE, 0)
        val IS_SITTING = bitmask(FOX_FLAGS, 0x01, false)
        val IS_CROUCHING = bitmask(FOX_FLAGS, 0x04, false)
        val IS_INTERESTED = bitmask(FOX_FLAGS, 0x08, false)
        val IS_POUNCING = bitmask(FOX_FLAGS, 0x10, false)
        val IS_SLEEPING = bitmask(FOX_FLAGS, 0x20, false)
        val IS_FACEPLANTED = bitmask(FOX_FLAGS, 0x40, false)
        val IS_DEFENDING = bitmask(FOX_FLAGS, 0x80.toByte(), false)
        val FIRST_UUID = define(MetadataType.OPTIONAL_UUID, null)
        val SECOND_UUID = define(MetadataType.OPTIONAL_UUID, null)
    }

    object Frog : MetadataGroup(AgeableMob) {
        val VARIANT = define(MetadataType.FROG_VARIANT, FrogVariants.TEMPERATE)
        val TONGUE_TARGET = define(MetadataType.OPTIONAL_VAR_INT, 0)
    }

    object Ocelot : MetadataGroup(AgeableMob) {
        val IS_TRUSTING = define(MetadataType.BOOLEAN, false)
    }

    object Panda : MetadataGroup(AgeableMob) {
        val BREED_TIMER = define(MetadataType.VAR_INT, 0)
        val SNEEZE_TIMER = define(MetadataType.VAR_INT, 0)
        val EAT_TIMER = define(MetadataType.VAR_INT, 0)
        val MAIN_GENE = define(MetadataType.BYTE, 0)
        val HIDDEN_GENE = define(MetadataType.BYTE, 0)
        val PANDA_FLAGS = define(MetadataType.BYTE, 0)
        val IS_SNEEZING = bitmask(PANDA_FLAGS, 0x02, false)
        val IS_ROLLING = bitmask(PANDA_FLAGS, 0x04, false)
        val IS_SITTING = bitmask(PANDA_FLAGS, 0x08, false)
        val IS_ON_BACK = bitmask(PANDA_FLAGS, 0x10, false)
    }

    object Chicken : MetadataGroup(AgeableMob) {
        val VARIANT = define(MetadataType.CHICKEN_VARIANT, ChickenVariants.TEMPERATE)
    }

    object Cow : MetadataGroup(AgeableMob) {
        val VARIANT = define(MetadataType.COW_VARIANT, CowVariants.TEMPERATE)
    }

    object Pig : MetadataGroup(AgeableMob) {
        val VARIANT = define(MetadataType.PIG_VARIANT, PigVariants.TEMPERATE)
    }

    //TODO rabbit

    object Turtle : MetadataGroup(AgeableMob) {
        val HAS_EGG = define(MetadataType.BOOLEAN, false)
        val IS_LAYING_EGGS = define(MetadataType.BOOLEAN, false)
    }

    object PolarBear : MetadataGroup(AgeableMob) {
        val IS_STANDING_UP = define(MetadataType.BOOLEAN, false)
    }

    object Mooshroom : MetadataGroup(AgeableMob) {
        val VARIANT = define(MetadataType.VAR_INT, 0)
    }

    object Hoglin : MetadataGroup(AgeableMob) {
        val IMMUNE_ZOMBIFICATION = define(MetadataType.BOOLEAN, false)
    }

    object Sheep : MetadataGroup(AgeableMob) {
        val SHEEP_FLAGS = define(MetadataType.BYTE, 0)
        //TODO bytemask
    }

    object Strider : MetadataGroup(AgeableMob) {
        val FUNGUS_BOOST = define(MetadataType.VAR_INT, 0)
        val IS_SHAKING = define(MetadataType.BOOLEAN, false)
    }

    object Goat : MetadataGroup(AgeableMob) {
        val IS_SCREAMING_GOAT = define(MetadataType.BOOLEAN, false) // me too goat, me too
        val HAS_LEFT_HORN = define(MetadataType.BOOLEAN, true)
        val HAS_RIGHT_HORN = define(MetadataType.BOOLEAN, true)
    }

    object TameableAnimal : MetadataGroup(AgeableMob) {
        val TAMABLE_ANIMAL_FLAGS = define(MetadataType.BYTE, 0)
        val IS_SITTING = bitmask(TAMABLE_ANIMAL_FLAGS, 0x01, false)
        val IS_TAMED = bitmask(TAMABLE_ANIMAL_FLAGS, 0x04, false)
        val OWNER = define(MetadataType.OPTIONAL_UUID, null)
    }

    object Cat : MetadataGroup(TameableAnimal) {
        val VARIANT = define(MetadataType.CAT_VARIANT, CatVariants.BLACK)
        val IS_LYING = define(MetadataType.BOOLEAN, false)
        val IS_RELAXED = define(MetadataType.BOOLEAN, false)
        val COLLAR_COLOR = define(MetadataType.VAR_INT, 14)
    }

    object Wolf : MetadataGroup(TameableAnimal) {
        val IS_BEGGING = define(MetadataType.BOOLEAN, false)
        val COLLAR_COLOR = define(MetadataType.VAR_INT, 14)
        val ANGER_TIME = define(MetadataType.VAR_INT, 0)
        val VARIANT = define(MetadataType.WOLF_VARIANT, WolfVariants.PALE)
        val SOUND_VARIANT = define(MetadataType.WOLF_SOUND_VARIANT, WolfSoundVariants.CLASSIC)
    }

    object Parrot : MetadataGroup(AgeableMob) {
        val VARIANT = define(MetadataType.VAR_INT, 0)
    }

    object AbstractVillager : MetadataGroup(AgeableMob) {
        val HEAD_SHAKE_TIMER = define(MetadataType.VAR_INT, 0)
    }

    //TODO Villager (need to do villager data, professions and stuff)
//    object Villager: MetadataGroup(AbstractVillager) {
//
//    }

    object HappyGhast : MetadataGroup(AgeableMob) {
        val IS_LEASH_HOLDER = define(MetadataType.BOOLEAN, false)
        val STAYS_STILL = define(MetadataType.BOOLEAN, false)
    }

    object IronGolem: MetadataGroup(Mob) {
        val IRON_GOLEM_FLAGS = define(MetadataType.BYTE, 0)
        val IS_PLAYER_CREATED = bitmask(IRON_GOLEM_FLAGS, 0x01, false)
    }

    object SnowGolem: MetadataGroup(Mob) {
        val SNOW_GOLEM_FLAGS = define(MetadataType.BYTE, 0)
        val PUMPKIN_HAT = bitmask(SNOW_GOLEM_FLAGS, 0x01, false)
    }

    object Shulker: MetadataGroup(Mob) {
        val ATTACH_FACE = define(MetadataType.DIRECTION, Direction.DOWN)
        val SHIELD_HEIGHT = define(MetadataType.BYTE, 0)
        val COLOR = define(MetadataType.BYTE, 16)
    }

    object CopperGolem: MetadataGroup(Mob) {
        val WEATHER_STATE = define(MetadataType.COPPER_GOLEM_WEATHER_STATE, io.github.dockyardmc.entity.entities.CopperGolem.WeatherState.UNAFFECTED)
        val STATE = define(MetadataType.COPPER_GOLEM_STATE, io.github.dockyardmc.entity.entities.CopperGolem.State.IDLE)
    }

    object BasePiglin: MetadataGroup(Mob) {
        val IMMUNE_ZOMBIFICATION = define(MetadataType.BOOLEAN, false)
    }

    object Piglin: MetadataGroup(BasePiglin) {
        val IS_BABY = define(MetadataType.BOOLEAN, false)
        val IS_CHARING_CROSSBOW = define(MetadataType.BOOLEAN, false)
        val IS_DANCING = define(MetadataType.BOOLEAN, false)
    }

    object Blaze: MetadataGroup(Mob) {
        val BLAZE_FLAGS = define(MetadataType.BYTE, 0)
        val IS_ON_FIRE = bitmask(BLAZE_FLAGS, 0x01, false)
    }

    object Bogged: MetadataGroup(Mob) {
        val IS_SHEARED = define(MetadataType.BOOLEAN, false)
    }

    object Creeking: MetadataGroup(Mob) {
        val CAN_MOVE = define(MetadataType.BOOLEAN, true)
        val IS_ACTIVE = define(MetadataType.BOOLEAN, false)
        val IS_TEARING_DOWN = define(MetadataType.BOOLEAN, false)
        val HOME_POS = define(MetadataType.OPTIONAL_BLOCK_POSITION, null)
    }

    object Creeper: MetadataGroup(Mob) {
        val STATE = define(MetadataType.VAR_INT, -1)
        val IS_CHARGED = define(MetadataType.BOOLEAN, false)
        val IS_IGNITED = define(MetadataType.BOOLEAN, false)
    }

    object Guardian: MetadataGroup(Mob) {
        val IS_RETRACTING_SPIKES = define(MetadataType.BOOLEAN, false)
        val TARGET_EID = define(MetadataType.VAR_INT, 0)
    }

    object Raider: MetadataGroup(Mob) {
        val IS_CELEBRATING = define(MetadataType.BOOLEAN, false)
    }

    object Pillager: MetadataGroup(Raider) {
        val IS_CHARING = define(MetadataType.BOOLEAN, false)
    }

    object SpellcasterIllager: MetadataGroup(Raider) {
        val SPELL = define(MetadataType.BYTE, 0)
    }

    object Witch: MetadataGroup(Raider) {
        val IS_DRINKING_POTION = define(MetadataType.BOOLEAN, false)
    }

    object Spider: MetadataGroup(Mob) {
        val SPIDER_FLAGS = define(MetadataType.BYTE, 0)
        val IS_CLIMBING = bitmask(SPIDER_FLAGS, 0x01, false)
    }

    object Vex: MetadataGroup(Mob) {
        val VEX_FLAGS = define(MetadataType.BYTE, 0)
        val IS_ATTACKING = bitmask(VEX_FLAGS, 0x01, false)
    }

    object Warden: MetadataGroup(Mob) {
        val ANGER_LEVEL = define(MetadataType.VAR_INT, 0)
    }

    object Wither: MetadataGroup(Mob) {
        val CENTER_HEAD_TARGET = define(MetadataType.VAR_INT, 0)
        val LEFT_HEAD_TARGET = define(MetadataType.VAR_INT, 0)
        val RIGHT_HEAD_TARGET = define(MetadataType.VAR_INT, 0)
        val INVULNERABLE_TIME = define(MetadataType.VAR_INT, 0)
    }

    object Zoglin: MetadataGroup(Mob) {
        val IS_BABY = define(MetadataType.BOOLEAN, false)
    }

    object Zombie: MetadataGroup(Mob) {
        val IS_BABY = define(MetadataType.BOOLEAN, false)
        val IS_BECOMING_DROWNED = define(MetadataType.BOOLEAN, false)
    }

    object ZombieVillager: MetadataGroup(Mob) {
        val IS_CONVERTING = define(MetadataType.BOOLEAN, false)
        //TODO VillagerData
    }

    object Enderman: MetadataGroup(Mob) {
        val CARRIED_BLOCK = define(MetadataType.OPTIONAL_BLOCK_STATE, null)
        val IS_SCREAMING = define(MetadataType.BOOLEAN, false)
        val IS_STARING = define(MetadataType.BOOLEAN, false)
    }

    object EnderDragon: MetadataGroup(Mob) {
        val DRAGON_PHASE = define(MetadataType.VAR_INT, 10)
    }

    object Ghast: MetadataGroup(Mob) {
        val IS_ATTACKING = define(MetadataType.BOOLEAN, false)
    }

    object Phantom: MetadataGroup(Mob) {
        val SIZE = define(MetadataType.VAR_INT, 0)
    }

    object Slime: MetadataGroup(Mob) {
        val SIZE = define(MetadataType.VAR_INT, 1)
    }

    object PrimedTnt: MetadataGroup(Metadata) {
        val FUSE_TIME = define(MetadataType.VAR_INT, 80)
        val BLOCK_STATE = define(MetadataType.BLOCK_STATE, DEFAULT_PRIMED_TNT_BLOCK_STATE)
    }

    object OminousItemSpawner: MetadataGroup(Metadata) {
        val ITEM = define(MetadataType.ITEM_STACK, ItemStack.AIR)
    }

    interface MetadataDefinitionEntry<T>

    data class MetadataDefinition<T>(val index: Int, val type: MetadataSerializer<T>, val default: T) : MetadataDefinitionEntry<T>

    data class BitmaskFlagDefinition<T>(
        val parent: MetadataDefinition<Byte>,
        val bitMask: Byte,
        val defaultValue: T
    ) : MetadataDefinitionEntry<T>

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