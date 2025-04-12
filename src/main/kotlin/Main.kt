import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.CommandException
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.entity.BlockDisplay
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.utils.DebugSidebar

fun suggestPotionEffects(player: Player): List<String> {
    return PotionEffectRegistry.potionEffects.keys.toList()
}

fun main() {
    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        withUpdateChecker(false)
        useDebugMode(true)
    }

    Events.on<PlayerJoinEvent> { event ->
        val player = event.player
        player.permissions.add("dockyard.admin")
        player.permissions.add("dockyard.*")
        player.gameMode.value = GameMode.CREATIVE
        DebugSidebar.sidebar.viewers.add(player)
    }

    var laser: BlockDisplay? = null

    val colorMap = mapOf<LegacyTextColor, RegistryBlock>(
        LegacyTextColor.DARK_BLUE to Blocks.BLUE_STAINED_GLASS,
        LegacyTextColor.GREEN to Blocks.LIME_STAINED_GLASS,
        LegacyTextColor.CYAN to Blocks.LIGHT_BLUE_STAINED_GLASS,
        LegacyTextColor.DARK_RED to Blocks.RED_STAINED_GLASS,
        LegacyTextColor.PURPLE to Blocks.PINK_STAINED_GLASS,
        LegacyTextColor.ORANGE to Blocks.YELLOW_STAINED_GLASS,
        LegacyTextColor.WHITE to Blocks.WHITE_STAINED_GLASS,
    )

    fun suggestColors(player: Player): List<String> {
        return colorMap.keys.map { key -> key.name }
    }

    Commands.add("/laser") {

        addSubcommand("spawn") {
            execute { ctx ->
                val initialColor = colorMap[LegacyTextColor.WHITE]!!

                val player = ctx.getPlayerOrThrow()
                if (laser != null) throw CommandException("laser already exists")
                laser = player.world.spawnEntity(BlockDisplay(player.location)) as BlockDisplay
                laser!!.block.value = initialColor.toBlock()
                laser!!.isGlowing.value = true
                laser!!.glowColor.value = CustomColor.fromHex(LegacyTextColor.WHITE.hex)
                laser!!.brightness.value = 255
                laser!!.scaleTo(0.15f, 15f, 0.15f)
            }
        }

        addSubcommand("color") {
            addArgument("color", StringArgument(), ::suggestColors)
            execute { _ ->
                if(laser == null) throw CommandException("laser is null")
                val color = LegacyTextColor.entries.first { color -> color.name == getArgument<String>("color") }
                val block = colorMap[color] ?: throw CommandException("unsupported color")
                laser!!.block.value = block.toBlock()
                laser!!.glowColor.value = CustomColor.fromHex(color.hex)
            }
        }
    }

    server.start()
}