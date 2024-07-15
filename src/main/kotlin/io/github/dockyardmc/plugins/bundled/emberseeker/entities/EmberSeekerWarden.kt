package io.github.dockyardmc.plugins.bundled.emberseeker.entities

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityEvent
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.world.World

class EmberSeekerWarden(override var location: Location, override var world: World): Entity() {
    override var type: EntityType = EntityTypes.WARDEN
    override var health: Bindable<Float> = Bindable(500f)
    override var inventorySize: Int = 0


    fun playAttackAnimation() {
        val packet = ClientboundEntityEventPacket(this, EntityEvent.WARDEN_ATTACK)
        viewers.sendPacket(packet)
    }
}