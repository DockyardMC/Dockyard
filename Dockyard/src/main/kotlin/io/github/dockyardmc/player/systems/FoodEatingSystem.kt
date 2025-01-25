package io.github.dockyardmc.player.systems

import io.github.dockyardmc.item.*
import io.github.dockyardmc.particles.ItemParticleData
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.ItemInUse
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityEvent
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.randomFloat
import io.github.dockyardmc.utils.vectors.Vector3f

class FoodEatingSystem(val player: Player) : TickablePlayerSystem {

    override fun tick() {
        val world = player.world
        val location = player.location
        val food = player.food
        val saturation = player.saturation

        if (player.itemInUse != null) {
            var item = player.itemInUse!!.item

            if (!item.isSameAs(player.getHeldItem(PlayerHand.MAIN_HAND))) {
                player.itemInUse = null
                return
            }

            item = player.getHeldItem(PlayerHand.MAIN_HAND)
            val consumableItemComponent = item.components.firstOrNullByType<ConsumableItemComponent>(ConsumableItemComponent::class)

            if ((world.worldAge % 5) == 0L) {
                val viewers = world.players.toMutableList().filter { it != player }
                viewers.playSound(item.material.consumeSound, location, 1f, randomFloat(0.9f, 1.3f))
                val particles = consumableItemComponent?.hasConsumeParticles ?: true
                if(particles) {
                    viewers.spawnParticle(
                        location.clone().apply { y += 1.5 },
                        Particles.ITEM,
                        Vector3f(0.2f),
                        0.05f,
                        6,
                        false,
                        ItemParticleData(item)
                    )
                }
            }

            if (world.worldAge - player.itemInUse!!.startTime >= player.itemInUse!!.time && player.itemInUse!!.time > 0) {
                val sound = getFinishConsumingSoundEffect(consumableItemComponent!!.animation)
                if(sound != null) world.playSound("minecraft:entity.player.burp", location)

                val component = item.components.firstOrNullByType<FoodItemComponent>(FoodItemComponent::class)
                if(component != null) {
                    val foodToAdd = component.nutrition + food.value
                    if (foodToAdd > 20) {
                        val saturationToAdd = food.value - 20
                        food.value = 20.0
                        saturation.value = saturationToAdd.toFloat()
                    } else {
                        food.value = foodToAdd
                    }
                }

                // notify the client that eating is finished
                player.sendPacket(ClientboundEntityEventPacket(player, EntityEvent.PLAYER_ITEM_USE_FINISHED))

                val newItem = if (item.amount == 1) ItemStack.AIR else item.clone().apply { amount -= 1 }
                player.inventory[player.heldSlotIndex.value] = newItem

                player.itemInUse = null
            }
        }
    }

    fun getFinishConsumingSoundEffect(animation: ConsumableAnimation): String? {
        return when(animation) {
            ConsumableAnimation.EAT -> "minecraft:entity.player.burp"
            ConsumableAnimation.DRINK -> "minecraft:entity.generic.drink"
            else -> null
        }
    }
}

fun startConsumingIfApplicable(item: ItemStack, player: Player) {
    val hasConsumableComponent = item.components.hasType(ConsumableItemComponent::class)
    if(hasConsumableComponent) {
        if(player.itemInUse != null) return
        val component = item.components.firstOrNullByType<ConsumableItemComponent>(ConsumableItemComponent::class)!!
        val eatingTime = component.consumeSeconds
        val useTime = (eatingTime * 20f).toInt()
        val startTime = player.world.worldAge
        player.itemInUse = ItemInUse(item, startTime, useTime.toLong())
    }
}