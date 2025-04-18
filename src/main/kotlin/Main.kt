import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerFlightToggleEvent
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.maths.randomFloat
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundClientInputPacket
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.DebugSidebar
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.VoidWorldGenerator

fun suggestPotionEffects(player: Player): List<String> {
    return PotionEffectRegistry.potionEffects.keys.toList()
}

fun main() {
    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        useDebugMode(true)
    }

    Events.on<PlayerJoinEvent> { event ->
        val player = event.player
        player.permissions.add("dockyard.admin")
        player.permissions.add("dockyard.*")
        player.gameMode.value = GameMode.CREATIVE
        DebugSidebar.sidebar.viewers.add(player)
        player.canFly.value = true
    }

    Events.on<ServerTickEvent> { event ->
        PlayerManager.players.forEach { player ->
            player.sendActionBar("<yellow>Holding: ${player.heldInputs}")
        }
    }

    Events.on<PlayerFlightToggleEvent> { event ->
        val player = event.player
        player.isFlying.value = false

        var subtitle = ""

        var newVelocity = Vector3d(0.0, 1.0, 0.0)
        val multBy = 12.0

        val dir = player.location.getDirection(true)
        val up = Vector3d(0.0, 2.0, 0.0)
        val cross = up.cross(dir)
        val rightVector = if (cross.length() > 0.0) cross.normalized() else Vector3d(1.0, 0.0, 0.0)

        if (player.heldInputs.contains(ServerboundClientInputPacket.Input.FORWARD)) {
            newVelocity += dir * multBy
            subtitle += "↑"
        }
        if (player.heldInputs.contains(ServerboundClientInputPacket.Input.BACKWARDS)) {
            newVelocity -= dir * multBy
            subtitle += "↓"
        }
        if (player.heldInputs.contains(ServerboundClientInputPacket.Input.LEFT)) {
            newVelocity += rightVector * multBy
            subtitle += "←"
        }
        if (player.heldInputs.contains(ServerboundClientInputPacket.Input.RIGHT)) {
            newVelocity -= rightVector * multBy
            subtitle += "→"
        }

        player.setVelocity(newVelocity)
        player.sendTitle("", "<#aca3ff>$subtitle", 0, 5, 5)
        player.playSound(Sounds.ENTITY_ENDER_DRAGON_FLAP, player.location, 1f, randomFloat(1.6f, 2f))
        player.playSound(Sounds.BLOCK_FIRE_EXTINGUISH, player.location, 0.1f, randomFloat(1.6f, 2f))
        player.world.spawnParticle(player.location, Particles.CLOUD, Vector3f(0f), 0.1f, 4)
    }

    server.start()
}