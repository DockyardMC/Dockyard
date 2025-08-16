package io.github.dockyardmc.utils.viewable

import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

abstract class Viewable {

    private val _viewers: ObjectOpenHashSet<Player> = ObjectOpenHashSet()
    val viewers: List<Player> get() = _viewers.toList()

    private val _rules: MutableMap<String, ViewRule> = mutableMapOf()
    val rules get() = _rules.toMap()

    abstract var autoViewable: Boolean

    fun addViewRule(identifier: String, filter: (Player) -> Boolean) {
        if (_rules.containsKey(identifier)) throw IllegalArgumentException("View Rule with identifier `$identifier` already exists on this viewable")
        _rules[identifier] = ViewRule(filter)
    }

    fun passesViewRules(player: Player): Boolean {
        return rules.all { (_, rule) -> rule.passes(player) }
    }

    open fun addViewer(player: Player): Boolean {
        if (rules.all { (_, rule) -> rule.passes(player) && !viewers.contains(player) }) {
            synchronized(_viewers) {
                _viewers.add(player)
            }
            return true
        }
        return false
    }

    open fun removeViewer(player: Player) {
        synchronized(_viewers) {
            _viewers.remove(player)
        }
    }

    fun isViewer(player: Player): Boolean {
        return viewers.contains(player)
    }

    fun clearViewers() {
        viewers.toList().forEach { viewer ->
            removeViewer(viewer)
        }
    }

    fun sendPacketToViewers(packet: ClientboundPacket) {
        viewers.sendPacket(packet)
    }
}