package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.world.World

class BlockDisplay(location: Location, world: World): DisplayEntityBase(location, world) {

    override var type: EntityType = EntityTypes.BLOCK_DISPLAY
    val block: Bindable<Block> = Bindable(Blocks.STONE)

    init {
        block.valueChanged {
            val type = EntityMetadataType.BLOCK_DISPLAY_BLOCK
            metadata[type] = EntityMetadata(type, EntityMetaValue.BLOCK_STATE, it.newValue)
        }
    }
}