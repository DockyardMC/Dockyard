package io.github.dockyardmc.events

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.World

interface Event {
    val context: Context

    class Context(
        players: Set<Player> = setOf<Player>(),
        entities: Set<Entity> = setOf<Entity>(),
        worlds: Set<World> = setOf<World>(),
        location: Set<Location> = setOf<Location>(),
        other: Set<Any> = setOf<Any>(),
        val isGlobalEvent: Boolean = false
    ) {
        // what the fuck
        val players = players + entities.filterIsInstance<Player>()
        val entities = entities + players
        val locations = location + this.entities.map { it.location }
        val worlds = worlds + this.locations.map { it.world }

        val other: Set<Any> = players + entities + worlds + location + other

        operator fun contains(element: Any) = other.contains(element)

        // i hate everything about this
        // please suggest something better.
    }
}