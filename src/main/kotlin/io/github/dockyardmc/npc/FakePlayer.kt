package io.github.dockyardmc.npc

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.AddPlayerInfoUpdateAction
import io.github.dockyardmc.player.DisplayedSkinPart
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerInfoUpdate
import io.github.dockyardmc.player.ProfileProperty
import io.github.dockyardmc.player.ProfilePropertyMap
import io.github.dockyardmc.player.SetListedInfoUpdateAction
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityRemovePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoRemovePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoUpdatePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSpawnEntityPacket
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.runnables.AsyncRunnable
import io.github.dockyardmc.utils.MojangUtil
import java.util.UUID

class FakePlayer(location: Location, username: String): Entity(location) {
    override var type: EntityType = EntityTypes.PLAYER
    override var health: Bindable<Float> = Bindable(20f)
    override var inventorySize: Int = 35

    var displayedSkinParts: BindableList<DisplayedSkinPart> = BindableList(DisplayedSkinPart.CAPE, DisplayedSkinPart.JACKET, DisplayedSkinPart.LEFT_PANTS, DisplayedSkinPart.RIGHT_PANTS, DisplayedSkinPart.LEFT_SLEEVE, DisplayedSkinPart.RIGHT_SLEEVE, DisplayedSkinPart.HAT)
    val username: Bindable<String> = Bindable(username)
    val isListed: Bindable<Boolean> = Bindable(false)

    var profile: Bindable<ProfilePropertyMap?> = Bindable(null)

    val mainHandItem: Bindable<ItemStack> = Bindable(ItemStack.air)
    val offHandItem: Bindable<ItemStack> = Bindable(ItemStack.air)
    val helmet: Bindable<ItemStack> = Bindable(ItemStack.air)
    val chestplate: Bindable<ItemStack> = Bindable(ItemStack.air)
    val leggings: Bindable<ItemStack> = Bindable(ItemStack.air)
    val boots: Bindable<ItemStack> = Bindable(ItemStack.air)

    init {
        profile.valueChanged {
            val setListedUpdate = PlayerInfoUpdate(uuid, SetListedInfoUpdateAction(true))
            val addPlayerUpdate = if(it.newValue != null) PlayerInfoUpdate(uuid, AddPlayerInfoUpdateAction(it.newValue!!)) else null

            viewers.forEach {
                it.sendPacket(ClientboundPlayerInfoRemovePacket(uuid))
                it.sendToViewers(ClientboundEntityRemovePacket(this))

                if(addPlayerUpdate != null) it.sendPacket(ClientboundPlayerInfoUpdatePacket(addPlayerUpdate))
                it.sendToViewers(ClientboundSpawnEntityPacket(entityId, uuid, type.getProtocolId(), location, location.yaw, 0, velocity))
                it.sendPacket(ClientboundPlayerInfoUpdatePacket(setListedUpdate))
            }
            displayedSkinParts.triggerUpdate()
        }

        mainHandItem.valueChanged { equipment.value = equipment.value.apply { mainHand = it.newValue } }
        offHandItem.valueChanged { equipment.value = equipment.value.apply { offHand = it.newValue } }
        helmet.valueChanged { equipment.value = equipment.value.apply { helmet = it.newValue } }
        chestplate.valueChanged { equipment.value = equipment.value.apply { chestplate = it.newValue } }
        leggings.valueChanged { equipment.value = equipment.value.apply { leggings = it.newValue } }
        boots.valueChanged { equipment.value = equipment.value.apply { boots = it.newValue } }
    }

    override fun addViewer(player: Player) {
        val infoUpdatePacket = PlayerInfoUpdate(uuid, AddPlayerInfoUpdateAction(ProfilePropertyMap(username.value, mutableListOf())))
        player.sendPacket(ClientboundPlayerInfoUpdatePacket(infoUpdatePacket))
        super.addViewer(player)

        sendMetadataPacket(player)
        sendEquipmentPacket(player)
        sendPotionEffectsPacket(player)

        if(profile.value == null) setSkin(username.value)
    }

    fun setSkin(uuid: UUID) {
        val asyncRunnable = AsyncRunnable {
            val skin = MojangUtil.getSkinFromUUID(uuid)
            profile.value = ProfilePropertyMap("textures", mutableListOf(skin))
        }
        asyncRunnable.run()
    }

    fun setSkin(username: String) {

        var uuid: UUID? = null
        val asyncRunnable = AsyncRunnable {
            uuid = MojangUtil.getUUIDFromUsername(username)
        }
        asyncRunnable.callback = {
            uuid?.let { setSkin(it) }
        }
        asyncRunnable.run()
    }

    override fun removeViewer(player: Player, isDisconnect: Boolean) {
        val playerRemovePacket = ClientboundPlayerInfoRemovePacket(this.uuid)
        player.sendPacket(playerRemovePacket)
        viewers.remove(player)
        super.removeViewer(player, isDisconnect)
    }
}