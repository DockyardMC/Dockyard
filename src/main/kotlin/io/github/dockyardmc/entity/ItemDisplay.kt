package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.EntityMetaValue
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.EntityMetadataType
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.world.World

class ItemDisplay(location: Location, world: World): DisplayEntity(location) {

    override var type: EntityType = EntityTypes.ITEM_DISPLAY
    val item: Bindable<ItemStack> = Bindable(ItemStack.AIR)
    val renderType: Bindable<ItemDisplayRenderType> = Bindable(ItemDisplayRenderType.NONE)

    init {
        item.valueChanged {
            val type = EntityMetadataType.ITEM_DISPLAY_ITEM
            metadata[type] = EntityMetadata(type, EntityMetaValue.ITEM_STACK, item.value)
        }
        renderType.valueChanged {
            val type = EntityMetadataType.ITEM_DISPLAY_RENDER_TYPE
            metadata[type] = EntityMetadata(type, EntityMetaValue.BYTE, renderType.value.ordinal)
        }
    }
}

enum class ItemDisplayRenderType {
    NONE,
    THIRD_PERSON_LEFT_HAND,
    THIRD_PERSON_RIGHT_HAND,
    FIRST_PERSON_LEFT_HAND,
    FIRST_PERSON_RIGHT_HAND,
    HEAD,
    GUI,
    GROUND,
    FIXED
}