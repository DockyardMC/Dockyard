package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.EntityMetaValue
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.EntityMetadataType
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
            if (event.newValue) {
                this.metadata[EntityMetadataType.ZOMBIE_RAISED_ARMS] = EntityMetadata(EntityMetadataType.ZOMBIE_RAISED_ARMS, EntityMetaValue.BYTE, 0x0)
            } else {
                this.metadata.remove(EntityMetadataType.ZOMBIE_RAISED_ARMS)
            }
        }
    }
}