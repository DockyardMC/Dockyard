package io.github.dockyardmc.utils.viewable

import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

abstract class Viewable {

    private val innerViewers: ObjectOpenHashSet<Player> = ObjectOpenHashSet()
    val viewers: List<Player> get() = innerViewers.toList()

    private val innerRules: MutableMap<String, ViewRule> = mutableMapOf()
    val rules get() = innerRules.toMap()

    abstract var autoViewable: Boolean

    fun addViewRule(identifier: String, filter: (Player) -> Boolean) {
        if (innerRules.containsKey(identifier)) throw IllegalArgumentException("View Rule with identifier `$identifier` already exists on this viewable")
        innerRules[identifier] = ViewRule(filter)
    }

    fun passesViewRules(player: Player): Boolean {
        return rules.all { (_, rule) -> rule.passes(player) }
    }

    open fun addViewer(player: Player): Boolean {
        if (rules.all { (_, rule) -> rule.passes(player) && !viewers.contains(player) }) {
            synchronized(innerViewers) {
                innerViewers.add(player)
            }
            return true
        }
        return false
    }

    open fun removeViewer(player: Player) {
        synchronized(innerViewers) {
            innerViewers.remove(player)
        }
    }

    fun isViewer(player: Player): Boolean {
        return viewers.contains(player)
    }

    fun sendPacketToViewers(packet: ClientboundPacket) {
        viewers.sendPacket(packet)
    }

    fun clearViewers() {
        viewers.toList().forEach { viewer ->
            removeViewer(viewer)
        }
    }
}