package io.github.dockyardmc.events

import io.github.dockyardmc.player.Player
import net.kyori.adventure.nbt.BinaryTag

data class CustomClickActionEvent(val player: Player, val id: String, val payload: BinaryTag?, override val context: Event.Context) : Event