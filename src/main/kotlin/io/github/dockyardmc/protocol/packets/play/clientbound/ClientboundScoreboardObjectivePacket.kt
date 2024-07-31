package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Update Objectives")
@ClientboundPacketInfo(0x5E, ProtocolState.PLAY)
class ClientboundScoreboardObjectivePacket(name: String, mode: ScoreboardMode, value: String?, type: ScoreboardType?): ClientboundPacket() {

    init {
        data.writeUtf(name)
        data.writeByte(mode.ordinal)
        if(mode == ScoreboardMode.CREATE || mode == ScoreboardMode.EDIT_TEXT) {
            if(value == null) throw Exception("value needs to be not null when using CREATE or EDIT_TEXT mode!")
            if(type == null) throw Exception("type needs to be not null when using CREATE or EDIT_TEXT mode!")
            data.writeTextComponent(value)
            data.writeVarIntEnum<ScoreboardType>(type)
            data.writeBoolean(true)
            data.writeVarInt(0)
        }
    }
}


enum class ScoreboardMode {
    CREATE,
    REMOVE,
    EDIT_TEXT
}

enum class ScoreboardType {
    INTEGER,
    HEARTS
}