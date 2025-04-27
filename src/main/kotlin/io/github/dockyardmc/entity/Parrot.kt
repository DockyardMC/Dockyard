package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundWorldEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.WorldEvent
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.EntityType

class Parrot(location: Location): Entity(location) {
    override var type: EntityType = EntityTypes.PARROT
    override val health: Bindable<Float> = Bindable(6f)
    override var inventorySize: Int = 0
    val variant: Bindable<ParrotVariant> = Bindable(ParrotVariant.entries.random())

    init {
        variant.valueChanged {
            metadata[EntityMetadataType.PARROT_VARIANT] = EntityMetadata(EntityMetadataType.PARROT_VARIANT, EntityMetaValue.VAR_INT, it.newValue.ordinal)
        }
        variant.triggerUpdate()
    }

    fun makeDance() {
        val jukeboxLoc = location.subtract(0, 2, 0)
        jukeboxLoc.world.setBlock(jukeboxLoc, Blocks.JUKEBOX)
        val recordPacket = ClientboundWorldEventPacket(WorldEvent.PLAY_RECORD, jukeboxLoc, Items.MUSIC_DISC_CREATOR.getProtocolId(), false)
        viewers.sendPacket(recordPacket)
    }
}

enum class ParrotVariant {
    RED_BLUE,
    BLUE,
    GREEN,
    YELLOW_BLUE,
    GRAY;
}