package io.github.dockyardmc.registry

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeOptional
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.netty.buffer.ByteBuf

object PotionEffects {
    val SPEED = PotionEffectRegistry["minecraft:speed"]
    val SLOWNESS = PotionEffectRegistry["minecraft:slowness"]
    val HASTE = PotionEffectRegistry["minecraft:haste"]
    val MINING_FATIGUE = PotionEffectRegistry["minecraft:mining_fatigue"]
    val STRENGTH = PotionEffectRegistry["minecraft:strength"]
    val INSTANT_HEALTH = PotionEffectRegistry["minecraft:instant_health"]
    val INSTANT_DAMAGE = PotionEffectRegistry["minecraft:instant_damage"]
    val JUMP_BOOST = PotionEffectRegistry["minecraft:jump_boost"]
    val NAUSEA = PotionEffectRegistry["minecraft:nausea"]
    val REGENERATION = PotionEffectRegistry["minecraft:regeneration"]
    val RESISTANCE = PotionEffectRegistry["minecraft:resistance"]
    val FIRE_RESISTANCE = PotionEffectRegistry["minecraft:fire_resistance"]
    val WATER_BREATHING = PotionEffectRegistry["minecraft:water_breathing"]
    val INVISIBILITY = PotionEffectRegistry["minecraft:invisibility"]
    val BLINDNESS = PotionEffectRegistry["minecraft:blindness"]
    val NIGHT_VISION = PotionEffectRegistry["minecraft:night_vision"]
    val HUNGER = PotionEffectRegistry["minecraft:hunger"]
    val WEAKNESS = PotionEffectRegistry["minecraft:weakness"]
    val POISON = PotionEffectRegistry["minecraft:poison"]
    val WITHER = PotionEffectRegistry["minecraft:wither"]
    val HEALTH_BOOST = PotionEffectRegistry["minecraft:health_boost"]
    val ABSORPTION = PotionEffectRegistry["minecraft:absorption"]
    val SATURATION = PotionEffectRegistry["minecraft:saturation"]
    val GLOWING = PotionEffectRegistry["minecraft:glowing"]
    val LEVITATION = PotionEffectRegistry["minecraft:levitation"]
    val LUCK = PotionEffectRegistry["minecraft:luck"]
    val BAD_LUCK = PotionEffectRegistry["minecraft:unluck"]
    val SLOW_FALLING = PotionEffectRegistry["minecraft:slow_falling"]
    val CONDUIT_POWER = PotionEffectRegistry["minecraft:conduit_power"]
    val DOLPHINS_GRACE = PotionEffectRegistry["minecraft:dolphins_grace"]
    val BAD_OMEN = PotionEffectRegistry["minecraft:bad_omen"]
    val HERO_OF_THE_VILLAGE = PotionEffectRegistry["minecraft:hero_of_the_village"]
    val DARKNESS = PotionEffectRegistry["minecraft:darkness"]
    val TRIAL_OMEN = PotionEffectRegistry["minecraft:trial_omen"]
    val RAID_OMEN = PotionEffectRegistry["minecraft:raid_omen"]
    val WIND_CHARGED = PotionEffectRegistry["minecraft:wind_charged"]
    val WEAVING = PotionEffectRegistry["minecraft:weaving"]
    val OOZING = PotionEffectRegistry["minecraft:oozing"]
    val INFESTED = PotionEffectRegistry["minecraft:infested"]
}

data class AppliedPotionEffect(
    var effect: PotionEffect,
    val settings: AppliedPotionEffectSettings,
    var startTime: Long? = null,
)

data class AppliedPotionEffectSettings(
    val amplifier: Int,
    val duration: Int,
    val isAmbient: Boolean,
    val showParticles: Boolean,
    val showIcon: Boolean,
    val hiddenEffect: AppliedPotionEffectSettings? = null
) {
    companion object {
        fun read(buffer: ByteBuf): AppliedPotionEffectSettings {
            val amplifier: Int = buffer.readVarInt()
            val duration: Int = buffer.readVarInt()
            val isAmbient: Boolean = buffer.readBoolean()
            val showParticles: Boolean = buffer.readBoolean()
            val showIcon: Boolean = buffer.readBoolean()
            var hiddenEffect: AppliedPotionEffectSettings? = null

            if(buffer.readBoolean()) {
                hiddenEffect = read(buffer)
            }
            return AppliedPotionEffectSettings(amplifier, duration, isAmbient, showParticles, showIcon, hiddenEffect)
        }
    }

    fun write(buffer: ByteBuf) {
        buffer.writeVarInt(amplifier)
        buffer.writeVarInt(duration)
        buffer.writeBoolean(isAmbient)
        buffer.writeBoolean(showIcon)
        buffer.writeBoolean(showIcon)
        buffer.writeOptional(hiddenEffect) {
            write(it)
        }
    }
}