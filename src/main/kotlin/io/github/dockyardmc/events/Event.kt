package io.github.dockyardmc.events

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.events.system.EventFilter
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.World

interface Event {
    val context: Context

    class Context(
        players: Set<Player> = setOf<Player>(),
        entities: Set<Entity> = setOf<Entity>(),
        worlds: Set<World> = setOf<World>(),
        locations: Set<Location> = setOf<Location>(),
        other: Set<Any> = setOf<Any>(),
        val isGlobalEvent: Boolean = false
    ) {
        // combining sets is expensive and is done in initialization of every event.
        // In most cases, either none or only one is accessed. Let's make them lazy so they are
        // computed only when needed
        val players: Set<Player> by lazy {
            players + entities.filterIsInstance<Player>()
        }

        val entities: Set<Entity> by lazy {
            entities + players
        }

        val locations: Set<Location> by lazy {
            locations + this.entities.map { entity -> entity.location }
        }
        val worlds: Set<World> by lazy {
            worlds + this.locations.map { location -> location.world }
        }

        val other: Set<Any> by lazy {
            this.players + this.entities + this.worlds + this.locations + other
        }

        operator fun contains(element: Any) = other.contains(element)

        // i hate everything about this
        // please suggest something better.
    }
}

operator fun EventFilter.not(): EventFilter {
    return EventFilter { !this.check(it) }
}