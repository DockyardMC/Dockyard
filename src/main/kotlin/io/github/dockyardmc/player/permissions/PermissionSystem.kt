package io.github.dockyardmc.player.permissions

import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.systems.PlayerSystem
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityEvent

class PermissionSystem(val player: Player, val bindable: BindableList<String>): PermissionHolder(), PlayerSystem {

    init {
        bindable.listUpdated {
            buildPermissionCache(bindable.values)
            player.rebuildCommandNodeGraph()

            if(bindable.contains("*")) {
                player.sendPacket(ClientboundEntityEventPacket(player, EntityEvent.PLAYER_SET_OP_PERMISSION_LEVEL_4))
            } else {
                player.sendPacket(ClientboundEntityEventPacket(player, EntityEvent.PLAYER_SET_OP_PERMISSION_LEVEL_0))
            }
        }
    }

    override fun dispose() {}

}