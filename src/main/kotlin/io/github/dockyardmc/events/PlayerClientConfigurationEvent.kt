package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.ClientConfiguration
import io.github.dockyardmc.player.Player

@EventDocumentation("server receives information about client's configuration (client settings)", false)
class PlayerClientConfigurationEvent(var configuration: ClientConfiguration, var player: Player): Event {
    override val context = elements(player)
}