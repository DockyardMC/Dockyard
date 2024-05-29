package io.github.dockyardmc.player

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundDisconnectPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSystemChatMessagePacket
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.world.World
import io.netty.channel.ChannelHandlerContext
import java.util.UUID

class Player(
    val username: String,
    val uuid: UUID,
    val address: String,
    val crypto: PlayerCrypto,
    val connection: ChannelHandlerContext,
    var brand: String? = null,
    var profile: ProfilePropertyMap? = null,
    var clientConfiguration: ClientConfiguration? = null,
    var location: Location = Location(0, 0, 0),
    var isOnGround: Boolean = false,
    var world: World? = null,
    var isFlying: Boolean = false,
    var entityId: Int? = null,
    var isSneaking: Boolean = false,
    var isSprinting: Boolean = false,
    var selectedHotbarSlot: Int = 0,
    val permissions: MutableList<String> = mutableListOf(),
    var isFullyInitialized: Boolean = false
) {

    // Hold messages client receives before state is PLAY, then send them after state changes to PLAY
    private var queuedMessages = mutableListOf<Pair<Component, Boolean>>()
    fun releaseMessagesQueue() {
        queuedMessages.forEach { sendSystemMessage(it.first, it.second) }
        queuedMessages.clear()
    }

    override fun toString(): String { return username }
    fun kick(reason: String) { this.kick(reason.toComponent()) }
    fun kick(reason: Component) { connection.sendPacket(ClientboundDisconnectPacket(reason)) }
    fun sendMessage(message: String) { this.sendMessage(message.toComponent()) }
    fun sendMessage(component: Component) { sendSystemMessage(component, false) }
    fun sendActionBar(message: String) { this.sendActionBar(message.toComponent()) }
    fun sendActionBar(component: Component) { sendSystemMessage(component, true) }
    private fun sendSystemMessage(component: Component, isActionBar: Boolean) {
        val processor = PlayerManager.playerToProcessorMap[this.uuid]
        processor.let {
            if(processor!!.state != ProtocolState.PLAY) {
                queuedMessages.add(Pair(component, isActionBar))
                return
            }
            connection.sendPacket(ClientboundSystemChatMessagePacket(component, isActionBar))
        }
    }

    fun sendPacket(packet: ClientboundPacket) {
        connection.sendPacket(packet)
    }

    fun hasPermission(permission: String): Boolean {
        if(permission.isEmpty()) return true
        return permissions.contains(permission)
    }
}