package io.github.dockyardmc.player

import io.github.dockyardmc.location.Location
import io.netty.channel.ChannelHandlerContext
import java.util.UUID

class Player(
    val username: String,
    val uuid: UUID,
    val address: String,
    val connectionEncryption: PlayerConnectionEncryption,
    val connection: ChannelHandlerContext,
    var brand: String? = null,
    var clientConfiguration: ClientConfiguration? = null,
    var location: Location = Location(0, 0, 0),
    var isOnGround: Boolean = false
) {
    override fun toString(): String {
        return username
    }
}