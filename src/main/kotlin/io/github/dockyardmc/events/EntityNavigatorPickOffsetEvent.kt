package io.github.dockyardmc.events

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.Navigator

data class EntityNavigatorPickOffsetEvent(val entity: Entity, val navigator: Navigator, var location: Location, override val context: Event.Context): CancellableEvent()