package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

class Parrot(location: Location) : Entity(location) {
    override var type: EntityType = EntityTypes.PARROT
    override val health: Bindable<Float> = bindablePool.provideBindable(6f)
    override var inventorySize: Int = 0
    val variant: Bindable<Variant> = bindablePool.provideBindable(Variant.entries.random())

    enum class Variant {
        RED_BLUE,
        BLUE,
        GREEN,
        YELLOW_BLUE,
        GRAY;
    }

    init {
        variant.valueChanged { event ->
            metadata[Metadata.Parrot.VARIANT] = event.newValue.ordinal
        }
        variant.triggerUpdate()
    }
}