package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writePosition
import io.github.dockyardmc.extentions.writeStringArray
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPlayPacket(
    entityId: Int,
    isHardcore: Boolean,
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
        //TODO: Figure out why this supposed to be int, but failing
        data.writeInt(entityId)
        data.writeBoolean(isHardcore)
        data.writeVarInt(dimensionNames.size)
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

        data.writeBoolean(true) // has death location
        data.writeUtf("minecraft:world")
        data.writePosition(Location(0, 0, 0))

        data.writeVarInt(portalCooldown)
    }
}