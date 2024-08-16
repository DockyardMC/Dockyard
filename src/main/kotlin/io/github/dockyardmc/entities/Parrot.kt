package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundWorldEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.WorldEvent
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.world.World

class Parrot(override var world: World, override var location: Location): Entity() {
    override var type: EntityType = EntityTypes.PARROT
    override var health: Bindable<Float> = Bindable(6f)
    override var inventorySize: Int = 0
    var variant: Bindable<ParrotVariant> = Bindable(ParrotVariant.entries.random())

    init {
        variant.valueChanged {
            metadata[EntityMetadataType.PARROT_VARIANT] = EntityMetadata(EntityMetadataType.PARROT_VARIANT, EntityMetadataByteBufWriter.VAR_INT, it.newValue.ordinal)
        }
        variant.triggerUpdate()
    }

    fun makeDance() {
        val jukeboxLoc = location.subtract(0, 2, 0)
        jukeboxLoc.world.setBlock(jukeboxLoc, Blocks.JUKEBOX)
        val recordPacket = ClientboundWorldEventPacket(WorldEvent.PLAY_RECORD, jukeboxLoc, Items.MUSIC_DISC_CREATOR.id, false)
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