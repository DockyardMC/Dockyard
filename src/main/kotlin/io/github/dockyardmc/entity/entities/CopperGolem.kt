package io.github.dockyardmc.entity.entities

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

// clanker
open class CopperGolem(location: Location): Entity(location) {
    override var type: EntityType = EntityTypes.COPPER_GOLEM
    override val health: Bindable<Float> = bindablePool.provideBindable(14f)

    enum class WeatherState {
        UNAFFECTED,
        EXPOSED,
        WEATHERED,
        OXIDIZED
    }

    enum class State {
        IDLE,
        GETTING_ITEM,
        GETTING_NO_ITEM,
        DROPPING_ITEM,
        DROPPING_NO_ITEM
    }
}