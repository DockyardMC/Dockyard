package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

class ItemDropEntity(override var location: Location, initialItem: ItemStack) : Entity(location) {

    override var type: EntityType = EntityTypes.ITEM
    override var inventorySize: Int = 0
    override val health: Bindable<Float> = bindablePool.provideBindable(9999f)

    val itemStack: Bindable<ItemStack> = bindablePool.provideBindable(initialItem)
    var canBePickedUp: Boolean = false
    var canBePickedUpAfter: Int = 20
    var pickupDistance: Int = 1
    var pickupAnimation: Boolean = true

    private var lifetime: Int = 0

    init {
        itemStack.valueChanged { event ->
            metadata[Metadata.ItemEntity.ITEM] = event.newValue
        }
        itemStack.triggerUpdate()
        if (canBePickedUpAfter == 0 || canBePickedUpAfter == -1) canBePickedUp = true
        hasNoGravity.value = true
    }

    override fun tick() {
        if (!canBePickedUp) {
            lifetime++
            if (lifetime == canBePickedUpAfter) {
                canBePickedUp = true
            }
        }
    }

    override fun dispose() {
        itemStack.dispose()
        super.dispose()
    }
}