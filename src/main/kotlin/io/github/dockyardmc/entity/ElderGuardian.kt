package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

class ElderGuardian(location: Location): Guardian(location) {

    override var health: Bindable<Float> = bindablePool.provideBindable(80f)
    override var type: EntityType = EntityTypes.ELDER_GUARDIAN
}