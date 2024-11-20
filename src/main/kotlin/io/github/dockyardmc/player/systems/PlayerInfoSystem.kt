package io.github.dockyardmc.player.systems

import cz.lukynka.Bindable
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerInfoUpdate
import io.github.dockyardmc.player.SetDisplayNameInfoUpdateAction
import io.github.dockyardmc.player.SetListedInfoUpdateAction
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoUpdatePacket


class PlayerInfoSystem(val player: Player): PlayerSystem {

    fun handle(
        displayName: Bindable<String?>,
        isListed: Bindable<Boolean>,
    ) {
        displayName.valueChanged {
            val packet = ClientboundPlayerInfoUpdatePacket(PlayerInfoUpdate(player.uuid, SetDisplayNameInfoUpdateAction(it.newValue)))
            player.sendPacket(packet)
            player.viewers.sendPacket(packet)
        }

        isListed.valueChanged {
            val update = PlayerInfoUpdate(player.uuid, SetListedInfoUpdateAction(it.newValue))
            val packet = ClientboundPlayerInfoUpdatePacket(update)
            player.sendToViewers(packet)
            player.sendPacket(packet)
        }
    }

    override fun dispose() {
    }
}
