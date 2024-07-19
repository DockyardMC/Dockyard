package io.github.dockyardmc.world

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
        val currentChunkIndex = getChunkIndex(player.location.x.toInt(), player.location.z.toInt())

        if (currentChunkIndex != lastChunkIndex) {
            lastChunkIndex = currentChunkIndex

            val chunksInRange = getChunksInRange(currentChunkIndex)

            val chunksToLoad = chunksInRange - loadedChunks
            val chunksToUnload = loadedChunks - chunksInRange

            chunksToLoad.forEach { chunkIndex ->
                loadChunk(chunkIndex, world)
            }

            chunksToUnload.forEach { chunkIndex ->
                unloadChunk(chunkIndex, world)
            }
        }
    }

    fun loadChunk(chunkIndex: Long, world: World) {
        if (world.chunks.containsKey(chunkIndex)) {
            // Chunk is already generated
            player.sendPacket(world.getChunkFromIndex(chunkIndex)!!.packet)
            player.sendMessage("<lime>Loading ${ChunkUtils.getChunkCoordsFromIndex(chunkIndex)}")
            loadedChunks.add(chunkIndex)
        } else {
            // Generate chunk asynchronously
            val (x, z) = ChunkUtils.getChunkCoordsFromIndex(chunkIndex)
            AsyncRunnable {
                world.generateChunk(x, z)
            }.apply {
                callback = {
                    player.sendPacket(world.getChunkFromIndex(chunkIndex)!!.packet)
                    loadedChunks.add(chunkIndex)
                }
            }.execute()
        }
    }

    fun unloadChunk(chunkIndex: Long, world: World) {
        val (x, z) = ChunkUtils.getChunkCoordsFromIndex(chunkIndex)
        player.sendPacket(ClientboundUnloadChunkPacket(x, z))
        loadedChunks.remove(chunkIndex)
        player.sendMessage("<red>Unloading ${ChunkUtils.getChunkCoordsFromIndex(chunkIndex)}")
    }

    fun getChunksInRange(centerChunkIndex: Long): Set<Long> {
        val (centerX, centerZ) = ChunkUtils.getChunkCoordsFromIndex(centerChunkIndex)
        return (-chunkRenderDistance..chunkRenderDistance).flatMap { x ->
            (-chunkRenderDistance..chunkRenderDistance).map { z ->
                getChunkIndex(centerX + x, centerZ + z)
            }
        }.toSet()
    }

    private fun getChunkIndex(x: Int, z: Int): Long = ChunkUtils.getChunkIndex(x, z) // Use your existing method
}