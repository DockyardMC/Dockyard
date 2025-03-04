package io.github.dockyardmc.player

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityRemovePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoRemovePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoUpdatePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSpawnEntityPacket
import io.github.dockyardmc.utils.MojangUtil
import java.util.*

object SkinManager {

    val skinCache = mutableMapOf<UUID, ProfileProperty>()

    // Get UUID of username first
    fun setSkinOf(player: Player, username: String) {

        var uuid: UUID? = null
        val asyncRunnable = DockyardServer.scheduler.runAsync {
            uuid = MojangUtil.getUUIDFromUsername(username)
        }
        asyncRunnable.thenAccept {
            uuid?.let { setSkinOf(player, it) }
        }
    }

    @Synchronized
    fun setSkinOf(player: Player, uuid: UUID) {
        val asyncRunnable = DockyardServer.scheduler.runAsync {
            val skin = MojangUtil.getSkinFromUUID(uuid)
            player.profile!!.properties[0] = skin
        }
        asyncRunnable.thenAccept {
            val location = player.location

            player.respawn(false)
            player.sendPacket(ClientboundPlayerInfoRemovePacket(player))
            val addPlayerUpdate = PlayerInfoUpdate(player.uuid, AddPlayerInfoUpdateAction(player.profile!!))
            val setListedUpdate = PlayerInfoUpdate(player.uuid, SetListedInfoUpdateAction(true))
            player.sendPacket(ClientboundPlayerInfoUpdatePacket(addPlayerUpdate))
            player.sendPacket(ClientboundPlayerInfoUpdatePacket(setListedUpdate))

            player.sendToViewers(ClientboundPlayerInfoRemovePacket(player))
            player.sendToViewers(ClientboundEntityRemovePacket(player))
            player.sendToViewers(ClientboundPlayerInfoUpdatePacket(addPlayerUpdate))
            player.sendToViewers(ClientboundPlayerInfoUpdatePacket(setListedUpdate))
            player.sendToViewers(ClientboundPlayerInfoUpdatePacket(PlayerInfoUpdate(player.uuid, AddPlayerInfoUpdateAction(player.profile!!))))
            player.sendToViewers(ClientboundSpawnEntityPacket(player.id, player.uuid, player.type.getProtocolId(), player.location, player.location.yaw, 0, player.velocity))
            player.displayedSkinParts.triggerUpdate()
            player.teleport(location)
        }
    }
}

fun Player.setSkin(uuid: UUID) {
    SkinManager.setSkinOf(this, uuid)
}

fun Player.setSkin(username: String) {
    SkinManager.setSkinOf(this, username)
}

fun Player.updateSkin() {
    SkinManager.skinCache.remove(this.uuid)
    setSkin(this.uuid)
}