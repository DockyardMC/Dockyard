package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.scheduler.CustomRateScheduler
import io.github.dockyardmc.world.World

@EventDocumentation("when world ticks", true)
class WorldTickEvent(val world: World, val scheduler: CustomRateScheduler, override val context: Event.Context) : CancellableEvent() {

}