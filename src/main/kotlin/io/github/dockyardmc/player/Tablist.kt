package io.github.dockyardmc.player

import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.Viewable

class Tablist: Disposable, Viewable() {

    override var autoViewable: Boolean = false

    private val headerLines: MutableMap<Int, TablistLine> = mutableMapOf()
    val header: Map<Int, TablistLine> get() = headerLines.toMap()

    private val footerLines: MutableMap<Int, TablistLine> = mutableMapOf()
    val footer: Map<Int, TablistLine> get() = footerLines.toMap()

    override fun addViewer(player: Player) {
        update(player)
        viewers.add(player)
    }

    override fun removeViewer(player: Player) {
        player.tabListFooter.value = "".toComponent()
        player.tabListHeader.value = "".toComponent()
        viewers.remove(player)
    }

    fun setHeaderLine(line: Int, value: String) {
        setHeaderLine(line, GlobalTablistLine(value))
    }

    fun setHeaderLine(line: Int, value: GlobalTablistLine) {
        headerLines[line] = value
        viewers.forEach(::update)
    }

    fun setPlayerHeaderLine(line: Int, value: (Player) -> String) {
        setPlayerHeaderLine(line, PlayerTablistLine(value))
    }

    fun setPlayerHeaderLine(line: Int, value: PlayerTablistLine) {
        headerLines[line] = value
        viewers.forEach(::update)
    }

    fun setFooterLine(line: Int, value: String) {
        setFooterLine(line, GlobalTablistLine(value))
    }

    fun setFooterLine(line: Int, value: GlobalTablistLine) {
        footerLines[line] = value
        viewers.forEach(::update)
    }

    fun setPlayerFooterLine(line: Int, value: (Player) -> String) {
        setPlayerFooterLine(line, PlayerTablistLine(value))
    }

    fun setPlayerFooterLine(line: Int, value: PlayerTablistLine) {
        footerLines[line] = value
        viewers.forEach(::update)
    }

    override fun dispose() {
        viewers.toList().forEach(::removeViewer)
        viewers.clear()
        headerLines.clear()
        footerLines.clear()
    }

    private fun update(player: Player) {
        val headerComponent = buildString {
            headerLines.toList().sortedByDescending { line -> line.first }.reversed().forEach { (_, line) ->
                when(line) {
                    is GlobalTablistLine -> append("${line.value}<r>\n")
                    is PlayerTablistLine -> {
                        val playerLine = line.getValue(player)
                        append("$playerLine<r>\n")
                    }
                }
            }
        }

        val footerComponent = buildString {
            footerLines.toList().sortedByDescending { it.first }.reversed().forEach { (_, line) ->
                when(line) {
                    is GlobalTablistLine -> append("${line.value}\n")
                    is PlayerTablistLine -> {
                        val playerLine = line.getValue(player)
                        append("$playerLine\n")
                    }
                }
            }
        }

        player.tabListHeader.value = headerComponent.toComponent()
        player.tabListFooter.value = footerComponent.toComponent()
    }
}

class TablistBuilder {
    val headerLines: MutableList<TablistLine> = mutableListOf()
    val footerLines: MutableList<TablistLine> = mutableListOf()

    fun addHeaderLine(line: String) {
        headerLines.add(GlobalTablistLine(line))
    }

    fun addFooterLine(line: String) {
        footerLines.add(GlobalTablistLine(line))
    }

    fun addPlayerHeaderLine(line: (Player) -> String) {
        headerLines.add(PlayerTablistLine(line))
    }

    fun addPlayerFooterLine(line: (Player) -> String) {
        footerLines.add(PlayerTablistLine(line))
    }
}

fun tablist(builder: TablistBuilder.() -> Unit): Tablist {
    val tablistBuilder = TablistBuilder()
    builder.invoke(tablistBuilder)

    val tablist = Tablist()
    tablistBuilder.headerLines.forEachIndexed { index, line ->
        when(line) {
            is GlobalTablistLine -> tablist.setHeaderLine(index, line)
            is PlayerTablistLine -> tablist.setPlayerHeaderLine(index, line)
        }
    }

    tablistBuilder.footerLines.forEachIndexed { index, line ->
        when(line) {
            is GlobalTablistLine -> tablist.setFooterLine(index, line)
            is PlayerTablistLine -> tablist.setPlayerFooterLine(index, line)
        }
    }

    return tablist
}

interface TablistLine

data class GlobalTablistLine(val value: String): TablistLine

data class PlayerTablistLine(val value: (Player) -> String): TablistLine {
    fun getValue(player: Player): String = value(player)
}
