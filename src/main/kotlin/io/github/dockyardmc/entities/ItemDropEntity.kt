package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

class ItemDropEntity(override var location: Location, initialItem: ItemStack) : Entity(location) {

    override var type: EntityType = EntityTypes.ITEM
    override var inventorySize: Int = 0
    override var health: Bindable<Float> = Bindable(9999f)

    val itemStack: Bindable<ItemStack> = Bindable(initialItem)

    init {
        itemStack.valueChanged {
            val type = EntityMetadataType.ITEM_DROP_ITEM_STACK
            metadata[type] = EntityMetadata(type, EntityMetaValue.ITEM_STACK, it.newValue)
        }
        itemStack.triggerUpdate()
    }

}