package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.types.ClientSettings

@EventDocumentation("server receives information about client's settings", false)
class PlayerClientSettingsEvent(var clientSettings: ClientSettings, var player: Player, override val context: Event.Context) : Event