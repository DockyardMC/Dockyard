package io.github.dockyardmc.player.systems

import io.github.dockyardmc.data.components.ConsumableComponent
import io.github.dockyardmc.data.components.FoodComponent
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.randomFloat
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.particles.data.ItemParticleData
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.ItemInUse
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityEvent
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.getPlayerEventContext
import kotlin.random.Random

class FoodEatingSystem(val player: Player) : TickablePlayerSystem {

    var tick = 0

    override fun tick() {
        val world = player.world
        val location = player.location
        val food = player.food
        val saturation = player.saturation

        if (player.itemInUse != null) {
            tick++
            var item = player.itemInUse!!.item

            if (!item.isSameAs(player.getHeldItem(PlayerHand.MAIN_HAND))) {
                player.itemInUse = null
                tick = 0
                Events.dispatch(PlayerCancelledConsumingEvent(player, item, getPlayerEventContext(player)))
                return
            }

            item = player.getHeldItem(PlayerHand.MAIN_HAND)
            val consumableComponent = item.components.get<ConsumableComponent>() ?: return

            Events.dispatch(PlayerConsumeItemTickEvent(player, item, tick, getPlayerEventContext(player)))

            // Eating
            if ((world.worldAge % 5) == 0L && consumableComponent.animation == ConsumableComponent.Animation.EAT) {
                val players = world.players.filter { it != player }
                players.playSound(consumableComponent.sound, location, 1f, Random.randomFloat(0.9f, 1.3f))
                if (consumableComponent.hasParticles) {
                    players.spawnParticle(
                        location = location.clone().apply { y += 1.5 },
                        particle = Particles.ITEM,
                        offset = Vector3f(0.2f),
                        speed = 0.05f,
                        amount = 6,
                        alwaysShow = false,
                        overrideLimiter = false,
                        particleData = ItemParticleData(item)
                    )
                }
            }

            if (world.worldAge - player.itemInUse!!.startTime >= player.itemInUse!!.time && player.itemInUse!!.time > 0) {
                val sound = getFinishConsumingSoundEffect(consumableComponent.animation)
                if (sound != null) world.playSound(sound, location)

                val component = item.components.get<FoodComponent>()
                if (component != null) {
                    val foodToAdd = component.nutrition + food.value
                    if (foodToAdd > 20) {
                        val saturationToAdd = food.value - 20
                        food.value = 20.0
                        saturation.value = saturationToAdd.toFloat()
                    } else {
                        food.value = foodToAdd
                    }
                }

                Events.dispatch(PlayerFinishConsumingEvent(player, item, getPlayerEventContext(player)))
                player.sendPacket(ClientboundEntityEventPacket(player, EntityEvent.PLAYER_ITEM_USE_FINISHED))
                player.itemInUse = null
                tick = 0

                if (consumableComponent.animation.decreasesAmount) {
                    val newItem = if (item.amount == 1) ItemStack.AIR else item.withAmount(item.amount - 1)
                    player.inventory[player.heldSlotIndex.value] = newItem
                }
            }
        }
    }

    fun getFinishConsumingSoundEffect(animation: ConsumableComponent.Animation): String? {
        return when (animation) {
            ConsumableComponent.Animation.EAT -> Sounds.ENTITY_PLAYER_BURP
            ConsumableComponent.Animation.DRINK -> Sounds.ENTITY_GENERIC_EAT
            else -> null
        }
    }

    override fun dispose() {}
}

fun startConsumingIfApplicable(item: ItemStack, player: Player) {
    val hasConsumableComponent = item.components.has(ConsumableComponent::class)
    if (hasConsumableComponent) {
        if (player.itemInUse != null) return
        val component = item.components.get<ConsumableComponent>() ?: return
        val event = PlayerStartConsumingEvent(player, item, getPlayerEventContext(player))
        Events.dispatch(event)
        if (event.cancelled) return

        val consumeTime = component.consumeSeconds
        val useTime = (consumeTime * 20f).toInt()
        val startTime = player.world.worldAge
        player.itemInUse = ItemInUse(item, startTime, useTime.toLong())
    }
}