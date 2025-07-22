package io.github.dockyardmc.events.noxesium

import com.noxcrew.noxesium.api.protocol.ClientSettings
import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.player.Player

@EventDocumentation("when server receives client settings packet from noxesium")
data class NoxesiumClientSettingsEvent(val player: Player, val clientSettings: ClientSettings, override val context: Event.Context) : Event