package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerDamageEntityEvent
import io.github.dockyardmc.events.PlayerInteractAtEntityEvent
import io.github.dockyardmc.events.PlayerInteractWithEntityEvent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.isDoubleInteract
import io.github.dockyardmc.utils.vectors.Vector3f
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Interact")
@ServerboundPacketInfo(22, ProtocolState.PLAY)
class ServerboundEntityInteractPacket(
    val entity: Entity,
    val interactionType: EntityInteractionType,
    val targetX: Float? = null,
    val targetY: Float? = null,
    val targetZ: Float? = null,
    val hand: PlayerHand? = null,
    val sneaking: Boolean
): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player= processor.player

        if(interactionType == EntityInteractionType.ATTACK) {
            val event = PlayerDamageEntityEvent(player, entity)
            Events.dispatch(event)
            if(event.cancelled) return
        }

        if(interactionType == EntityInteractionType.INTERACT) {

            if(isDoubleInteract(player)) return

            val event = PlayerInteractWithEntityEvent(player, entity, hand!!)
            Events.dispatch(event)
            if(event.cancelled) return
        }

        if(interactionType == EntityInteractionType.INTERACT_AT) {

            val event = PlayerInteractAtEntityEvent(player, entity, Vector3f(targetX!!, targetY!!, targetZ!!), hand!!)
            Events.dispatch(event)
            if(event.cancelled) return
        }
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundEntityInteractPacket {

            val entityId = buf.readVarInt()
            val entity = EntityManager.entities.firstOrNull { entity -> entity.id == entityId }
            if(entity == null) throw IllegalStateException("Entity with id $entityId was not found")
            val type = buf.readVarIntEnum<EntityInteractionType>()
            var targetX: Float? = null
            var targetY: Float? = null
            var targetZ: Float? = null

            if(type == EntityInteractionType.INTERACT_AT) {
                targetX = buf.readFloat()
                targetY = buf.readFloat()
                targetZ = buf.readFloat()
            }
            var hand: PlayerHand? = null
            if(type == EntityInteractionType.INTERACT_AT || type == EntityInteractionType.INTERACT) {
                hand = buf.readVarIntEnum<PlayerHand>()
            }
            val sneaking = buf.readBoolean()

            return ServerboundEntityInteractPacket(entity, type, targetX, targetY, targetZ, hand, sneaking)
        }
    }
}

enum class EntityInteractionType {
    INTERACT,
    ATTACK,
    INTERACT_AT
}