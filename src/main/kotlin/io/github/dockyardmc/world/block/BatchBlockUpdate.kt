package io.github.dockyardmc.world.block

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.world.World

@Suppress("LoopToCallChain")
class BatchBlockUpdate(val world: World) {
    var updates: MutableMap<Location, Block> = mutableMapOf()

    fun setBlock(location: Location, block: Block) {
        updates[location] = block
    }

    fun setBlock(location: Location, block: RegistryBlock) {
        setBlock(location, block.toBlock())
    }

    fun setBlock(x: Double, y: Double, z: Double, block: Block) {
        updates[Location(x, y, z, world)] = block
    }

    fun setBlock(x: Double, y: Double, z: Double, block: RegistryBlock) {
        setBlock(x, y, z, block.toBlock())
    }

    fun setBlock(x: Int, y: Int, z: Int, block: Block) {
        updates[Location(x, y, z, world)] = block
    }

    fun setBlock(x: Int, y: Int, z: Int, block: RegistryBlock) {
        setBlock(x, y, z, block.toBlock())
    }

    fun fill(fromX: Double, fromY: Double, fromZ: Double, toX: Double, toY: Double, toZ: Double, block: Block) {
        fill(Location(fromX, fromY, fromZ, world), Location(toX, toY, toZ, world), block)
    }

    fun fill(fromX: Double, fromY: Double, fromZ: Double, toX: Double, toY: Double, toZ: Double, block: RegistryBlock) {
        fill(fromX, fromY, fromZ, toX, toY, toZ, block.toBlock())
    }

    fun fill(from: Location, to: Location, block: RegistryBlock) {
        fill(from, to, block.toBlock())
    }

    fun fill(from: Location, to: Location, block: Block) {
        val minX = minOf(from.x, to.x)
        val minY = minOf(from.y, to.y)
        val minZ = minOf(from.z, to.z)

        val maxX = maxOf(from.x, to.x)
        val maxY = maxOf(from.y, to.y)
        val maxZ = maxOf(from.z, to.z)

        for (iX in minX.toInt()..maxX.toInt()) {
            for (iY in minY.toInt()..maxY.toInt()) {
                for (iZ in minZ.toInt()..maxZ.toInt()) {
                    val location = Location(iX, iY, iZ, world)
                    updates[location] = block
                }
            }
        }
    }
}