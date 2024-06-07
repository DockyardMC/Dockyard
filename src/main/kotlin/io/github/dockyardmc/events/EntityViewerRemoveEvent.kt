package io.github.dockyardmc.events

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player

class EntityViewerRemoveEvent(var entity: Entity, var viewer: Player): CancellableEvent()