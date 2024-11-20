package io.github.dockyardmc.player.systems

import io.github.dockyardmc.item.*
import io.github.dockyardmc.particles.ItemParticleData
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityEvent
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.randomFloat
import io.github.dockyardmc.utils.vectors.Vector3f

class FoodEatingSystem(val player: Player): TickablePlayerSystem {

    override fun tick() {
        val world = player.world
        val location = player.location
        val food = player.food
        val saturation = player.saturation

        if(player.itemInUse != null) {
            val item = player.itemInUse!!.item

            if(!item.isSameAs(player.getHeldItem(PlayerHand.MAIN_HAND))) {
                player.itemInUse = null
                return
            }

            val isFood = item.components.hasType(FoodItemComponent::class)
            if(isFood) {

                if((world.worldAge % 5) == 0L) {
                    val viewers = world.players.toMutableList().filter { it != player }
                    viewers.playSound(item.material.consumeSound, location, 1f, randomFloat(0.9f, 1.3f))
                    viewers.spawnParticle(location.clone().apply { y += 1.5 }, Particles.ITEM, Vector3f(0.2f), 0.05f, 6, false, ItemParticleData(item))
                }

                if(world.worldAge - player.itemInUse!!.startTime >= player.itemInUse!!.time && player.itemInUse!!.time > 0) {
                    world.playSound("minecraft:entity.player.burp", location)
                    val component = item.components.firstOrNullByType<FoodItemComponent>(FoodItemComponent::class)!!

                    val foodToAdd = component.nutrition + food.value
                    if(foodToAdd > 20) {
                        val saturationToAdd = food.value - 20
                        food.value = 20.0
                        saturation.value = saturationToAdd.toFloat()
                    } else {
                        food.value = foodToAdd
                    }

                    // notify the client that eating is finished
                    player.sendPacket(ClientboundEntityEventPacket(player, EntityEvent.PLAYER_ITEM_USE_FINISHED))

                    val newItem = if(item.amount == 1) ItemStack.AIR else item.clone().apply { amount -= 1 }
                    player.inventory[player.heldSlotIndex.value] = newItem

                    // if new item is air, stop eating, if not, reset eating time
                    if(!newItem.isSameAs(ItemStack.AIR)) {
                        player.itemInUse!!.startTime = world.worldAge
                        player.itemInUse!!.item = newItem
                    } else {
                        player.itemInUse = null
                    }
                }
            }
        }
    }

    override fun dispose() {
    }
}