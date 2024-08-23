package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.*

class BlockDisplay(location: Location): DisplayEntityBase(location) {

    override var type: EntityType = EntityTypes.BLOCK_DISPLAY
    val block: Bindable<Block> = Bindable(Blocks.STONE)

    init {
        block.valueChanged {
            val type = EntityMetadataType.BLOCK_DISPLAY_BLOCK
            metadata[type] = EntityMetadata(type, EntityMetaValue.BLOCK_STATE, it.newValue)
        }
    }
}