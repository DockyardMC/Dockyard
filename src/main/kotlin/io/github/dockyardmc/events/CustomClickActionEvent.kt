package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import net.kyori.adventure.nbt.BinaryTag

@EventDocumentation("when custom click action is received from client")
data class CustomClickActionEvent(val player: Player, val id: String, val payload: BinaryTag?, override val context: Event.Context) : Event