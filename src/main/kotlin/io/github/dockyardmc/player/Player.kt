package io.github.dockyardmc.player

import io.github.dockyardmc.extentions.component
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundDisconnectPacket
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.world.World
import io.netty.channel.ChannelHandlerContext
import java.util.UUID

class Player(
    val username: String,
    val uuid: UUID,
    val address: String,
    val connectionEncryption: PlayerConnectionEncryption,
    val connection: ChannelHandlerContext,
    var brand: String? = null,
    var profile: ProfilePropertyMap? = null,
    var clientConfiguration: ClientConfiguration? = null,
    var location: Location = Location(0, 0, 0),
    var isOnGround: Boolean = false,
    var world: World? = null,
    var isFlying: Boolean = false,
    var isSneaking: Boolean = false,
    var isSprinting: Boolean = false,
) {
    override fun toString(): String {
        return username
    }

    fun kick(reason: String) {
        this.kick(reason.component())
    }

    fun kick(reason: Component) {
        connection.sendPacket(ClientboundDisconnectPacket(reason))
    }
}