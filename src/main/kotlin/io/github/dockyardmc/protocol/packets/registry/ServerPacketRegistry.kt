package io.github.dockyardmc.protocol.packets.registry

import io.github.dockyardmc.protocol.packets.configurations.ServerboundClientInformationPacket
import io.github.dockyardmc.protocol.packets.configurations.ServerboundFinishConfigurationAcknowledgePacket
import io.github.dockyardmc.protocol.packets.handshake.ServerboundHandshakePacket
import io.github.dockyardmc.protocol.packets.handshake.ServerboundPingRequestPacket
import io.github.dockyardmc.protocol.packets.handshake.ServerboundStatusRequestPacket
import io.github.dockyardmc.protocol.packets.login.ServerboundEncryptionResponsePacket
import io.github.dockyardmc.protocol.packets.login.ServerboundLoginAcknowledgedPacket
import io.github.dockyardmc.protocol.packets.login.ServerboundLoginStartPacket
import io.github.dockyardmc.protocol.packets.play.serverbound.*

object ServerPacketRegistry: PacketRegistry() {


    override fun load() {
        addHandshake(ServerboundHandshakePacket::class)

        addStatus(ServerboundStatusRequestPacket::class)
        addStatus(ServerboundPingRequestPacket::class)

        addLogin(ServerboundLoginStartPacket::class)
        addLogin(ServerboundEncryptionResponsePacket::class)
        skipLogin("login plugin response")
        addLogin(ServerboundLoginAcknowledgedPacket::class)
        skipLogin("cookie response")

        addConfiguration(ServerboundClientInformationPacket::class)
        skipLogin("cookie response")
        skipLogin("plugin message")
        addConfiguration(ServerboundFinishConfigurationAcknowledgePacket::class)
        skipLogin("keep alive")
        skipLogin("pong")
        skipLogin("resourcepack status")
        skipLogin("known packs")

        addPlay(ServerboundTeleportConfirmationPacket::class)
        skipPlay("query block nbt")
        skipPlay("select bundle item")
        skipPlay("change difficulty")
        skipPlay("chat ack")
        addPlay(ServerboundChatCommandPacket::class)
        skipPlay("signed command")
        skipPlay("signed chat")
        addPlay(ServerboundPlayerChatMessagePacket::class)
        skipPlay("chat session update")
        skipPlay("chunk batch received")
        addPlay(ServerboundClientStatusPacket::class)
        skipPlay("tick end")
        skipPlay("client settings update")
        addPlay(ServerboundCommandSuggestionPacket::class)
        skipPlay("configuration ack")
        skipPlay("click container button")
        addPlay(ServerboundClickContainerPacket::class)
        addPlay(ServerboundCloseContainerPacket::class)
        skipPlay("container slot state")
        skipPlay("cookie response")
        addPlay(ServerboundPlayPluginMessagePacket::class)
        skipPlay("debug sample subscription")
        skipPlay("edit book")
        skipPlay("query entity nbt")
        addPlay(ServerboundEntityInteractPacket::class)
        skipPlay("generate structure")
        addPlay(ServerboundKeepAlivePacket::class)
        skipPlay("lock difficulty")
        addPlay(ServerboundSetPlayerPositionPacket::class)
        addPlay(ServerboundSetPlayerPositionAndRotationPacket::class)
        addPlay(ServerboundSetPlayerRotationPacket::class)
        skipPlay("position status (is on ground)")
        skipPlay("vehicle move")
        skipPlay("steer boat")
        skipPlay("pick item")
        skipPlay("ping request")
        skipPlay("place recipe")
        addPlay(ServerboundPlayerAbilitiesPacket::class)
        skipPlay("player digging")
        skipPlay("entity action")
        skipPlay("client input")
        skipPlay("client pong")
        skipPlay("set recipe book state")
        skipPlay("set recipe book seen")
        skipPlay("name item")
        addPlay(ServerboundResourcepackResponsePacket::class)
        skipPlay("advancement tab")
        skipPlay("select trade")
        skipPlay("set beacon effect")
        addPlay(ServerboundSetPlayerHeldItemPacket::class)
        skipPlay("update command block")
        skipPlay("update command minecart")
        addPlay(ServerboundSetCreativeModeSlotPacket::class)
        skipPlay("jigsaw update")
        skipPlay("structure update")
        skipPlay("update sign")
        skipPlay("client animation")
        skipPlay("client spectate")
        skipPlay("block place")
        addPlay(ServerboundUseItemOnPacket::class)
        addPlay(ServerboundUseItemPacket::class)
    }
}