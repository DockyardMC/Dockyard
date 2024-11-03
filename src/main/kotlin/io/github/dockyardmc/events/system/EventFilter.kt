package io.github.dockyardmc.events.system

import io.github.dockyardmc.entities.Entity
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
        fun isGlobalOr(filter: EventFilter) = EventFilter { it.context.isGlobalEvent || filter.check(it) }

        fun containsPlayer(obj: Player) = isGlobalOr { it.context.players.contains(obj) }
        fun containsEntity(obj: Entity) = isGlobalOr { it.context.entities.contains(obj) }
        fun containsWorld(obj: World) = isGlobalOr { it.context.worlds.contains(obj) }
        fun containsObject(obj: Any) = isGlobalOr { it.context.other.contains(obj) }

        fun all(vararg filters: EventFilter) = EventFilter { evt -> filters.all { it.check(evt) } }
        fun any(vararg filters: EventFilter) = EventFilter { evt -> filters.any { it.check(evt) } }
    }
}