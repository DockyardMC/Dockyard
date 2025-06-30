package io.github.dockyardmc.npc

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerInfoUpdate
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoUpdatePacket
import io.github.dockyardmc.protocol.types.GameProfile
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

class FakePlayer(location: Location) : Entity(location) {
    override var type: EntityType = EntityTypes.PLAYER
    override val health: Bindable<Float> = bindablePool.provideBindable(20f)
    override var inventorySize: Int = 35

    private val gameProfile = GameProfile(uuid, uuid.toString().substring(0, 16))
    val isListed: Bindable<Boolean> = bindablePool.provideBindable(false)

    init {
        viewDistanceBlocks = 10
    }

    override fun addViewer(player: Player): Boolean {

        val updates = mutableListOf(
            PlayerInfoUpdate.AddPlayer(gameProfile),
            PlayerInfoUpdate.UpdateListed(isListed.value),
            PlayerInfoUpdate.UpdateDisplayName(customName.value),
        )

        player.sendPacket(ClientboundPlayerInfoUpdatePacket(mapOf(uuid to updates)))
        if (!super.addViewer(player)) return false

//        player.sendMetadataPacket(this)
//        this.displayedSkinParts.triggerUpdate()
        sendMetadataPacket(player)
        sendEquipmentPacket(player)
        return true
    }
}