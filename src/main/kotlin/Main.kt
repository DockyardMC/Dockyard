import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.*
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.getOpposite
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.player.toNormalizedVector3f
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.scheduler.runAsync
import io.github.dockyardmc.utils.DebugSidebar
import io.github.dockyardmc.world.block.Block


fun main() {
    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        useDebugMode(true)
    }

    Events.on<PlayerJoinEvent> { event ->
        val player = event.player
        player.gameMode.value = GameMode.CREATIVE
        DebugSidebar.sidebar.addViewer(player)
        player.permissions.add("*")
        player.give(Items.DEBUG_STICK)
        player.give(Items.OAK_LOG)
    }

    Events.on<PlayerBlockPlaceEvent> { event ->
        val visited = mutableSetOf<Location>()
        if (event.block.registryBlock == Blocks.REDSTONE_BLOCK) {
            runAsync {
                event.location.getNeighbours().forEach { (_, neighborLoc) ->
                    updateRedstoneComponent(neighborLoc, true, visited)
                }
            }
        }
    }

    Events.on<PlayerBlockBreakEvent> { event ->
        val visited = mutableSetOf<Location>()
        if (event.block.registryBlock == Blocks.REDSTONE_BLOCK) {
            runAsync {
                event.location.getNeighbours().forEach { (_, neighborLoc) ->
                    updateRedstoneComponent(neighborLoc, false, visited)
                }
            }
        }
    }

    Events.on<PlayerBlockRightClickEvent> { event ->
        val player = event.player
        val block: Block = event.block
        val registryBlock = event.block.registryBlock

        if(registryBlock != Blocks.LEVER) return@on
        val powered = block.blockStates["powered"]!!.toBoolean()
        event.location.setBlock(block.withBlockStates("powered" to (!powered).toString()))
        runAsync {
            repeat(2) { yOffset ->
                event.location.getNeighbours().forEach { (_, neighborLoc) ->
                    updateRedstoneComponent(neighborLoc.subtract(0, yOffset, 0), !powered, mutableSetOf())
                }
            }
        }
    }

    server.start()
}

fun updateRedstoneComponent(location: Location, powered: Boolean, visited: MutableSet<Location>) {
    if (visited.contains(location)) return
    visited.add(location)

    val block = location.block
    when (block.registryBlock) {
        Blocks.REDSTONE_WIRE -> {
            location.setBlock(block.withBlockStates("power" to if (powered) "15" else "0"))

            location.getNeighbours().forEach { (_, neighborLoc) ->
                updateRedstoneComponent(neighborLoc, powered, visited)
            }
        }

        // is a logic gate
        Blocks.REPEATER -> {
            val facing = block.blockStates["facing"] ?: "north"
            val delay = block.blockStates["delay"]?.toInt() ?: 1

            when (delay) {

                // NOT gate
                1 -> {
                    val inputDirection = when (facing) {
                        "north" -> Direction.SOUTH
                        "south" -> Direction.NORTH
                        "east" -> Direction.WEST
                        "west" -> Direction.EAST
                        else -> Direction.SOUTH
                    }.getOpposite()

                    val outputLocation = location.add(inputDirection.getOpposite().toNormalizedVector3f())
                    updateRedstoneComponent(outputLocation, !powered, visited)
                }

                // AND gate
                // should take inputs from both sides and output if both are on
                2 -> {
                    val (leftDir, rightDir) = when (facing) {
                        "north" -> Direction.WEST to Direction.EAST
                        "south" -> Direction.EAST to Direction.WEST
                        "east" -> Direction.NORTH to Direction.SOUTH
                        "west" -> Direction.SOUTH to Direction.NORTH
                        else -> Direction.WEST to Direction.EAST
                    }

                    val leftInput = location.relative(leftDir)
                    val rightInput = location.relative(rightDir)

                    val leftPowered = isPowered(leftInput)
                    val rightPowered = isPowered(rightInput)
                    val bothPowered = leftPowered && rightPowered

                    val outputLocation = location.relative(Direction.valueOf(facing.uppercase()).getOpposite())
                    updateRedstoneComponent(outputLocation, bothPowered, visited)
                }
                3 -> {
                    val (leftDir, rightDir) = when (facing) {
                        "north" -> Direction.WEST to Direction.EAST
                        "south" -> Direction.EAST to Direction.WEST
                        "east" -> Direction.NORTH to Direction.SOUTH
                        "west" -> Direction.SOUTH to Direction.NORTH
                        else -> Direction.WEST to Direction.EAST
                    }

                    val leftInput = location.relative(leftDir)
                    val rightInput = location.relative(rightDir)

                    val leftPowered = isPowered(leftInput)
                    val rightPowered = isPowered(rightInput)
                    val bothNotPowered = !leftPowered && !rightPowered

                    val outputLocation = location.relative(Direction.valueOf(facing.uppercase()).getOpposite())
                    updateRedstoneComponent(outputLocation, bothNotPowered, visited)
                }

                else -> {}
            }
        }
        Blocks.REDSTONE_LAMP -> {
            location.setBlock(block.withBlockStates("lit" to powered.toString()))
        }
    }
}

fun isPowered(location: Location): Boolean {
    val block = location.block
    if (block.registryBlock != Blocks.REDSTONE_WIRE) return false
    val power = block.blockStates["power"]?.toInt() ?: return false
    return power != 0
}