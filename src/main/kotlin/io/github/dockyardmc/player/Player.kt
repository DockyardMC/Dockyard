package io.github.dockyardmc.player

import io.netty.channel.ChannelHandlerContext
import java.util.UUID

class Player(
    val username: String,
    val uuid: UUID,
    val address: String,
    val connectionEncryption: PlayerConnectionEncryption,
    val connection: ChannelHandlerContext,
    var brand: String? = null,
    var clientConfiguration: ClientConfiguration? = null
) {
    override fun toString(): String {
        return username
    }
}