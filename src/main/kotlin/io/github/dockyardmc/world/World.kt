package io.github.dockyardmc.world

import io.github.dockyardmc.extentions.SHA256Long
import io.github.dockyardmc.extentions.SHA256String

class World(var name: String = "world", worldSeed: String = "trans rights!!") {

    var seed = worldSeed.SHA256Long()
    var seedBytes = worldSeed.SHA256String()
    var worldBorder = WorldBorder(this)

}