package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when the current tick of client ends executing", false)
class ClientTickEndEvent(val player: Player): Event