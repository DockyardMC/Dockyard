package io.github.dockyardmc.registry

import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry

object PotionEffects {
    val SPEED = PotionEffectRegistry["speed"]
    val SLOWNESS = PotionEffectRegistry["slowness"]
    val HASTE = PotionEffectRegistry["haste"]
    val MINING_FATIGUE = PotionEffectRegistry["mining_fatigue"]
    val STRENGTH = PotionEffectRegistry["strength"]
    val INSTANT_HEALTH = PotionEffectRegistry["instant_health"]
    val INSTANT_DAMAGE = PotionEffectRegistry["instant_damage"]
    val JUMP_BOOST = PotionEffectRegistry["jump_boost"]
    val NAUSEA = PotionEffectRegistry["nausea"]
    val REGENERATION = PotionEffectRegistry["regeneration"]
    val RESISTANCE = PotionEffectRegistry["resistance"]
    val FIRE_RESISTANCE = PotionEffectRegistry["fire_resistance"]
    val WATER_BREATHING = PotionEffectRegistry["water_breathing"]
    val INVISIBILITY = PotionEffectRegistry["invisibility"]
    val BLINDNESS = PotionEffectRegistry["blindness"]
    val NIGHT_VISION = PotionEffectRegistry["night_vision"]
    val HUNGER = PotionEffectRegistry["hunger"]
    val WEAKNESS = PotionEffectRegistry["weakness"]
    val POISON = PotionEffectRegistry["poison"]
    val WITHER = PotionEffectRegistry["wither"]
    val HEALTH_BOOST = PotionEffectRegistry["health_boost"]
    val ABSORPTION = PotionEffectRegistry["absorption"]
    val SATURATION = PotionEffectRegistry["saturation"]
    val GLOWING = PotionEffectRegistry["glowing"]
    val LEVITATION = PotionEffectRegistry["levitation"]
    val LUCK = PotionEffectRegistry["luck"]
    val BAD_LUCK = PotionEffectRegistry["unluck"]
    val SLOW_FALLING = PotionEffectRegistry["slow_falling"]
    val CONDUIT_POWER = PotionEffectRegistry["conduit_power"]
    val DOLPHINS_GRACE = PotionEffectRegistry["dolphins_grace"]
    val BAD_OMEN = PotionEffectRegistry["bad_omen"]
    val HERO_OF_THE_VILLAGE = PotionEffectRegistry["hero_of_the_village"]
    val DARKNESS = PotionEffectRegistry["darkness"]
    val TRIAL_OMEN = PotionEffectRegistry["trial_omen"]
    val RAID_OMEN = PotionEffectRegistry["raid_omen"]
    val WIND_CHARGED = PotionEffectRegistry["wind_charged"]
    val WEAVING = PotionEffectRegistry["weaving"]
    val OOZING = PotionEffectRegistry["oozing"]
    val INFESTED = PotionEffectRegistry["infested"]
}

data class AppliedPotionEffect(
    var effect: PotionEffect,
    var duration: Int,
    var level: Int = 1,
    var showParticles: Boolean = false,
    var showBlueBorder: Boolean = false,
    var showIconOnHud: Boolean = false,
    var startTime: Long? = null,
)