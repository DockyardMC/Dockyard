package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.world.World

@EventDocumentation("when world is finished loading", false)
class WorldFinishLoadingEvent(val world: World): Event {
    override val context = Event.Context(worlds = setOf(world))
}