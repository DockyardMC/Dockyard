package io.github.dockyardmc.entity

import cz.lukynka.Bindable
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityEvent
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

abstract class Ravager(location: Location) : Entity(location) {

    override var type: EntityType = EntityTypes.RAVAGER
    override var health: Bindable<Float> = bindablePool.provideBindable(100f)
    override var inventorySize: Int = 0

    fun playAttackAnimation() {
        viewers.sendPacket(ClientboundEntityEventPacket(this, EntityEvent.RAVAGER_ATTACK_ANIMATION))
    }

}