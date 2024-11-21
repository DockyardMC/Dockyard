package io.github.dockyardmc.world

import cz.lukynka.prettylog.log
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerEnterChunkEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetCenterChunkPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUnloadChunkPacket
import io.github.dockyardmc.utils.ChunkUtils
import io.github.dockyardmc.utils.chunkInSpiral
import io.github.dockyardmc.utils.getPlayerEventContext

class PlayerChunkEngine(val player: Player) {

    companion object {
        const val DEFAULT_RENDER_DISTANCE = 10
    }

    private var previousChunkPos = ChunkPos.ZERO

    fun update() {
        val world = player.world

        world.scheduler.runAsync {

            val currentChunkPos = player.getCurrentChunkPos()
            val currentChunkIndex = currentChunkPos.pack()
            val (currentChunkX, currentChunkZ) = ChunkPos.unpack(currentChunkIndex)

            if (previousChunkPos == currentChunkPos) return@runAsync

            val oldCenter = previousChunkPos
            previousChunkPos = currentChunkPos

            val distance = DEFAULT_RENDER_DISTANCE

            Events.dispatch(
                PlayerEnterChunkEvent(
                    previousChunkPos,
                    currentChunkPos,
                    player,
                    getPlayerEventContext(player)
                )
            )

            player.sendActionBar("${currentChunkPos.x}, ${currentChunkPos.z}")
            player.sendPacket(ClientboundSetCenterChunkPacket(currentChunkPos))

            // new chunks
            val chunksToLoad = ChunkUtils.forDifferingChunksInRange(currentChunkX, currentChunkZ, oldCenter.x, oldCenter.z, distance)
            val chunksToUnload = ChunkUtils.forDifferingChunksInRange(oldCenter.x, oldCenter.z, currentChunkX, currentChunkZ, distance)

            chunksToLoad.forEach(::loadChunk)
            chunksToUnload.forEach(::unloadChunk)
        }
    }


    fun resendChunks() {
        player.world.scheduler.runAsync {
            getChunksInRange(player.getCurrentChunkPos()).forEach {
                loadChunk(ChunkPos.fromIndex(it))
            }
        }
    }

    fun loadChunk(pos: ChunkPos) {
        log("loading chunk")
        val world = player.world

        var chunk: Chunk? = world.getChunk(pos.x, pos.z)
        if (chunk != null) {
            player.sendPacket(chunk.packet)
        } else {
            log("generating ${pos.x} ${pos.z}")
            chunk = world.generateChunk(pos.x, pos.z)
            player.sendPacket(chunk.packet)
        }
    }

    fun unloadChunk(pos: ChunkPos) {
        player.sendPacket(ClientboundUnloadChunkPacket(pos))
    }

    fun getChunksInRange(pos: ChunkPos): MutableList<Long> {
        val viewDistance = DEFAULT_RENDER_DISTANCE
        val list = mutableListOf<Long>()
        val chunksInRange = (viewDistance * 2 + 1) * (viewDistance * 2 + 1)

        for (i in 0 until chunksInRange) {
            val (x, z) = chunkInSpiral(i, pos.x, pos.z)
            list.add(ChunkPos(x, z).pack())
        }
        return list
    }
}