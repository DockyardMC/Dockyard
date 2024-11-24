package io.github.dockyardmc.utils

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.world.World

fun getPlayerEventContext(player: Player): Event.Context {
    return Event.Context(
        setOf(player),
        setOf(player),
        setOf(player.world),
        setOf(player.location),
    )
}

fun getEntityEventContext(entity: Entity): Event.Context {
    return Event.Context(
        setOf(),
        setOf(entity),
        setOf(entity.world),
        setOf(entity.location),
    )
}

fun getLocationEventContext(location: Location): Event.Context {
    return Event.Context(
        setOf(),
        setOf(),
        setOf(location.world),
        setOf(location),
    )
}

fun getWorldEventContext(world: World): Event.Context {
    return Event.Context(
        setOf(),
        setOf(),
        setOf(world),
        setOf(),
    )
}

fun Event.Context.fistPlayerConditionOrFalse(condition: (Player) -> Boolean): Boolean {
    val player = players.firstOrNull() ?: return false
    return condition.invoke(player)
}

fun Event.Context.firstWorldCondition(condition: (World) -> Boolean): Boolean {
    val world = worlds.firstOrNull() ?: return false
    return condition.invoke(world)
}