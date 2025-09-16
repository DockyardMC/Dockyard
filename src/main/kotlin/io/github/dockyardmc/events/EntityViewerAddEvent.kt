package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player

@EventDocumentation("when viewer is added to an entity viewer list")
class EntityViewerAddEvent(var entity: Entity, var viewer: Player, override val context: Event.Context) : CancellableEvent()