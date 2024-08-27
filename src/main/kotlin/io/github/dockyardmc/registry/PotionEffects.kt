package io.github.dockyardmc.registry

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEffectPacket
import io.netty.buffer.ByteBuf

object PotionEffects {

    val potions: MutableMap<String, PotionEffect> = mutableMapOf(
        "speed" to PotionEffect(0, "speed", "Speed", PotionEffectType.GOOD),
        "slowness" to PotionEffect(1, "slowness", "Slowness", PotionEffectType.BAD),
        "haste" to PotionEffect(2, "haste", "Haste", PotionEffectType.GOOD),
        "mining_fatigue" to PotionEffect(3, "mining_fatigue", "Mining Fatigue", PotionEffectType.BAD),
        "strength" to PotionEffect(4, "strength", "Strength", PotionEffectType.GOOD),
        "instant_health" to PotionEffect(5, "instant_health", "Instant Health", PotionEffectType.GOOD),
        "instant_damage" to PotionEffect(6, "instant_damage", "Instant Damage", PotionEffectType.BAD),
        "jump_boost" to PotionEffect(7, "jump_boost", "Jump Boost", PotionEffectType.GOOD),
        "nausea" to PotionEffect(8, "nausea", "Nausea", PotionEffectType.BAD),
        "regeneration" to PotionEffect(9, "regeneration", "Regeneration", PotionEffectType.GOOD),
        "resistance" to PotionEffect(10, "resistance", "Resistance", PotionEffectType.GOOD),
        "fire_resistance" to PotionEffect(11, "fire_resistance", "Fire Resistance", PotionEffectType.GOOD),
        "water_breathing" to PotionEffect(12, "water_breathing", "Water Breathing", PotionEffectType.GOOD),
        "invisibility" to PotionEffect(13, "invisibility", "Invisibility", PotionEffectType.GOOD),
        "blindness" to PotionEffect(14, "blindness", "Blindness", PotionEffectType.BAD),
        "night_vision" to PotionEffect(15, "night_vision", "Night Vision", PotionEffectType.GOOD),
        "hunger" to PotionEffect(16, "hunger", "Hunger", PotionEffectType.BAD),
        "weakness" to PotionEffect(17, "weakness", "Weakness", PotionEffectType.BAD),
        "poison" to PotionEffect(18, "poison", "Poison", PotionEffectType.BAD),
        "wither" to PotionEffect(19, "wither", "Wither", PotionEffectType.BAD),
        "health_boost" to PotionEffect(20, "health_boost", "Health Boost", PotionEffectType.GOOD),
        "absorption" to PotionEffect(21, "absorption", "Absorption", PotionEffectType.GOOD),
        "saturation" to PotionEffect(22, "saturation", "Saturation", PotionEffectType.GOOD),
        "glowing" to PotionEffect(23, "glowing", "Glowing", PotionEffectType.BAD),
        "levitation" to PotionEffect(24, "levitation", "Levitation", PotionEffectType.BAD),
        "luck" to PotionEffect(25, "luck", "Luck", PotionEffectType.GOOD),
        "unluck" to PotionEffect(26, "unluck", "Bad Luck", PotionEffectType.BAD),
        "slow_falling" to PotionEffect(27, "slow_falling", "Slow Falling", PotionEffectType.GOOD),
        "conduit_power" to PotionEffect(28, "conduit_power", "Conduit Power", PotionEffectType.GOOD),
        "dolphins_grace" to PotionEffect(29, "dolphins_grace", "Dolphin's Grace", PotionEffectType.GOOD),
        "bad_omen" to PotionEffect(30, "bad_omen", "Bad Omen", PotionEffectType.BAD),
        "hero_of_the_village" to PotionEffect(31, "hero_of_the_village", "Hero of the Village", PotionEffectType.GOOD),
        "darkness" to PotionEffect(32, "darkness", "Darkness", PotionEffectType.BAD),
        "trial_omen" to PotionEffect(33, "trial_omen", "Trial Omen", PotionEffectType.BAD),
        "raid_omen" to PotionEffect(34, "raid_omen", "Raid Omen", PotionEffectType.BAD),
        "wind_charged" to PotionEffect(35, "wind_charged", "Wind Charged", PotionEffectType.BAD),
        "weaving" to PotionEffect(36, "weaving", "Weaving", PotionEffectType.BAD),
        "oozing" to PotionEffect(37, "oozing", "Oozing", PotionEffectType.BAD),
        "infested" to PotionEffect(38, "infested", "Infested", PotionEffectType.BAD),
    )

    fun getPotionEffect(effect: String): PotionEffect = potions[effect] ?: throw Exception("Potion effect $effect is not in the registry!")

