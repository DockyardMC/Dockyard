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
        // what the fuck
        val players = players + entities.filterIsInstance<Player>()
        val entities = entities + players
        val locations = locations + this.entities.map { it.location }
        val worlds = worlds + this.locations.map { it.world }

        val other: Set<Any> = this.players + this.entities + this.worlds + this.locations + other

        operator fun contains(element: Any) = other.contains(element)

        // i hate everything about this
        // please suggest something better.
    }
}

operator fun EventFilter.not(): EventFilter {
    return EventFilter { !this.check(it) }
}