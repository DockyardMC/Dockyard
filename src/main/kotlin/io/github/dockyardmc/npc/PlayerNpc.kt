package io.github.dockyardmc.npc

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.entity.metadata.EntityMetaValue
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.EntityMetadataType
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.utils.MojangUtil
import java.util.*

class PlayerNpc(location: Location, username: String) : NpcEntity(location) {
    override var type: EntityType = EntityTypes.PLAYER
    override val health: Bindable<Float> = Bindable(20f)
    override var inventorySize: Int = 0

    val displayedSkinParts: BindableList<DisplayedSkinPart> = BindableList(
        DisplayedSkinPart.CAPE,
        DisplayedSkinPart.JACKET,
        DisplayedSkinPart.LEFT_PANTS,
        DisplayedSkinPart.RIGHT_PANTS,
        DisplayedSkinPart.LEFT_SLEEVE,
        DisplayedSkinPart.RIGHT_SLEEVE,
        DisplayedSkinPart.HAT
    )
    val username: Bindable<String> = Bindable(username)
    val isListed: Bindable<Boolean> = Bindable(false)

    val profile: Bindable<ProfilePropertyMap?> = Bindable(null)

    init {

        isListed.valueChanged {
            val setListedUpdate = PlayerInfoUpdate(uuid, SetListedInfoUpdateAction(isListed.value))
            viewers.sendPacket(ClientboundPlayerInfoUpdatePacket(setListedUpdate))
        }

        displayedSkinParts.listUpdated {
            metadata[EntityMetadataType.PLAYER_DISPLAY_SKIN_PARTS] = EntityMetadata(
                EntityMetadataType.PLAYER_DISPLAY_SKIN_PARTS,
                EntityMetaValue.BYTE,
                displayedSkinParts.values.getBitMask()
            )
            sendMetadataPacketToViewers()
        }

        profile.valueChanged {
            val setListedUpdate = PlayerInfoUpdate(uuid, SetListedInfoUpdateAction(isListed.value))
            val addPlayerUpdate =
                if (it.newValue != null) PlayerInfoUpdate(uuid, AddPlayerInfoUpdateAction(it.newValue!!)) else null

            viewers.sendPacket(ClientboundEntityRemovePacket(this))
            viewers.sendPacket(ClientboundPlayerInfoRemovePacket(uuid))
            if (addPlayerUpdate != null) viewers.sendPacket(ClientboundPlayerInfoUpdatePacket(addPlayerUpdate))

            viewers.sendPacket(
                ClientboundSpawnEntityPacket(id, uuid, type.getProtocolId(), location, location.yaw, 0, velocity)
            )
            viewers.sendPacket(ClientboundPlayerInfoUpdatePacket(setListedUpdate))

            displayedSkinParts.triggerUpdate()
            equipment.triggerUpdate()
        }

        team.value = npcTeam
    }

    fun swingHand() {
        val packet = ClientboundPlayerAnimationPacket(this, EntityAnimation.SWING_MAIN_ARM)
        viewers.sendPacket(packet)
    }

    override fun addViewer(player: Player): Boolean {
        if (!super.addViewer(player)) return false

        val profileMap = if (profile.value == null) ProfilePropertyMap(username.value, mutableListOf()) else profile.value!!
        val infoUpdatePacket = PlayerInfoUpdate(uuid, AddPlayerInfoUpdateAction(profileMap))
        val listedPacket = PlayerInfoUpdate(uuid, SetListedInfoUpdateAction(isListed.value))
        player.sendPacket(ClientboundPlayerInfoUpdatePacket(infoUpdatePacket))
        player.sendPacket(ClientboundPlayerInfoUpdatePacket(listedPacket))


        sendMetadataPacket(player)
        sendEquipmentPacket(player)
        sendPotionEffectsPacket(player)

        if (profile.value == null) setSkin(username.value)
        return true
    }

    override fun removeViewer(player: Player) {
        val playerRemovePacket = ClientboundPlayerInfoRemovePacket(this.uuid)
        player.sendPacket(playerRemovePacket)
        super.removeViewer(player)
    }

    fun setSkin(uuid: UUID) {
        world.scheduler.runAsync {
            val skin = MojangUtil.getSkinFromUUID(uuid)
            profile.value = ProfilePropertyMap(username.value, mutableListOf(skin))
        }
    }

    fun setSkin(username: String) {
        var uuid: UUID? = null
        val asyncRunnable = world.scheduler.runAsync {
            uuid = MojangUtil.getUUIDFromUsername(username)
        }
        asyncRunnable.thenAccept {
            uuid?.let { setSkin(it) }
        }
    }
}