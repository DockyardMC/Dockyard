package io.github.dockyardmc.extentions

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import java.util.*

fun UUID.asPlayer(): Player? = PlayerManager.players.firstOrNull { it.uuid == this }

fun isValidUUID(uuid: String): Boolean {
    if(uuid.length > 36) return false
    val dash1: Int = uuid.indexOf('-')
    val dash2: Int = uuid.indexOf('-', dash1 + 1)
    val dash3: Int = uuid.indexOf('-', dash2 + 1)
    val dash4: Int = uuid.indexOf('-', dash3 + 1)
    val dash5: Int = uuid.indexOf('-', dash4 + 1)
    return !(dash4 < 0 || dash5 >= 0)
}