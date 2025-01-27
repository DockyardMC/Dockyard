package io.github.dockyardmc.protocol.types.item

import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.ItemComponent

abstract class NetworkItemStack(
    val material: Item,
    val amount: Int,
    val components: Set<ItemComponent> = setOf(),
    //TODO attributes
): NetworkWritable {

    

}