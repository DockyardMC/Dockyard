package io.github.dockyardmc.effects

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.registry.PotionEffect

object PotionEffectImpl {

    //TODO Implement attributes
    fun onEffectApply(entity: Entity, effect: PotionEffect) {
        when(effect.namespace) {
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
            "invisibility" -> {}
            "hunger" -> {}
            "weakness" -> {}
            "poison" -> {}
            "wither" -> {}
            "health_boost" -> {}
            "absorption" -> {}
            "saturation" -> {}
            "glowing" -> {}
            "levitation" -> {}
            "conduit_power" -> {}
            "dolphins_grace" -> {}
            "bad_omen" -> {}
            "hero_of_the_village" -> {}
        }
    }
}