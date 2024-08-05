package io.github.dockyardmc.plugins.bundled

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.ServerMetrics
import io.github.dockyardmc.bossbar.Bossbar
import io.github.dockyardmc.bossbar.BossbarColor
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.item.EnchantmentGlintOverrideItemComponent
import io.github.dockyardmc.item.FoodItemComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.SecondPeriod
import io.github.dockyardmc.periodic.TickPeriod
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.addIfNotPresent
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEffectPacket
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.schematics.SchematicReader
import io.github.dockyardmc.schematics.placeSchematic
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.serverlinks.DefaultServerLinkType
import io.github.dockyardmc.serverlinks.DefaultServerLink
import io.github.dockyardmc.serverlinks.CustomServerLink
import io.github.dockyardmc.serverlinks.ServerLinks
import io.github.dockyardmc.sidebar.Sidebar
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.ui.CookieClickerScreen
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.world.WorldManager
import java.io.File

class MayaTestPlugin: DockyardPlugin {
    override var name: String = "MayaTestPlugin"
    override val author: String = "LukynkaCZE"
    override val version: String = DockyardServer.versionInfo.dockyardVersion

    override fun load(server: DockyardServer) {

        val serverBar = Bossbar(
            "<aqua>DockyardMC <dark_gray>| <gray>Version ${DockyardServer.versionInfo.dockyardVersion} (${DockyardServer.versionInfo.gitBranch})",
            1f,
            BossbarColor.BLUE
        )

        val sidebar = Sidebar("<aqua><bold>DockyardMC") {
            setGlobalLine("")
            setPlayerLine { "Hello, <#ed9eff><u>${it.username}</u><white>!" }
            setGlobalLine("Welcome to DockyardMC!")
            setGlobalLine(" ")
            setGlobalLine("<white>Server uptime: <lime>0")
            setGlobalLine(" ")
            setPlayerLine { "Your health is: <red>${it.health}" }
            setPlayerLine { "Your food is: <orange>${it.food}" }
            setGlobalLine(" ")
            setGlobalLine("<yellow>www.github.com/DockyardMC/")
        }

        Period.on<TickPeriod> {
            val runtime = Runtime.getRuntime()
            val mspt = ServerMetrics.millisecondsPerTick
            val memoryUsage = runtime.totalMemory() - runtime.freeMemory()
            val memUsagePercent =
                MathUtils.percent(runtime.totalMemory().toDouble(), memoryUsage.toDouble()).truncate(0)

            val fMem = (memoryUsage.toDouble() / 1000000).truncate(1)
            val fMax = (runtime.totalMemory().toDouble() / 1000000).truncate(1)
            PlayerManager.players.forEach {
                it.sendActionBar("<white>MSPT: <lime>$mspt <dark_gray>| <white>Memory Usage: <#ff6830>$memUsagePercent% <gray>(${fMem}mb / ${fMax}mb) <dark_gray>| <white>World: <#f224a7>${it.world.name} <gray>(${it.world.players.values.size})")
            }
        }

        Events.on<PlayerJoinEvent> {
            val effectPacket = ClientboundEntityEffectPacket(it.player, 15, 1, 99999, 0x00)
            it.player.sendPacket(effectPacket)

            it.player.tabListHeader.value =
                "\n  <dark_gray><s>        <r>  <aqua>DockyardMC<r>  <dark_gray><s>        <r>  \n".toComponent()
            it.player.tabListFooter.value = "\n  <dark_gray><s>                                   <r>  \n".toComponent()
            serverBar.viewers.addIfNotPresent(it.player)

            it.player.experienceBar.value = 1f
            it.player.experienceLevel.value = 0

            sidebar.viewers.addIfNotPresent(it.player)
        }

        Events.on<PlayerRightClickWithItemEvent> {
            if (it.item.displayName.value != "<orange><u>Cookie Clicker<r> <gray>(Right-Click)") return@on
            it.player.sendMessage("<orange>Cookie Clicker <dark_gray>| <gray>Opening the cookie clicker menu..")
            it.player.openDrawableScreen(CookieClickerScreen())
        }

        var seconds: Int = 0
        Period.on<SecondPeriod> {
            seconds++
            PlayerManager.players.forEach { it.experienceLevel.value = seconds }
            sidebar.setGlobalLine(12, "<white>Server uptime: <lime>${seconds}s")
        }

        ServerLinks.links.add(CustomServerLink("<aqua>Github", "https://github.com/DockyardMC/Dockyard"))
        ServerLinks.links.add(CustomServerLink("<aqua>Discord", "https://discord.gg/SA9nmfMkdc"))
        ServerLinks.links.add(
            DefaultServerLink(
                DefaultServerLinkType.BUG_REPORT,
                "https://github.com/DockyardMC/Dockyard/issues"
            )
        )

        Commands.add("/world") { cmd ->
            cmd.addArgument("world", StringArgument())
            cmd.execute { executor ->
                val player = executor.player!!
                val world = WorldManager.getOrThrow(cmd.get<String>("world"))
                world.join(player)
            }
        }

        val warps = mutableMapOf<String, Location>()

        Commands.add("/setwarp") {
            it.addArgument("name", StringArgument())
            it.execute { ctx ->
                val player = ctx.player!!
                val name = it.get<String>("name")
                val location = player.location

                warps[name] = location
                player.sendMessage("<yellow>Set warp <lime>$name<yellow> to <pink>$location")
            }
        }

        Commands.add("/warp") {
            it.addArgument("name", StringArgument())
            it.execute { ctx ->
                val player = ctx.player!!
                val name = it.get<String>("name")
                val location = warps[name] ?: throw Exception("This warp not exist!")
                player.teleport(location)
                player.playSound("entity.enderman.teleport", player.location, 1f, 2f)
                player.spawnParticle(player.location.add(Vector3f(0f, 0.5f, 0f)), Particles.PORTAL, Vector3f(0f), 1f,15)
            }
        }

        Commands.add("/paste") {
            it.execute { ctx ->
                val player = ctx.playerOrThrow()
                val file = File("./map.schem")
                val schematic = SchematicReader.read(file)
                player.world.placeSchematic(player.location, schematic)
            }
        }

        Commands.add("/item") {
            it.execute { exec ->
                val player = exec.player!!
                val item = ItemStack(Items.AMETHYST_SHARD, 999)
                item.displayName.value = "<pink><underline>Woooah Magical Shaaarddddd"
                item.customModelData.value = 1
                item.unbreakable.value = true
                item.maxStackSize.value = 999
                item.lore.add(" ")
                item.lore.add("<gray>This is very <lime><i>very <gray></i>magical shard.")
                item.lore.add(" ")
                item.lore.add("<orange>⭐ <yellow>This item is edible!")
                item.lore.add("<orange>⭐ <yellow>Max stack size is 999")
                item.lore.add(" ")
                item.components.add(FoodItemComponent(1))
                player.inventory[0] = item
            }
        }
    }


    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}