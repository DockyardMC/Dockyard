package io.github.dockyardmc.plugins.bundled.emberseeker.entities

import io.github.dockyardmc.entities.ai.AIManager
import io.github.dockyardmc.entities.ai.ShortTermMemory
import io.github.dockyardmc.entities.ai.WardenAttackPlayerGoal
import io.github.dockyardmc.entities.ai.WardenSniffAIGoal
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerDamageEntityEvent
import io.github.dockyardmc.events.PlayerInteractWithEntityEvent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.World

class EmberSeekerWarden(override var location: Location, override var world: World): Warden(location, world) {

    var ai = AIManager(this)
    init {
        ai.addGoal(WardenSniffAIGoal(this, 1))
        ai.addGoal(WardenAttackPlayerGoal(this, 10))

        Events.on<PlayerInteractWithEntityEvent> {
            val entity = it.entity
            if(entity != this) return@on
            playAnimation(WardenAnimation.TENDRIL_SHAKE)
            angerLevel.value += 10
        }

        Events.on<PlayerDamageEntityEvent> {
            if(it.entity != this) return@on
            angerAt(it.player)
        }
    }

    fun angerAt(player: Player) {
        ai.memory["target"] = ShortTermMemory<Player>(300, player)
    }
}