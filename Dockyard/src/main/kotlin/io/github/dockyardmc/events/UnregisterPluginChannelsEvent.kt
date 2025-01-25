package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player

@EventDocumentation("when server receives the minecraft:unregister plugin message with list of custom channels", false)
class UnregisterPluginChannelsEvent(val player: Player, val channels: List<String>): Event {
    override val context = Event.Context(players = setOf(player))
}