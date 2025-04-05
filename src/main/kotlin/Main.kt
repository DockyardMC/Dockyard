import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.commands.IntArgument
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerFlightToggleEvent
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.sendActionBar
import io.github.dockyardmc.extentions.sendTitle
import io.github.dockyardmc.maths.counter.RollingCounter
import io.github.dockyardmc.maths.counter.RollingCounterInt
import io.github.dockyardmc.maths.randomFloat
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundClientInputPacket
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.utils.DebugSidebar
import kotlin.time.Duration.Companion.seconds
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.utils.DebugSidebar

fun main() {

    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        useDebugMode(true)

    }

    Events.on<PlayerJoinEvent> { event ->
        val player = event.player
        player.gameMode.value = GameMode.CREATIVE
        DebugSidebar.sidebar.viewers.add(player)
        player.canFly.value = true
    }

    val rollingCounter = RollingCounterInt(DockyardServer.scheduler)
    rollingCounter.isRollingProportional = false
    rollingCounter.rollingDuration = 5.seconds
    rollingCounter.rollingEasing = RollingCounter.Easing.IN_EXPO
    rollingCounter.rollDispatcher.subscribe { int ->
        PlayerManager.players.sendTitle("<lime>${int}", "", 0, 9999, 0)
    }

    Events.on<ServerTickEvent> { _ ->
        PlayerManager.players.sendActionBar("<yellow>Counter is at: <lime><bold>${rollingCounter.animatedDisplayValue}")
    }

    Commands.add("/counter") {
        addSubcommand("set") {
            addArgument("number", IntArgument())
            addArgument("seconds", IntArgument())
            addArgument("easing", EnumArgument(RollingCounter.Easing::class))
            execute { _ ->
                val number = getArgument<Int>("number")
                val time = getArgument<Int>("seconds")
                val easing = getEnumArgument<RollingCounter.Easing>("easing")
                rollingCounter.rollingEasing = easing
                rollingCounter.rollingDuration = time.seconds
                rollingCounter.value.value = number
            }
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