package io.github.dockyardmc.noxesium.rules

import com.noxcrew.noxesium.api.protocol.rule.EntityRuleIndices
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.noxesium.toPluginMessagePackets
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.viewable.Viewable

class NoxesiumEntityRuleContainer : Disposable, Viewable() {
    private val _entityToRulesMap: MutableMap<Entity, MutableMap<Int, NoxesiumServerRule<*>>> = mutableMapOf()
    val entityToRulesMap get() = _entityToRulesMap.toMap()
    override var autoViewable: Boolean = false

    companion object {
        val ALL_ENTITY_INDICES = listOf(
            EntityRuleIndices.BEAM_COLOR,
            EntityRuleIndices.BEAM_COLOR_FADE,
            EntityRuleIndices.CUSTOM_GLOW_COLOR,
            EntityRuleIndices.DISABLE_BUBBLES,
            EntityRuleIndices.QIB_BEHAVIOR,
            EntityRuleIndices.QIB_WIDTH_Z,
        )
    }

    private fun updateViewers() {
        viewers.forEach { viewer ->
            viewer.noxesiumIntegration.getEntityRulesResetPackets().toPluginMessagePackets().forEach(viewer::sendPacket)
            viewer.noxesiumIntegration.getEntityRulesPackets().toPluginMessagePackets().forEach(viewer::sendPacket)
        }
    }

    override fun addViewer(player: Player): Boolean {
        if (!super.addViewer(player)) return false
        if (!player.noxesiumIntegration.isUsingNoxesium.value) return false

        val entityRulesPacket = player.noxesiumIntegration.getEntityRulesPackets()
        entityRulesPacket.toPluginMessagePackets().forEach(player::sendPacket)

        return true
    }

    override fun removeViewer(player: Player) {
        super.removeViewer(player)
        if (!player.noxesiumIntegration.isUsingNoxesium.value) return
        player.noxesiumIntegration.getEntityRulesResetPackets().toPluginMessagePackets().forEach(player::sendPacket)
    }

    operator fun set(entity: Entity, rule: NoxesiumServerRule<*>) {
        val existingRules = _entityToRulesMap[entity] ?: mutableMapOf()
        existingRules[rule.ruleIndex] = rule
        _entityToRulesMap[entity] = existingRules
        updateViewers()
    }

    fun removeRule(entity: Entity, rule: NoxesiumServerRule<*>) {
        val existingRules = _entityToRulesMap[entity] ?: mutableMapOf()
        existingRules.remove(rule.ruleIndex)
        _entityToRulesMap[entity] = existingRules
        updateViewers()
    }

    fun removeEntity(entity: Entity) {
        _entityToRulesMap.remove(entity)
        updateViewers()
    }

    fun disableBubbles(entity: Entity, value: Boolean) {
        set(entity, NoxesiumRules.Entity.DISABLE_BUBBLES.createRule(value))
    }

    fun setCustomGlowColor(entity: Entity, value: CustomColor?) {
        set(entity, NoxesiumRules.Entity.CUSTOM_GLOW_COLOR.createRule(value))
    }

    fun setCustomBeamColor(entity: Entity, value: CustomColor?) {
        set(entity, NoxesiumRules.Entity.BEAM_COLOR.createRule(value))
    }

    fun setQIBBehaviour(entity: Entity, value: String) {
        set(entity, NoxesiumRules.Entity.QIB_BEHAVIOUR.createRule(value))
    }

    fun setQIBInteractionWidthZ(entity: Entity, value: Double) {
        set(entity, NoxesiumRules.Entity.QIB_INTERACTION_WIDTH_Z.createRule(value))
    }

    fun setBeamFadeColor(entity: Entity, value: CustomColor) {
        set(entity, NoxesiumRules.Entity.BEAM_FADE_COLOR.createRule(value))
    }

    override fun dispose() {
        this.clearViewers()
        _entityToRulesMap.clear()
    }
}

