package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.Navigator

@EventDocumentation("when navigator selects locations in a path to fllow. You can use this event to add slight offset to your path for example", false)
data class EntityNavigatorPickOffsetEvent(val entity: Entity, val navigator: Navigator, var location: Location, override val context: Event.Context) : Event