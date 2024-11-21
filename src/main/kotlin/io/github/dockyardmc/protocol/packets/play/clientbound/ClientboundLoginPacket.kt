package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeStringArray
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundLoginPacket(
    entityId: Int,
    isHardcore: Boolean,
    dimensionNames: Collection<String>,
    maxPlayers: Int,
    viewDistance: Int,
    simulationDistance: Int,
    reducedDebugInfo: Boolean,
    enableRespawnScreen: Boolean,
    doLimitedCrafting: Boolean,
    dimensionType: Int,
    dimensionName: String,
    hashedSeed: Long,
    gameMode: GameMode,
    previousGameMode: GameMode,
    isDebug: Boolean,
    isFlat: Boolean,
    portalCooldown: Int,
    seaLevel: Int,
    enforcesSecureChat: Boolean
): ClientboundPacket() {

    init {
        data.writeInt(entityId)
        data.writeBoolean(isHardcore)
        data.writeStringArray(dimensionNames)
        data.writeVarInt(maxPlayers)
        data.writeVarInt(viewDistance)
        data.writeVarInt(simulationDistance)
        data.writeBoolean(reducedDebugInfo)
        data.writeBoolean(enableRespawnScreen)
        data.writeBoolean(doLimitedCrafting)
        data.writeVarInt(dimensionType)
        data.writeString(dimensionName)
        data.writeLong(hashedSeed)
        data.writeByte(gameMode.ordinal)
        data.writeByte(previousGameMode.ordinal)
        data.writeBoolean(isDebug)
        data.writeBoolean(isFlat)

        data.writeBoolean(false) // has death location
//        data.writeUtf("minecraft:world") // death dimension
//        data.writePosition(Location(0, 0, 0)) // death location

        data.writeVarInt(portalCooldown)
        data.writeVarInt(seaLevel)
        data.writeBoolean(enforcesSecureChat)
    }
}