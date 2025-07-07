package io.github.dockyardmc.apis.sidebar

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.viewable.Viewable
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class Sidebar(initialTitle: String, initialLines: Map<Int, SidebarLine>) : Viewable(), Disposable {

    val title: Bindable<String> = Bindable(initialTitle)

    private val indexToLineMap: Int2ObjectOpenHashMap<SidebarLine> = Int2ObjectOpenHashMap(initialLines)
    val lines get() = indexToLineMap.toList()

    private val objective = UUID.randomUUID().toString()

    private val createPacket get() = ClientboundScoreboardObjectivePacket(objective, ScoreboardMode.CREATE, title.value, ScoreboardType.INTEGER)
    private val removePacket get() = ClientboundScoreboardObjectivePacket(objective, ScoreboardMode.REMOVE, null, null)
    private val displayPacket get() = ClientboundDisplayObjectivePacket(ObjectivePosition.SIDEBAR, objective)

    override var autoViewable: Boolean = false

    interface SidebarLine {

        class Static(var value: String) : SidebarLine

        class Player(var line: (io.github.dockyardmc.player.Player) -> String) : SidebarLine {
            fun getValue(player: io.github.dockyardmc.player.Player): String = line(player)
        }
    }

    class Builder {
        val lines: Int2ObjectOpenHashMap<SidebarLine> = Int2ObjectOpenHashMap()
        var title: String = ""

        private val defaultLineIndex = AtomicInteger(16)

        private fun addDefaultIndexedLine(line: SidebarLine) {
            lines[defaultLineIndex.getAndDecrement()] = line
        }

        fun withTitle(title: String) {
            this.title = title
        }

        fun withStaticLine(line: String) {
            addDefaultIndexedLine(SidebarLine.Static(line))
        }

        fun withPlayerLine(line: (Player) -> String) {
            addDefaultIndexedLine(SidebarLine.Player(line))
        }

        fun withStaticLine(index: Int, line: String) {
            lines[index] = SidebarLine.Static(line)
        }

        fun withSpacer(index: Int? = null) {
            val line = SidebarLine.Static("    ")
            if (index == null) addDefaultIndexedLine(line) else lines[index] = line
        }

        fun withWideSpacer(index: Int? = null) {
            val line = SidebarLine.Static("                                      ")
            if (index == null) addDefaultIndexedLine(line) else lines[index] = line
        }

        fun withPlayerLine(index: Int, line: (Player) -> String) {
            lines[index] = SidebarLine.Player(line)
        }

        fun build(): Sidebar {
            return Sidebar(title, lines)
        }
    }

    fun setGlobalLine(index: Int, value: String) {
        if (viewers.isEmpty()) return
        val before = indexToLineMap[index] as SidebarLine.Static?
        indexToLineMap[index] = SidebarLine.Static(value)
        if (before?.value != value) viewers.forEach { viewer -> sendLinePacket(viewer, index) }
    }

    fun setPlayerLine(index: Int, value: (Player) -> String) {
        if (viewers.isEmpty()) return
        indexToLineMap[index] = SidebarLine.Player(value)
        viewers.forEach { viewer -> sendLinePacket(viewer, index) }
    }

    private fun sendCreatePackets(player: Player) {
        player.sendPacket(createPacket)
        player.sendPacket(displayPacket)
    }

    private fun sendLinesPackets(player: Player) {
        indexToLineMap.forEach { line ->
            sendLinePacket(player, line.key)
        }
    }

    private fun sendLinePacket(player: Player, line: Int) {
        player.sendPacket(ClientboundUpdateScorePacket(objective, line, getLine(line, player)))
    }

    private fun getLine(index: Int, player: Player): String {
        val value = when (val line = indexToLineMap[index]) {
            is SidebarLine.Static -> line.value
            is SidebarLine.Player -> line.getValue(player)
            else -> ""
        }
        return value.replace("'", "")
    }

    init {
        title.valueChanged { event ->
            if (viewers.isEmpty()) return@valueChanged
            val packet = ClientboundScoreboardObjectivePacket(objective, ScoreboardMode.EDIT_TEXT, event.newValue, ScoreboardType.INTEGER)
            viewers.sendPacket(packet)
        }
    }

    override fun addViewer(player: Player): Boolean {
        if (!super.addViewer(player)) return false
        sendCreatePackets(player)
        sendLinesPackets(player)
        return true
    }

    override fun removeViewer(player: Player) {
        player.sendPacket(removePacket)
    }

    override fun dispose() {
        title.dispose()
        viewers.toList().forEach(::removeViewer)
        indexToLineMap.clear()
    }
}

inline fun sidebar(unit: Sidebar.Builder.() -> Unit): Sidebar {
    val builder = Sidebar.Builder()
    unit.invoke(builder)
    return builder.build()
}