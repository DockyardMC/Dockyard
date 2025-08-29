package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.world.World

@EventDocumentation("when world is finished loading")
data class WorldFinishLoadingEvent(val world: World, override val context: Event.Context) : Event