package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when player enters the configuration phase", false)
data class PlayerEnterConfigurationEvent(val player: Player, override val context: Event.Context) : Event