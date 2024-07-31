package io.github.dockyardmc.plugins.bundled

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.ServerMetrics
import io.github.dockyardmc.bossbar.Bossbar
import io.github.dockyardmc.bossbar.BossbarColor
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.item.EnchantmentGlintOverrideItemComponent
import io.github.dockyardmc.item.FoodItemComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.SecondPeriod
import io.github.dockyardmc.periodic.TickPeriod
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.addIfNotPresent
import io.github.dockyardmc.player.removeIfPresent
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEffectPacket
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.serverlinks.DefaultServerLinkType
import io.github.dockyardmc.serverlinks.DefaultServerLink
import io.github.dockyardmc.serverlinks.CustomServerLink
import io.github.dockyardmc.serverlinks.ServerLinks
import io.github.dockyardmc.sidebar.Sidebar
import io.github.dockyardmc.ui.CookieClickerScreen
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.world.WorldManager

class MayaTestPlugin: DockyardPlugin {
    override var name: String = "MayaTestPlugin"
    override val author: String = "LukynkaCZE"
    override val version: String = DockyardServer.versionInfo.dockyardVersion

    override fun load(server: DockyardServer) {

        val serverBar = Bossbar("<aqua>DockyardMC <dark_gray>| <gray>Version ${DockyardServer.versionInfo.dockyardVersion} (${DockyardServer.versionInfo.gitBranch})", 1f, BossbarColor.BLUE)

        val sidebar = Sidebar("<aqua><bold>DockyardMC") {
            setGlobalLine("")
            setPlayerLine{ "Hello, <yellow>${it.username}<white>!" }
            setGlobalLine("Welcome to DockyardMC!")
            setGlobalLine(" ")
            setGlobalLine("<white>Server uptime: <lime>0")
        }

        Period.on<TickPeriod> {
            val runtime = Runtime.getRuntime()
            val mspt = ServerMetrics.millisecondsPerTick
            val memoryUsage = runtime.totalMemory() - runtime.freeMemory()
            val memUsagePercent = MathUtils.percent(runtime.totalMemory().toDouble(), memoryUsage.toDouble()).truncate(0)

            val fMem = (memoryUsage.toDouble() / 1000000).truncate(1)
            val fMax = (runtime.totalMemory().toDouble() / 1000000).truncate(1)
            PlayerManager.players.forEach {
                it.sendActionBar("<white>MSPT: <lime>$mspt <dark_gray>| <white>Memory Usage: <#ff6830>$memUsagePercent% <gray>(${fMem}mb / ${fMax}mb) <dark_gray>| <white>World: <#f224a7>${it.world.name} <gray>(${it.world.players.values.size})")
            }
        }

        Events.on<PlayerJoinEvent> {
            val effectPacket = ClientboundEntityEffectPacket(it.player, 15, 1, 99999, 0x00)
            it.player.sendPacket(effectPacket)

            it.player.tabListHeader.value = "\n  <dark_gray><s>        <r>  <aqua>DockyardMC<r>  <dark_gray><s>        <r>  \n".toComponent()
            it.player.tabListFooter.value = "\n  <dark_gray><s>                                   <r>  \n".toComponent()
            serverBar.viewers.addIfNotPresent(it.player)

            it.player.experienceBar.value = 1f
            it.player.experienceLevel.value= 0

            it.player.gameMode.value = GameMode.SURVIVAL
            it.player.inventory[0] = ItemStack(Items.COOKIE).apply { displayName.value = "<orange><u>Cookie Clicker<r> <gray>(Right-Click)"; components.add(EnchantmentGlintOverrideItemComponent(true)) }

            sidebar.viewers.addIfNotPresent(it.player)
        }

        Events.on<PlayerRightClickWithItemEvent> {
            if(it.item.displayName.value != "<orange><u>Cookie Clicker<r> <gray>(Right-Click)") return@on
            it.player.sendMessage("<orange>Cookie Clicker <dark_gray>| <gray>Opening the cookie clicker menu..")
            it.player.openDrawableScreen(CookieClickerScreen())
        }

        var seconds: Int = 0
        Period.on<SecondPeriod> {
            seconds++
            PlayerManager.players.forEach { it.experienceLevel.value = seconds }
            sidebar.setGlobalLine(12, "<white>Server uptime: <lime>$seconds")
        }

        ServerLinks.links.add(CustomServerLink("<aqua>Github", "https://github.com/DockyardMC/Dockyard"))
        ServerLinks.links.add(CustomServerLink("<aqua>Discord", "https://discord.gg/SA9nmfMkdc"))
        ServerLinks.links.add(DefaultServerLink(DefaultServerLinkType.BUG_REPORT, "https://github.com/DockyardMC/Dockyard/issues"))

        Commands.add("/world") { cmd ->
            cmd.addArgument("world", StringArgument())
            cmd.execute { executor ->
                val player = executor.player!!
                val world = WorldManager.getOrThrow(cmd.get<String>("world"))
                world.join(player)
            }
        }

        Commands.add("/sidebar") {
            it.execute { ctx ->
                val player = ctx.player!!
                sidebar.viewers.removeIfPresent(player)
            }
        }

        Events.on<PlayerDamageEvent> {
            it.damage = 20f
            DockyardServer.broadcastMessage("${it.player} damage ${it.damage}")
        }

        Events.on<EntityDamageEvent> {
            DockyardServer.broadcastMessage("${it.entity} damage ${it.damage}")
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