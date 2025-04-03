package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundScoreboardObjectivePacket(name: String, mode: ScoreboardMode, value: String?, type: ScoreboardType?): ClientboundPacket() {

    init {
        buffer.writeString(name)
        buffer.writeByte(mode.ordinal)
        if(mode == ScoreboardMode.CREATE || mode == ScoreboardMode.EDIT_TEXT) {
            if(value == null) throw Exception("value needs to be not null when using CREATE or EDIT_TEXT mode!")
            if(type == null) throw Exception("type needs to be not null when using CREATE or EDIT_TEXT mode!")
            buffer.writeTextComponent(value)
            buffer.writeEnum<ScoreboardType>(type)
            buffer.writeBoolean(true)
            buffer.writeVarInt(0)
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