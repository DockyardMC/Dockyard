package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.EntityMetaValue
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.EntityMetadataType
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityEvent
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

open class Warden(location: Location): Entity(location) {
    override var type: EntityType = EntityTypes.WARDEN
    override val health: Bindable<Float> = Bindable(500f)
    override var inventorySize: Int = 0

    val angerLevel: Bindable<Int> = Bindable(0)

    init {
        angerLevel.valueChanged {
            metadata[EntityMetadataType.WARDEN_ANGER_LEVEL] = EntityMetadata(EntityMetadataType.WARDEN_ANGER_LEVEL, EntityMetaValue.VAR_INT, it.newValue)
        }
    }

    fun playAnimation(animation: WardenAnimation) {

        when(animation) {
            WardenAnimation.EMERGE -> pose.value = EntityPose.EMERGING
            WardenAnimation.ROAR -> pose.value = EntityPose.ROARING
            WardenAnimation.SNIFF -> pose.value = EntityPose.SNIFFING
            WardenAnimation.DIGGING -> pose.value = EntityPose.DIGGING
            WardenAnimation.ATTACK -> viewers.sendPacket(ClientboundEntityEventPacket(this, EntityEvent.WARDEN_ATTACK))
            WardenAnimation.SONIC_BOOM -> viewers.sendPacket(ClientboundEntityEventPacket(this, EntityEvent.WARDEN_SONIC_BOOM))
            WardenAnimation.TENDRIL_SHAKE -> viewers.sendPacket(ClientboundEntityEventPacket(this, EntityEvent.WARDEN_TENDRIL_SHAKING))
        }
    }
}

enum class WardenAnimation {
    EMERGE,
    ROAR,
    SNIFF,
    DIGGING,
    ATTACK,
    SONIC_BOOM,
    TENDRIL_SHAKE
}