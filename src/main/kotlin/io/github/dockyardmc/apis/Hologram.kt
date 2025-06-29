package io.github.dockyardmc.apis

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.*
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.metadata.EntityMetaValue
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.EntityMetadataType
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.toPersistent
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.scroll.extensions.toComponent

class HologramBuilder {
    val lines: MutableList<Hologram.ContentLine> = mutableListOf()

    fun withStaticLine(line: String) {
        lines.add(Hologram.StaticContentLine(line))
    }

    fun withPlayerLine(line: (Player) -> String) {
        lines.add(Hologram.PlayerContentLine(line))
    }
}

fun hologram(location: Location, unit: HologramBuilder.() -> Unit): Hologram {
    val builder = HologramBuilder()
    unit.invoke(builder)
    return location.world.spawnEntity(Hologram(location, builder)) as Hologram
}

class Hologram(spawnLocation: Location, builder: HologramBuilder) : Entity(spawnLocation, spawnLocation.world) {

    private val lines = mutableListOf<ContentLine>()
    private val lineEntities = mutableListOf<TextDisplay>()

    override var type: EntityType = EntityTypes.MARKER
    override val health: Bindable<Float> = Bindable(0f)
    override var inventorySize: Int = 0

    override var autoViewable: Boolean = true

    override var tickable: Boolean = false

    override fun teleport(location: Location) {
        lineEntities.forEachIndexed { index, entity ->
            entity.teleport(location.subtract(0.0, index * 0.3, 0.0))
        }
    }

    override fun teleportClientside(location: Location, player: Player) {
        lineEntities.forEachIndexed { index, entity ->
            entity.teleportClientside(location.subtract(0.0, index * 0.3, 0.0), player)
        }
    }

    init {
        lines.addAll(builder.lines)
        viewers.forEach(::updateFull)
    }

    fun addStaticLine(line: StaticContentLine) {
        lines.add(line)
        if (viewers.isEmpty()) return
        viewers.forEach(::updateFull)
    }

    fun addStaticLine(line: String) {
        addStaticLine(StaticContentLine(line))
    }

    fun addPlayerLine(line: PlayerContentLine) {
        lines.add(line)
        if (viewers.isEmpty()) return
        viewers.forEach(::updateFull)
    }

    fun addPlayerLine(line: (Player) -> String) {
        addPlayerLine(PlayerContentLine(line))
    }

    fun setStaticLine(lineIndex: Int, line: String) {
        setStaticLine(lineIndex, StaticContentLine(line))
    }

    fun setStaticLine(lineIndex: Int, line: StaticContentLine) {
        val currentLine = lines.getOrNull(lineIndex) ?: return

        if (currentLine !is StaticContentLine) {
            viewers.forEach(::updateFull)
            return
        }

        if (line.line == currentLine.line) return

        lines[lineIndex] = currentLine
        setGlobalLineContent(lineIndex, line.line)
    }

    fun setPlayerLine(lineIndex: Int, line: (Player) -> String) {
        setPlayerLine(lineIndex, PlayerContentLine(line))
    }

    fun setPlayerLine(lineIndex: Int, line: PlayerContentLine) {
        val currentLine = lines.getOrNull(lineIndex) ?: return

        if (currentLine !is PlayerContentLine) {
            viewers.forEach(::updateFull)
            return
        }

        lines[lineIndex] = currentLine

        viewers.forEach { viewer ->
            setPlayerLineContent(viewer, lineIndex, line.line.invoke(viewer))
        }
    }

    private fun updateFull(player: Player) {

        lines.forEachIndexed { index, line ->
            var entity = lineEntities.getOrNull(index)
            if (entity == null) {
                entity = location.world.spawnEntity(TextDisplay(location.subtract(0.0, index * 0.3, 0.0))) as TextDisplay
                entity.autoViewable = false
                entity.lineWidth.value = Int.MAX_VALUE
                lineEntities.add(entity)
            }
            if (line !is PlayerContentLine && entity.metadataLayers.values.isNotEmpty()) entity.metadataLayers.clear()

            when (line) {
                is StaticContentLine -> setGlobalLineContent(index, line.line)
                is PlayerContentLine -> {
                    setPlayerLineContent(player, index, line.line.invoke(player))
                }
            }

            entity.addViewer(player)
        }
    }

    private fun setPlayerLineContent(player: Player, lineIndex: Int, message: String) {
        val display = lineEntities.getOrNull(lineIndex) ?: return

        if (display.metadataLayers[player.toPersistent()] == null) display.metadataLayers[player.toPersistent()] = mutableMapOf()
        val layer = display.metadataLayers[player.toPersistent()]!!
        layer[EntityMetadataType.TEXT_DISPLAY_TEXT] = EntityMetadata(EntityMetadataType.TEXT_DISPLAY_TEXT, EntityMetaValue.TEXT_COMPONENT, message.toComponent())
        display.sendMetadataPacket(player)
    }

    private fun setGlobalLineContent(lineIndex: Int, message: String) {
        val display = lineEntities.getOrNull(lineIndex) ?: return
        display.text.value = if (message.replace(" ", "").isEmpty()) "" else message
    }

    override fun dispose() {
        lineEntities.toList().forEach { entity ->
            world.despawnEntity(entity)
        }
        clearViewers()
        lines.clear()
        lineEntities.clear()
        super.dispose()
    }

    override fun addViewer(player: Player): Boolean {
        if (!super.addViewer(player)) return false

        lineEntities.forEach { entity ->
            entity.addViewer(player)
        }
        updateFull(player)
        return true
    }

    override fun removeViewer(player: Player) {
        lineEntities.forEach { entity ->
            entity.removeViewer(player)
        }
        super.removeViewer(player)
    }

    interface ContentLine

    data class StaticContentLine(val line: String) : ContentLine

    data class PlayerContentLine(val line: (Player) -> String) : ContentLine
}