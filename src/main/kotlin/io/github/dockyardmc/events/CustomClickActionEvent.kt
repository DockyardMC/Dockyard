package io.github.dockyardmc.events

import io.github.dockyardmc.player.Player
import net.kyori.adventure.nbt.CompoundBinaryTag

class CustomClickActionEvent(val player: Player, val id: String, val payload: CompoundBinaryTag?, override val context: Event.Context) : Event