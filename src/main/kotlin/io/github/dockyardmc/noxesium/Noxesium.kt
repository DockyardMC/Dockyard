package io.github.dockyardmc.noxesium

import io.github.dockyardmc.nbt.nbt

object Noxesium {
    const val IMMOVABLE_TAG = "noxesium:immovable"
    const val BUKKIT_TAG = "PublicBukkitValues"
    const val PACKET_NAMESPACE = "${NoxesiumReferences.NAMESPACE}-v2"

    val BUKKIT_COMPOUND = nbt {
        withBoolean(IMMOVABLE_TAG, true)
    }
}