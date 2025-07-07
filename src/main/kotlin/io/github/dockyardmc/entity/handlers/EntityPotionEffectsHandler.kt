package io.github.dockyardmc.entity.handlers

import cz.lukynka.bindables.BindableMap
import io.github.dockyardmc.effects.AppliedPotionEffect
import io.github.dockyardmc.effects.PotionEffectAttributes
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEffectPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundRemoveEntityEffectPacket
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import io.github.dockyardmc.utils.ticksToMs

class EntityPotionEffectsHandler(override val entity: Entity) : TickableEntityHandler {

    fun handle(potionEffects: BindableMap<PotionEffect, AppliedPotionEffect>) {
        potionEffects.itemSet {
            it.value.startTime = System.currentTimeMillis()
            val packet = ClientboundEntityEffectPacket(
                entity,
                it.value.effect,
                it.value.settings.amplifier,
                it.value.settings.duration.inWholeMinecraftTicks,
                it.value.settings.showParticles,
                it.value.settings.isAmbient,
                it.value.settings.showIcon
            )

            entity.sendPacketToViewers(packet)
            entity.sendSelfPacketIfPlayer(packet)
            if(entity is Player) PotionEffectAttributes.onEffectApply(entity, it.value)
        }

        potionEffects.itemRemoved {
            val packet = ClientboundRemoveEntityEffectPacket(entity, it.value)
            entity.sendPacketToViewers(packet)
            if(entity is Player) PotionEffectAttributes.onEffectRemoved(entity, it.value.effect)
            entity.sendSelfPacketIfPlayer(packet)
        }
    }

    override fun tick() {
        entity.potionEffects.values.forEach { effect ->
            if(effect.value.settings.duration.isInfinite()) return@forEach
            if (System.currentTimeMillis() >= effect.value.startTime!! + ticksToMs(effect.value.settings.duration.inWholeMinecraftTicks)) {
                entity.potionEffects.remove(effect.key)
            }
        }
    }
}