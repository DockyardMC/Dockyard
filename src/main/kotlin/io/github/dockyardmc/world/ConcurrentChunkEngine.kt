package io.github.dockyardmc.world

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUnloadChunkPacket
import io.github.dockyardmc.runnables.AsyncRunnable
import io.github.dockyardmc.utils.ChunkUtils

class ConcurrentChunkEngine(val player: Player) {

    val loadedChunks: MutableSet<Long> = mutableSetOf()
    val chunkRenderDistance = 8
    var lastChunkIndex: Long = 0L

    fun update() {
        val world = player.world
        val currentChunkX = ChunkUtils.getChunkCoordinate(player.location.x)
        val currentChunkZ = ChunkUtils.getChunkCoordinate(player.location.z)

        val currentChunkIndex = ChunkUtils.getChunkIndex(currentChunkX, currentChunkZ)

        if (currentChunkIndex != lastChunkIndex) {
            lastChunkIndex = currentChunkIndex

            val chunksInRange = getChunksInRange(currentChunkIndex, world)

            val chunksToLoad = mutableListOf<Long>()
            val chunksToUnload = mutableListOf<Long>()

            chunksInRange.forEach {
                if(loadedChunks.contains(it)) return@forEach
                chunksToLoad.add(it)
            }

            loadedChunks.forEach {
                if(chunksInRange.contains(it)) return@forEach
                chunksToLoad.add(it)
            }

            chunksToLoad.forEach { chunkIndex ->
                loadChunk(chunkIndex, world)
            }

            chunksToUnload.forEach { chunkIndex ->
                val chunk = world.getChunkFromIndex(chunkIndex) ?: return@forEach
                unloadChunk(chunk)
            }
        }
    }

    fun loadChunk(chunkIndex: Long, world: World) {
        if (world.chunks.containsKey(chunkIndex)) {
            player.sendPacket(world.getChunkFromIndex(chunkIndex)!!.packet)
            loadedChunks.add(chunkIndex)
        } else {
            val (x, z) = ChunkUtils.getChunkCoordsFromIndex(chunkIndex)
            AsyncRunnable {
                try {
                    world.generateChunk(x, z)
                } catch (exception: Exception) {
                    throw exception
                }
            }.apply {
                callback = {
                    player.sendPacket(world.getChunkFromIndex(chunkIndex)!!.packet)
                    loadedChunks.add(chunkIndex)
                    DockyardServer.broadcastMessage("<pink>generated chunk $x $z")
                }
            }.execute()
        }
    }

    fun unloadChunk(chunk: Chunk) {
        player.sendPacket(ClientboundUnloadChunkPacket(chunk.chunkX, chunk.chunkZ))
        loadedChunks.remove(chunk.getIndex())
    }

    fun getChunksInRange(index: Long, world: World): MutableList<Long> {
        val viewDistance = chunkRenderDistance + 1
        val (chunkX, chunkZ) = ChunkUtils.getChunkCoordsFromIndex(index)

        val list = mutableListOf<Long>()
        for (x in chunkX - viewDistance..chunkX + viewDistance) {
            for(z in chunkZ - viewDistance..chunkZ + viewDistance) {
                val distanceSqrt = (x - chunkX) * (x - chunkX) + (z - chunkZ) * (z - chunkZ)
                if(distanceSqrt <= viewDistance * viewDistance) {
                    list.add(ChunkUtils.getChunkIndex(x, z))
                }
            }
        }
        return list
    }
}