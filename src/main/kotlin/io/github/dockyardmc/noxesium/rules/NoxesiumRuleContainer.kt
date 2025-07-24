package io.github.dockyardmc.noxesium.rules

import io.github.dockyardmc.noxesium.protocol.clientbound.ClientboundNoxesiumResetPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.viewable.Viewable

class NoxesiumRuleContainer : Viewable(), Disposable {
    override var autoViewable: Boolean = false

    private val _noxesiumRules: MutableMap<Int, NoxesiumServerRule<*>> = mutableMapOf()
    val noxesiumRules get() = _noxesiumRules.toMap()

    fun set(rule: NoxesiumServerRule<*>) {
        _noxesiumRules[rule.ruleIndex] = rule
        sendUpdate()
    }

    fun remove(type: Int) {
        _noxesiumRules.remove(type)
        sendUpdate()
    }

    fun remove(rule: NoxesiumRules.RuleFunction<*>) {
        _noxesiumRules.remove(rule.index)
    }

    fun sendUpdate() {
        viewers.forEach(::updateViewer)
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViewer(player: Player) {
        // Reset all server rules
        player.sendPacket(ClientboundNoxesiumResetPacket(0x01).getPluginMessagePacket())
        player.sendPacket(player.noxesiumIntegration.getRulesPacket().getPluginMessagePacket())
    }

    override fun addViewer(player: Player): Boolean {
        if (!super.addViewer(player)) return false
        if (!player.noxesiumIntegration.isUsingNoxesium.value) return false

        updateViewer(player)
        return true
    }

    override fun removeViewer(player: Player) {
        super.removeViewer(player)
        if (!player.noxesiumIntegration.isUsingNoxesium.value) return
        player.sendPacket(ClientboundNoxesiumResetPacket(0x01).getPluginMessagePacket())
    }

    override fun dispose() {
        _noxesiumRules.clear()
        clearViewers()
    }
}