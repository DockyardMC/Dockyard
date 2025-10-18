package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

class ItemDisplay(location: Location): DisplayEntity(location) {

    override var type: EntityType = EntityTypes.ITEM_DISPLAY
    val item: Bindable<ItemStack> = bindablePool.provideBindable(ItemStack.AIR)
    val renderType: Bindable<RenderType> = bindablePool.provideBindable(RenderType.NONE)

    enum class RenderType {
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

    init {
        item.valueChanged { event ->
            metadata[Metadata.ItemDisplay.DISPLAYED_ITEM] = event.newValue
        }
        renderType.valueChanged { event ->
            metadata[Metadata.ItemDisplay.DISPLAY_TYPE] = event.newValue.ordinal.toByte()
        }
    }
}

