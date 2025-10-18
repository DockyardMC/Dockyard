package io.github.dockyardmc.entity.entities

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

open class Sniffer(location: Location): Entity(location) {
    override var type: EntityType = EntityTypes.SNIFFER
    override val health: Bindable<Float> = bindablePool.provideBindable(14f)

    enum class State {
        IDLING,
        FEELING_HAPPY,
        SCENTING,
        SNIFFIING,
        SEARCHING,
        DIGGING,
        RISING
    }
}