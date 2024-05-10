package io.github.dockyardmc.protocol.packets.play

import io.github.dockyardmc.extentions.writeStringArray
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPlayPacket(
    entityId: Int,
    isHardcore: Boolean,
    dimensionCount: Int,
    dimensionNames: MutableList<String>,
    maxPlayers: Int,
    viewDistance: Int,
    simulationDistance: Int,
    reducedDebugInfo: Boolean,
    enableRespawnScreen: Boolean,
    doLimitedCrafting: Boolean,
    dimensionType: String,
    dimensionName: String,
    hashedSeed: Long,
    gameMode: GameMode,
    previousGameMode: GameMode,
    isDebug: Boolean,
    isFlat: Boolean,
    portalCooldown: Int,
): ClientboundPacket(41) {

    init {
        data.writeVarInt(entityId)
        data.writeBoolean(isHardcore)
        data.writeVarInt(dimensionCount)
        data.writeStringArray(dimensionNames)
        data.writeVarInt(maxPlayers)
        data.writeVarInt(viewDistance)
        data.writeVarInt(simulationDistance)
        data.writeBoolean(reducedDebugInfo)
        data.writeBoolean(enableRespawnScreen)
        data.writeBoolean(doLimitedCrafting)
        data.writeUtf(dimensionType)
        data.writeUtf(dimensionName)
        data.writeLong(hashedSeed)
        data.writeByte(gameMode.ordinal)
        data.writeByte(previousGameMode.ordinal)
        data.writeBoolean(isDebug)
        data.writeBoolean(isFlat)
        data.writeBoolean(false) // has death location
        data.writeVarInt(portalCooldown)
    }
}