package io.github.dockyardmc.extentions

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import java.util.*

fun UUID.asPlayer(): Player? = PlayerManager.players.firstOrNull { it.uuid == this }