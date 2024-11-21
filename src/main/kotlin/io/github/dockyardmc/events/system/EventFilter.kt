package io.github.dockyardmc.events.system

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.World

fun interface EventFilter {
    /**
     * Checks an event against this filter
     * @return true if the Event satisfies the conditions
     * of this Filter, and should be dispatched
     */
    fun check(event: Event): Boolean

    companion object {
        /**
         * An empty EventFilter, always allows events through
         */
        fun empty() = EventFilter { true }

        fun containsPlayer(obj: Player) = EventFilter { it.context.players.contains(obj) }
        fun containsEntity(obj: Entity) = EventFilter { it.context.entities.contains(obj) }
        fun containsWorld(obj: World) = EventFilter { it.context.worlds.contains(obj) }
        fun containsObject(obj: Any) = EventFilter { it.context.other.contains(obj) }

        fun all(vararg filters: EventFilter) = EventFilter { evt -> filters.all { it.check(evt) } }
        fun any(vararg filters: EventFilter) = EventFilter { evt -> filters.any { it.check(evt) } }
    }
}