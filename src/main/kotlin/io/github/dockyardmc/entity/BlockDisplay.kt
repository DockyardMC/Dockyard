package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.world.block.Block

class BlockDisplay(location: Location) : DisplayEntity(location) {

    override var type: EntityType = EntityTypes.BLOCK_DISPLAY
    val block: Bindable<Block> = bindablePool.provideBindable(Blocks.STONE.toBlock())

    init {
        block.valueChanged { event ->
            metadata[Metadata.BlockDisplay.DISPLAYED_BLOCK_STATE] = event.newValue
        }
    }
}