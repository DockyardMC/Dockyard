package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityEvent
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

open class Warden(location: Location) : Entity(location) {
    override var type: EntityType = EntityTypes.WARDEN
    override val health: Bindable<Float> = bindablePool.provideBindable(500f)
    override var inventorySize: Int = 0

    val angerLevel: Bindable<Int> = bindablePool.provideBindable(0)

    enum class Animation {
        EMERGE,
        ROAR,
        SNIFF,
        DIGGING,
        ATTACK,
        SONIC_BOOM,
        TENDRIL_SHAKE
    }

    init {
        angerLevel.valueChanged { event ->
            metadata[Metadata.Warden.ANGER_LEVEL] = event.newValue
        }
    }

    fun playAnimation(animation: Animation) {

        when (animation) {
            Animation.EMERGE -> pose.value = EntityPose.EMERGING
            Animation.ROAR -> pose.value = EntityPose.ROARING
            Animation.SNIFF -> pose.value = EntityPose.SNIFFING
            Animation.DIGGING -> pose.value = EntityPose.DIGGING
            Animation.ATTACK -> viewers.sendPacket(ClientboundEntityEventPacket(this, EntityEvent.WARDEN_ATTACK))
            Animation.SONIC_BOOM -> viewers.sendPacket(ClientboundEntityEventPacket(this, EntityEvent.WARDEN_SONIC_BOOM))
            Animation.TENDRIL_SHAKE -> viewers.sendPacket(ClientboundEntityEventPacket(this, EntityEvent.WARDEN_TENDRIL_SHAKING))
        }
    }
}

