package io.github.dockyardmc.player.systems

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.extentions.properStrictCase
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerInfoUpdate
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundGameEventPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoUpdatePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.GameEvent


class GameModeSystem(val player: Player): PlayerSystem {

    fun handle(bindable: Bindable<GameMode>) {
        bindable.valueChanged {
            player.sendPacket(ClientboundGameEventPacket(GameEvent.CHANGE_GAME_MODE, it.newValue.ordinal.toFloat()))
            when (it.newValue) {
                GameMode.SPECTATOR -> {
                    player.canFly.value = true
                    player.isFlying.value = true
                    player.isInvulnerable = true
                }
                GameMode.CREATIVE -> {
                    player.canFly.value = true
                    player.isFlying.value = player.isFlying.value
                    player.isInvulnerable = true
                }

                GameMode.ADVENTURE,
                GameMode.SURVIVAL -> {
                    if (it.oldValue == GameMode.CREATIVE || it.oldValue == GameMode.SPECTATOR) {
                        player.canFly.value = false
                        player.isFlying.value = false
                        player.isInvulnerable = false
                    }
                }
            }

            player.isInvisible.value = it.newValue == GameMode.SPECTATOR
            player.refreshAbilities()
            val updatePacket = ClientboundPlayerInfoUpdatePacket(mapOf(player.uuid to listOf(PlayerInfoUpdate.UpdateGameMode(it.newValue))))
            player.sendPacket(updatePacket)
            player.sendToViewers(updatePacket)
        }
    }

}

enum class GameMode {
    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SPECTATOR;

    override fun toString(): String = this.name.properStrictCase()
}