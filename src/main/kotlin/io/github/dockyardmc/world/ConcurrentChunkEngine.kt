package io.github.dockyardmc.world

import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUnloadChunkPacket
import io.github.dockyardmc.utils.ChunkUtils

class ConcurrentChunkEngine(val player: Player) {

    val loadedChunks: MutableList<Long> = mutableListOf()
    val chunkRenderDistance = 2

    fun update() {
        val world = player.world

        val currentChunkX = ChunkUtils.getChunkCoordinate(player.location.x.toInt())
        val currentChunkZ = ChunkUtils.getChunkCoordinate(player.location.z.toInt())

        val chunksInRange = mutableSetOf<Long>()

        for (x in currentChunkX - chunkRenderDistance..currentChunkX + chunkRenderDistance) {
            for (z in currentChunkZ - chunkRenderDistance..currentChunkZ + chunkRenderDistance) {
                chunksInRange.add(ChunkUtils.getChunkIndex(x, z))
            }
        }


        val chunksToLoad = chunksInRange.filter { !loadedChunks.contains(it) }
        val chunksToUnload = loadedChunks.filter { !chunksInRange.contains(it) }

        player.sendMessage("+<lime>${chunksToLoad.size}")
        player.sendMessage("=<yellow>${chunksInRange.size}")
        player.sendMessage("-<red>${chunksToLoad.size}")

        chunksToLoad.forEach { chunkIndex ->
            if(world.chunks[chunkIndex] != null) {
                player.sendPacket(world.getChunkFromIndex(chunkIndex)!!.packet)
            } else {
                //generate chunk
                log("TODO: generate new chunk at index ${ChunkUtils.getChunkCoordsFromIndex(chunkIndex)}")
            }
        }

        chunksToUnload.forEach { chunkIndex ->
            val chunkCoords = ChunkUtils.getChunkCoordsFromIndex(chunkIndex)
            player.sendPacket(ClientboundUnloadChunkPacket(chunkCoords.first, chunkCoords.second))
        }

        loadedChunks.addAll(chunksToLoad)
        loadedChunks.removeAll(chunksToUnload)
    }

}