package io.github.dockyardmc.noxesium

import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT

object Noxesium {
    const val IMMOVABLE_TAG = "noxesium:immovable"
    const val BUKKIT_TAG = "PublicBukkitValues"

    val BUKKIT_COMPOUND = NBT.Compound { builder ->
        builder.put(IMMOVABLE_TAG, true)
    }
}