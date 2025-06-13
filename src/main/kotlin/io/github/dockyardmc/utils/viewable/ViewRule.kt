package io.github.dockyardmc.utils.viewable

import io.github.dockyardmc.player.Player

data class ViewRule(private val filter: (Player) -> Boolean) {

    fun passes(player: Player): Boolean {
        return filter.invoke(player)
    }

}