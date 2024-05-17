package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.world.World

@EventDocumentation("when world border size changes", true)
class BorderSizeChangeEvent(var oldValue: Double, var newValue: Double, var speed: Long, world: World): Event