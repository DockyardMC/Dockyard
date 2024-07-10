package io.github.dockyardmc.plugins.bundled.piano

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.registry.Items

class PianoPlugin: DockyardPlugin {
    override val name: String = "Piano"
    override val author: String = "LukynkaCZE"
    override val version: String = "0.1"

    val item = ItemStack(Items.POPPED_CHORUS_FRUIT)

    override fun load(server: DockyardServer) {

        item.displayName.value = "<aqua><u>The Very Cool Piano Item"
        item.lore.add("")
        item.lore.add("<gray>yeee boi")
        item.lore.add("")
        item.lore.add("<yellow><b>Right-Click to Use")
        item.lore.add("")

        item.customModelData.value = 1

        Listeners(this)
    }

    override fun unload(server: DockyardServer) {

    }
}