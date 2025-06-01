import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.types.ConsumeEffect
import io.github.dockyardmc.registry.*
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
        DebugSidebar.sidebar.viewers.add(player)
        player.permissions.add("*")
        player.give(Items.OAK_LOG.toItemStack().withMeta { withEnchantmentGlint(true) })
    }

    val effects = listOf(
        AppliedPotionEffect(PotionEffects.OOZING, AppliedPotionEffectSettings(1, 30.seconds, true, true, false))
    )

    val applyEffect = ConsumeEffect.ApplyEffects(effects, 1f)
//    val component = ConsumableComponent(1f, ConsumableComponent.Animation.EAT, Sounds.ENTITY_GENERIC_EAT, false, applyEffects)

    val hash = applyEffect.hashStruct().getHashed()

    log("dockyard transcoder - $hash")

    server.start()
}