package io.github.dockyardmc.events

import io.github.dockyardmc.player.ClientConfiguration
import io.github.dockyardmc.player.Player

class PlayerClientConfigurationEvent(var configuration: ClientConfiguration, var player: Player): Event