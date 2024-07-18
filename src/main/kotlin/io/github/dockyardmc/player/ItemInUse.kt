package io.github.dockyardmc.player

import io.github.dockyardmc.item.ItemStack

data class ItemInUse(
    var item: ItemStack,
    var startTime: Long,
    val time: Long,
)