package io.github.dockyardmc.player.systems

import cz.lukynka.BindableMap
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ItemGroupCooldownEndEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.Player.ItemGroupCooldown
import io.github.dockyardmc.utils.getPlayerEventContext
import io.github.dockyardmc.utils.ticksToMs

class CooldownSystem(val player: Player): TickablePlayerSystem {

    val cooldowns: BindableMap<String, ItemGroupCooldown> = player.bindablePool.provideBindableMap()

    override fun tick() {
        cooldowns.values.forEach { (group, cooldown) ->
            if (System.currentTimeMillis() >= cooldown.startTime + ticksToMs(cooldown.durationTicks)) {
                cooldowns.remove(group)
                Events.dispatch(ItemGroupCooldownEndEvent(player, cooldown, getPlayerEventContext(player)))
            }
        }
    }

    override fun dispose() {
        cooldowns.dispose()
    }

}