package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeVarIntEnum
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundScoreboardObjectivePacket(name: String, mode: ScoreboardMode, value: String?, type: ScoreboardType?): ClientboundPacket() {

    init {
        data.writeString(name)
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