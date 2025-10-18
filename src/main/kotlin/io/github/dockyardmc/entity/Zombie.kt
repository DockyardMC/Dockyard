package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerAnimationPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityAnimation
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

open class Zombie(location: Location) : Entity(location) {
    override var type: EntityType = EntityTypes.ZOMBIE
    override val health: Bindable<Float> = bindablePool.provideBindable(20f)
    override var inventorySize: Int = 0

    val raisedArms: Bindable<Boolean> = bindablePool.provideBindable(false)

    fun swingHands() {
        this.sendPacketToViewers(ClientboundPlayerAnimationPacket(this, EntityAnimation.SWING_MAIN_ARM))
    }

    init {
        raisedArms.valueChanged { event ->
            this.metadata[Metadata.LivingEntity.IS_HAND_ACTIVE] = event.newValue
        }
    }
}