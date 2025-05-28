package io.github.dockyardmc.events

import io.github.dockyardmc.player.Player
import org.jglrxavpok.hephaistos.nbt.NBT

class CustomClickActionEvent(val player: Player, val id: String, val payload: NBT?) : Event {
    override val context: Event.Context = Event.Context(players = setOf(player))
}