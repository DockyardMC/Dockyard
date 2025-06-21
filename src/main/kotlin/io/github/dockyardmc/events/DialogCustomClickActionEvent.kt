package io.github.dockyardmc.events

import io.github.dockyardmc.player.Player
import net.kyori.adventure.nbt.CompoundBinaryTag

data class DialogCustomClickActionEvent(val player: Player, val id: String, val payload: CompoundBinaryTag, override val context: Event.Context) : Event