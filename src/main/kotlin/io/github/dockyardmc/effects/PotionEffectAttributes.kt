package io.github.dockyardmc.effects

import io.github.dockyardmc.attributes.AttributeOperation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.registry.Attributes
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.registry.registries.PotionEffect

object PotionEffectAttributes {

    fun onEffectApply(entity: Player, effect: AppliedPotionEffect) {
        when (effect.effect) {
            PotionEffects.SPEED -> {
                entity.attributes[Attributes.MOVEMENT_SPEED].addModifier("minecraft:speed", effect.settings.amplifier * 0.20, AttributeOperation.MULTIPLY_BASE)
            }

            PotionEffects.SLOWNESS -> {
                entity.attributes[Attributes.MOVEMENT_SPEED].addModifier("minecraft:slowness", effect.settings.amplifier * -0.20, AttributeOperation.MULTIPLY_BASE)
            }

            PotionEffects.HASTE -> {
                entity.attributes[Attributes.ATTACK_SPEED].addModifier("minecraft:haste", effect.settings.amplifier * 10.0, AttributeOperation.MULTIPLY_BASE)
                entity.attributes[Attributes.MINING_EFFICIENCY].addModifier("minecraft:haste", effect.settings.amplifier * 20.0, AttributeOperation.MULTIPLY_BASE)
            }

            PotionEffects.MINING_FATIGUE -> {
                entity.attributes[Attributes.ATTACK_SPEED].addModifier("minecraft:haste", effect.settings.amplifier * -10.0, AttributeOperation.MULTIPLY_BASE)
                entity.attributes[Attributes.MINING_EFFICIENCY].addModifier("minecraft:haste", effect.settings.amplifier * -20.0, AttributeOperation.MULTIPLY_BASE)
            }

            PotionEffects.STRENGTH -> {}
            PotionEffects.INSTANT_HEALTH -> {}
            PotionEffects.INSTANT_DAMAGE -> {}
            PotionEffects.JUMP_BOOST -> {
                entity.attributes[Attributes.JUMP_STRENGTH].addModifier("minecraft:jump_boost", effect.settings.amplifier * 0.1, AttributeOperation.MULTIPLY_BASE)
            }

            PotionEffects.REGENERATION -> {}
            PotionEffects.RESISTANCE -> {}
            PotionEffects.FIRE_RESISTANCE -> {}
            PotionEffects.WATER_BREATHING -> {}
            PotionEffects.INVISIBILITY -> entity.isInvisible.value = true
            PotionEffects.GLOWING -> entity.isGlowing.value = true
        }
    }

    fun onEffectRemoved(player: Player, effect: PotionEffect) {
        when (effect) {
            PotionEffects.SPEED -> {
                player.attributes[Attributes.MOVEMENT_SPEED].removeModifier("minecraft:speed")
            }

            PotionEffects.SLOWNESS -> {
                player.attributes[Attributes.MOVEMENT_SPEED].removeModifier("minecraft:slowness")
            }

            PotionEffects.HASTE -> {
                player.attributes[Attributes.MINING_EFFICIENCY].removeModifier("minecraft:haste")
                player.attributes[Attributes.ATTACK_SPEED].removeModifier("minecraft:haste")
            }

            PotionEffects.MINING_FATIGUE -> {
                player.attributes[Attributes.MINING_EFFICIENCY].removeModifier("minecraft:mining_fatigue")
                player.attributes[Attributes.ATTACK_SPEED].removeModifier("minecraft:mining_fatigue")
            }

            PotionEffects.STRENGTH -> {}
            PotionEffects.INSTANT_HEALTH -> {}
            PotionEffects.INSTANT_DAMAGE -> {}
            PotionEffects.JUMP_BOOST -> {
                player.attributes[Attributes.JUMP_STRENGTH].removeModifier("minecraft:jump_boost")
            }

            PotionEffects.REGENERATION -> {}
            PotionEffects.RESISTANCE -> {}
            PotionEffects.FIRE_RESISTANCE -> {}
            PotionEffects.WATER_BREATHING -> {}
            PotionEffects.INVISIBILITY -> player.isInvisible.value = false
            PotionEffects.GLOWING -> player.isGlowing.value = false
        }
    }
}