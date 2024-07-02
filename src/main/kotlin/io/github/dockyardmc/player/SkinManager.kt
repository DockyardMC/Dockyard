package io.github.dockyardmc.player

import cz.lukynka.prettylog.log
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.runnables.AsyncRunnable
import io.github.dockyardmc.utils.MojangUtil
import java.util.*

object SkinManager {

    val skinCache = mutableMapOf<UUID, ProfileProperty>()

    // Get UUID of username first
    fun setSkinOf(player: Player, username: String) {

        var uuid: UUID? = null
        val asyncRunnable = AsyncRunnable {
            uuid = MojangUtil.getUUIDFromUsername(username)
        }
        asyncRunnable.callback = {
            uuid?.let { setSkinOf(player, it) }
        }
        asyncRunnable.execute()
    }

    fun setSkinOf(player: Player, uuid: UUID) {
        val asyncRunnable = AsyncRunnable {
            val skin = MojangUtil.getSkinFromUUID(uuid)
            player.profile!!.properties[0] = skin
            log(skin.toString())
        }
        asyncRunnable.callback = {
            player.sendPacket(ClientboundPlayerInfoRemovePacket(player))
            player.sendPacket(ClientboundRespawnPacket(player, ClientboundRespawnPacket.RespawnDataKept.KEEP_ALL))
            val addPlayerUpdate = PlayerInfoUpdate(player.uuid, AddPlayerInfoUpdateAction(player.profile!!))
            val setListedUpdate = PlayerInfoUpdate(player.uuid, SetListedInfoUpdateAction(true))
            player.sendPacket(ClientboundPlayerInfoUpdatePacket(addPlayerUpdate))
            player.sendPacket(ClientboundPlayerInfoUpdatePacket(setListedUpdate))

            player.sendToViewers(ClientboundPlayerInfoRemovePacket(player))
            player.sendToViewers(ClientboundEntityRemovePacket(player))
            player.sendToViewers(ClientboundPlayerInfoUpdatePacket(addPlayerUpdate))
            player.sendToViewers(ClientboundPlayerInfoUpdatePacket(setListedUpdate))
            player.sendToViewers(ClientboundPlayerInfoUpdatePacket(PlayerInfoUpdate(player.uuid, AddPlayerInfoUpdateAction(player.profile!!))))
            player.sendToViewers(ClientboundSpawnEntityPacket(player.entityId, player.uuid, player.type.id, player.location, player.location.yaw, 0, player.velocity))
            player.displayedSkinParts.triggerUpdate()

            //TODO Retain effects on respawn
            val packet = ClientboundEntityEffectPacket(player, 15, 1, 99999, 0x00)
            player.sendPacket(packet)
        }
        asyncRunnable.execute()
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