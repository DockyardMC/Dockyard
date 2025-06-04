import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.ItemDropEntity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerRightClickWithItemEvent
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.randomFloat
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.maths.velocity.VelocityPhysics
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.github.dockyardmc.scheduler.runLaterAsync
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.utils.DebugSidebar
import kotlin.time.Duration.Companion.seconds

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

    Events.on<PlayerRightClickWithItemEvent> { event ->
        event.player.setVelocity(Vector3d(0, 20.5, 0))
    }

    Commands.add("/test") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
            player.world.scheduler.repeat(30, 1.ticks) {
                repeat(3) {
                    val physics = VelocityPhysics(player.location, Vector3f(randomFloat(-0.50f, 0.50f), 0.5f, randomFloat(-0.50f, 0.50f)), true)
                    val entity = player.world.spawnEntity(ItemDropEntity(player.location, ItemStack(ItemRegistry.items.values.random()))) as ItemDropEntity

                    physics.onTick.subscribe { location ->
                        entity.teleport(location)
                    }

                    physics.start()

                    runLaterAsync(5.seconds) {
                        physics.dispose()
                        player.world.despawnEntity(entity)
                    }
                }
            }
        }
    }

    server.start()
}