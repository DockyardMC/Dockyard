package io.github.dockyardmc.extentions

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.world.World


fun World.sendMessage(message: String) {
    this.sendMessage(message.toComponent())
}

fun World.sendMessage(component: Component) {
    players.sendMessage(component)
}

fun World.sendActionBar(message: String) {
    this.sendActionBar(message.toComponent())
}

fun World.sendActionBar(component: Component) {
    players.sendActionBar(component)
}