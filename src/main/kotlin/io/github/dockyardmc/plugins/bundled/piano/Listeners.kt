package io.github.dockyardmc.plugins.bundled.piano

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerRightClickWithItemEvent
import io.github.dockyardmc.item.isSameAs
import io.github.dockyardmc.runnables.runLater

class Listeners(plugin: PianoPlugin) {

    init {

        Events.on<PlayerJoinEvent> {
            runLater(11) {
                it.player.inventory.clear()
                it.player.inventory[4] = plugin.item
            }
        }

        Events.on<PlayerRightClickWithItemEvent> {
            if(it.item.isSameAs(plugin.item)) {
                it.player.sendMessage("<lime>opening piano")
            }
        }
    }
}