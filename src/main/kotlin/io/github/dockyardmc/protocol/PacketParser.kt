package io.github.dockyardmc.protocol

import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.packets.configurations.ServerboundClientInformationPacket
import io.github.dockyardmc.protocol.packets.configurations.ServerboundFinishConfigurationAcknowledgePacket
import io.github.dockyardmc.protocol.packets.configurations.ServerboundPluginMessagePacket
import io.github.dockyardmc.protocol.packets.login.ServerboundEncryptionResponsePacket
import io.github.dockyardmc.protocol.packets.login.ServerboundLoginStartPacket
import io.github.dockyardmc.protocol.packets.handshake.ServerboundHandshakePacket
import io.github.dockyardmc.protocol.packets.handshake.ServerboundPingRequestPacket
import io.github.dockyardmc.protocol.packets.handshake.ServerboundStatusRequestPacket
import io.github.dockyardmc.protocol.packets.login.ServerboundLoginAcknowledgedPacket
import io.github.dockyardmc.protocol.packets.play.serverbound.*
import io.netty.buffer.ByteBuf

object PacketParser {
    fun parsePacket(id: Int, buffer: ByteBuf, processor: PacketProcessor, size: Int): ServerboundPacket? {

        var outPacket: ServerboundPacket? = null

        if(processor.state == ProtocolState.HANDSHAKE) {
            outPacket = when (id) {
                0 -> ServerboundHandshakePacket.read(buffer)
                else -> null
            }
        }

        if(processor.state == ProtocolState.STATUS) {
            outPacket = when (id) {
                0 -> ServerboundStatusRequestPacket.read(buffer)
                1 -> ServerboundPingRequestPacket.read(buffer)
                else -> null
            }
        }

        if(processor.state == ProtocolState.LOGIN) {
            outPacket = when(id) {
                0 -> ServerboundLoginStartPacket.read(buffer)
                1 -> ServerboundEncryptionResponsePacket.read(buffer)
                3 -> ServerboundLoginAcknowledgedPacket()
                else -> null
            }
        }

        if(processor.state == ProtocolState.CONFIGURATION) {
            outPacket = when(id) {
                0 -> ServerboundClientInformationPacket.read(buffer)
                1 -> ServerboundPluginMessagePacket.read(buffer, size)
                2 -> ServerboundFinishConfigurationAcknowledgePacket()
                else -> null
            }
        }

        if(processor.state == ProtocolState.PLAY) {
            outPacket = when(id) {
                0 -> ServerboundTeleportConfirmationPacket.read(buffer)
                4 -> ServerboundChatCommandPacket.read(buffer)
                5 -> ServerboundPlayerChatMessagePacket.read(buffer)
                6 -> ServerboundPlayerSessionPacket.read(buffer)
                13 -> ServerboundClickContainerPacket.read(buffer)
                14 -> ServerboundContainerClosePacket.read(buffer)
                16 -> ServerboundPlayPluginMessagePacket.read(buffer, size)
                21 -> ServerboundKeepAlivePacket.read(buffer)
                23 -> ServerboundSetPlayerPositionPacket.read(buffer)
                24 -> ServerboundSetPlayerPositionAndRotationPacket.read(buffer)
                25 -> ServerboundSetPlayerRotationPacket.read(buffer)
                32 -> ServerboundPlayerAbilitiesPacket.read(buffer)
                33 -> ServerboundPlayerActionPacket.read(buffer)
                34 -> ServerboundPlayerCommandPacket.read(buffer)
                44 -> ServerboundSetPlayerHeldItemPacket.read(buffer)
                47 -> ServerboundSetCreativeModeSlotPacket.read(buffer)
                51 -> ServerboundPlayerSwingHandPacket.read(buffer)
                53 -> ServerboundUseItemOnPacket.read(buffer)
                else -> null
            }
        }

//        log("Returning $outPacket for id $id")
        return outPacket
    }
}