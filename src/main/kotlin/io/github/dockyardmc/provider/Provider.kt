package io.github.dockyardmc.provider

import io.github.dockyardmc.player.Player

interface Provider {
    val playerGetter: Collection<Player>
}