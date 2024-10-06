package io.github.dockyardmc.effects

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.registry.registries.PotionEffect

object PotionEffectImpl {

    //TODO Implement attributes
    fun onEffectApply(entity: Entity, effect: PotionEffect) {
        when(effect.identifier) {
            "speed" -> {}
            "slowness" -> {}
            "haste" -> {}
            "mining_fatigue" -> {}
            "strength" -> {}
            "instant_health" -> {}
            "instant_damage" -> {}
            "jump_boost" -> {}
            "regeneration" -> {}
            "resistance" -> {}
            "fire_resistance" -> {}
            "water_breathing" -> {}
            "invisibility" -> entity.isInvisible.value = true
            "glowing" -> entity.isGlowing.value = true
        }
    }

    fun onEffectRemoved(entity: Entity, effect: PotionEffect) {
        when(effect.identifier) {
            "speed" -> {}
            "slowness" -> {}
            "haste" -> {}
            "mining_fatigue" -> {}
            "strength" -> {}
            "instant_health" -> {}
            "instant_damage" -> {}
            "jump_boost" -> {}
            "regeneration" -> {}
            "resistance" -> {}
            "fire_resistance" -> {}
            "water_breathing" -> {}
            "invisibility" -> entity.isInvisible.value = false
            "glowing" -> entity.isGlowing.value = false
        }
    }
}