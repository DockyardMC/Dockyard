package io.github.dockyardmc.noxesium.rules

import io.github.dockyardmc.noxesium.protocol.clientbound.ClientboundNoxesiumChangeServerRulesPacket
import io.github.dockyardmc.noxesium.protocol.clientbound.ClientboundNoxesiumResetPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.viewable.Viewable
import io.netty.buffer.ByteBuf

class NoxesiumRuleContainer : Viewable(), Disposable {
    override var autoViewable: Boolean = false

    private val noxesiumRules: MutableMap<Int, NoxesiumServerRule<*>> = mutableMapOf()

    operator fun set(type: Int, rule: NoxesiumServerRule<*>) {
        noxesiumRules[type] = rule
        sendUpdate()
    }

    operator fun set(type: Int, rule: NoxesiumRules.RuleFunction<*>) {
        this[type] = rule.rule.invoke(type)
    }

    fun remove(type: Int) {
        noxesiumRules.remove(type)
        sendUpdate()
    }

    fun sendUpdate() {
        viewers.forEach(::updateViewer)
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViewer(player: Player) {
        // Reset all server rules
        player.sendPacket(ClientboundNoxesiumResetPacket(0x01).getPluginMessagePacket())

        val writers = noxesiumRules.mapValues { (_, rule) -> { buffer: ByteBuf -> (rule as NoxesiumServerRule<Any?>).write(rule.value, buffer) } }
        player.sendPacket(ClientboundNoxesiumChangeServerRulesPacket(writers).getPluginMessagePacket())
    }

    override fun addViewer(player: Player): Boolean {
        if (!super.addViewer(player)) return false
        updateViewer(player)
        return true
    }

    override fun removeViewer(player: Player) {
        super.removeViewer(player)
        player.sendPacket(ClientboundNoxesiumResetPacket(0x01).getPluginMessagePacket())
    }

    override fun dispose() {
        noxesiumRules.clear()
        clearViewers()
    }
}