package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.EntityMetaValue
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.EntityMetadataType
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

class BlockDisplay(location: Location): DisplayEntity(location) {

    override var type: EntityType = EntityTypes.BLOCK_DISPLAY
    val block: Bindable<io.github.dockyardmc.world.block.Block> = Bindable(Blocks.STONE.toBlock())

    init {
        block.valueChanged {
            val type = EntityMetadataType.BLOCK_DISPLAY_BLOCK
            metadata[type] = EntityMetadata(type, EntityMetaValue.BLOCK_STATE, it.newValue)
        }
    }
}