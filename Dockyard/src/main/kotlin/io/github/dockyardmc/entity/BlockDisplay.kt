package io.github.dockyardmc.entity

import cz.lukynka.Bindable
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.registry.registries.EntityType

class BlockDisplay(location: Location): DisplayEntityBase(location) {

    override var type: EntityType = EntityTypes.BLOCK_DISPLAY
    val block: Bindable<Block> = Bindable(Blocks.STONE.toBlock())

    init {
        block.valueChanged {
            val type = EntityMetadataType.BLOCK_DISPLAY_BLOCK
            metadata[type] = EntityMetadata(type, EntityMetaValue.BLOCK_STATE, it.newValue)
        }
    }
}