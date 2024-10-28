package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when server receives the minecraft:register plugin message with list of custom channels", false)
class RegisterPluginChannelsEvent(val player: Player, val channels: List<String>): Event {
    override val context = elements(player)
}