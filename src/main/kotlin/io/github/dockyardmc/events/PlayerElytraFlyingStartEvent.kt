package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player starts flying with elytra (not flight state change)", false)
class PlayerElytraFlyingStartEvent(player: Player): Event