    val SPEED = getPotionEffect("speed")
    val SLOWNESS = getPotionEffect("slowness")
    val HASTE = getPotionEffect("haste")
    val MINING_FATIGUE = getPotionEffect("mining_fatigue")
    val STRENGTH = getPotionEffect("strength")
    val INSTANT_HEALTH = getPotionEffect("instant_health")
    val INSTANT_DAMAGE = getPotionEffect("instant_damage")
    val JUMP_BOOST = getPotionEffect("jump_boost")
    val NAUSEA = getPotionEffect("nausea")
    val REGENERATION = getPotionEffect("regeneration")
    val RESISTANCE = getPotionEffect("resistance")
    val FIRE_RESISTANCE = getPotionEffect("fire_resistance")
    val WATER_BREATHING = getPotionEffect("water_breathing")
    val INVISIBILITY = getPotionEffect("invisibility")
    val BLINDNESS = getPotionEffect("blindness")
    val NIGHT_VISION = getPotionEffect("night_vision")
    val HUNGER = getPotionEffect("hunger")
    val WEAKNESS = getPotionEffect("weakness")
    val POISON = getPotionEffect("poison")
    val WITHER = getPotionEffect("wither")
    val HEALTH_BOOST = getPotionEffect("health_boost")
    val ABSORPTION = getPotionEffect("absorption")
    val SATURATION = getPotionEffect("saturation")
    val GLOWING = getPotionEffect("glowing")
    val LEVITATION = getPotionEffect("levitation")
    val LUCK = getPotionEffect("luck")
    val BAD_LUCK = getPotionEffect("unluck")
    val SLOW_FALLING = getPotionEffect("slow_falling")
    val CONDUIT_POWER = getPotionEffect("conduit_power")
    val DOLPHINS_GRACE = getPotionEffect("dolphins_grace")
    val BAD_OMEN = getPotionEffect("bad_omen")
    val HERO_OF_THE_VILLAGE = getPotionEffect("hero_of_the_village")
    val DARKNESS = getPotionEffect("darkness")
    val TRIAL_OMEN = getPotionEffect("trial_omen")
    val RAID_OMEN = getPotionEffect("raid_omen")
    val WIND_CHARGED = getPotionEffect("wind_charged")
    val WEAVING = getPotionEffect("weaving")
    val OOZING = getPotionEffect("oozing")
    val INFESTED = getPotionEffect("infested")
}

data class PotionEffect(
    val id: Int,
    val namespace: String,
    val name: String,
    val type: PotionEffectType
)
enum class PotionEffectType {
    GOOD,
    BAD
}

data class AppliedPotionEffect(
    var effect: PotionEffect,
    var duration: Int,
    var level: Int = 1,
    var showParticles: Boolean = false,
    var showBlueBorder: Boolean = false,
    var showIconOnHud: Boolean = false,
    var startTime: Long? = null
)

fun Entity.addPotionEffect(
    effect: PotionEffect,
    duration: Int,
    level: Int = 1,
    showParticles: Boolean = false,
    showBlueBorder: Boolean = false,
    showIconOnHud: Boolean = false,
) {
    val potionEffect = AppliedPotionEffect(effect, duration, level, showParticles, showBlueBorder, showIconOnHud)
    this.potionEffects[effect] = potionEffect
}

fun Entity.removePotionEffect(effect: PotionEffect) {
    this.potionEffects.remove(effect)
}

fun Entity.removePotionEffect(effect: AppliedPotionEffect) {
    this.potionEffects.remove(effect.effect)
}

fun Entity.clearPotionEffects() {
    this.potionEffects.clear()
}

fun Entity.refreshPotionEffects() {
    viewers.forEach(::sendPotionEffectsPacket)
    if(this is Player) this.sendPotionEffectsPacket(this)
}

fun Entity.sendPotionEffectsPacket(player: Player) {
    potionEffects.values.values.forEach {
        val packet = ClientboundEntityEffectPacket(this, it.effect, it.level, it.duration, it.showParticles, it.showBlueBorder, it.showIconOnHud)
        player.sendPacket(packet)
    }
}

fun ByteBuf.readAppliedPotionEffect(): AppliedPotionEffect {
    val id = this.readVarInt()
    val amplifier = this.readVarInt()
    val duration = this.readVarInt()
    val ambient = this.readBoolean()
    val showParticles = this.readBoolean()
    val showIcon = this.readBoolean()

    val effect = PotionEffects.potions.values.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Potion effect with id $id does not exist in the registry!")
    return AppliedPotionEffect(effect, duration, amplifier, showParticles, ambient, showIcon)
}