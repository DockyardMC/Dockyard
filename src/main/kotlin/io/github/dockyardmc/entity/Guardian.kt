package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

open class Guardian(location: Location) : Entity(location) {

    override var type: EntityType = EntityTypes.GUARDIAN
    override val health: Bindable<Float> = bindablePool.provideBindable(30f)
    override var inventorySize: Int = 0

    val isRetractingSpikes: Bindable<Boolean> = bindablePool.provideBindable(false)
    val target: Bindable<Entity?> = bindablePool.provideBindable(null)

    init {
        isRetractingSpikes.valueChanged { event ->
            metadata[Metadata.Guardian.IS_RETRACTING_SPIKES] = event.newValue
        }

        target.valueChanged { event ->
            metadata[Metadata.Guardian.TARGET_EID] = event.newValue?.id ?: 0
        }
    }
}