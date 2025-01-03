package io.github.dockyardmc.entity

import cz.lukynka.Bindable
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

open class Guardian(location: Location): Entity(location) {

    companion object {
        val RETRACTING_SPIKES_METADATA = EntityMetadataType.GUARDIAN_RETRACTING_SPIKES
        val TARGET_ENTITY_ID = EntityMetadataType.GUARDIAN_TARGET_ENTITY_ID
    }

    override var type: EntityType = EntityTypes.GUARDIAN
    override var health: Bindable<Float> = bindablePool.provideBindable(30f)
    override var inventorySize: Int = 0

    val isRetractingSpikes: Bindable<Boolean> = bindablePool.provideBindable(false)
    val target: Bindable<Entity?> = bindablePool.provideBindable(null)

    init {
        isRetractingSpikes.valueChanged { change ->
            metadata[RETRACTING_SPIKES_METADATA] = EntityMetadata(RETRACTING_SPIKES_METADATA, EntityMetaValue.BOOLEAN, change.newValue)
        }

        target.valueChanged { change ->
            metadata[TARGET_ENTITY_ID] = EntityMetadata(TARGET_ENTITY_ID, EntityMetaValue.VAR_INT, change.newValue?.entityId ?: 0)
        }
    }
}