package io.github.dockyardmc.player.systems

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerInfoUpdate
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoUpdatePacket


class PlayerInfoSystem(val player: Player) : PlayerSystem {

    fun handle(
        displayName: Bindable<String?>,
        isListed: Bindable<Boolean>,
    ) {
        displayName.valueChanged {
            val packet = ClientboundPlayerInfoUpdatePacket(mapOf(player.uuid to listOf(PlayerInfoUpdate.UpdateDisplayName(it.newValue))))
            player.sendPacket(packet)
            player.viewers.sendPacket(packet)
        }

        isListed.valueChanged {
            val update = mapOf(player.uuid to listOf(PlayerInfoUpdate.UpdateListed(it.newValue)))
            val packet = ClientboundPlayerInfoUpdatePacket(update)
            player.sendToViewers(packet)
            player.sendPacket(packet)
        }
    }

    override fun dispose() {}
}
