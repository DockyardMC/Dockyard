package io.github.dockyardmc.noxesium.rules

import com.noxcrew.noxesium.api.util.DebugOption
import io.github.dockyardmc.item.ItemStack
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

    fun setCameraLocked(value: Boolean) {
        set(NoxesiumRules.Server.CAMERA_LOCKED.createRule(value))
    }

    fun setHeldItemOffset(value: Int) {
        set(NoxesiumRules.Server.HELD_ITEM_NAME_OFFSET.createRule(value))
    }

    fun disableVanillaMusic(value: Boolean) {
        set(NoxesiumRules.Server.DISABLE_VANILLA_MUSIC.createRule(value))
    }

    fun showMapUi(value: Boolean) {
        set(NoxesiumRules.Server.SHOW_MAP_IN_UI.createRule(value))
    }

    fun disableMapUi(value: Boolean) {
        set(NoxesiumRules.Server.DISABLE_MAP_UI.createRule(value))
    }

    fun disableBoatCollision(value: Boolean) {
        set(NoxesiumRules.Server.DISABLE_BOAT_COLLISION.createRule(value))
    }

    fun customCreativeItems(value: List<ItemStack>) {
        set(NoxesiumRules.Server.CUSTOM_CREATIVE_ITEMS.createRule(value))
    }

    fun disableDefferedChunkUpdates(value: Boolean) {
        set(NoxesiumRules.Server.DISABLE_DEFFERED_CHUNK_UPDATES.createRule(value))
    }

    fun overrideGraphicsMode(value: NoxesiumRules.Server.GraphicsType) {
        set(NoxesiumRules.Server.OVERRIDE_GRAPHICS_MODE.createRule(value))
    }

    fun setRiptideCoyoteTime(value: Int) {
        set(NoxesiumRules.Server.RIPTIDE_COYOTE_TIME.createRule(value))
    }

    fun setRiptidePreCharging(value: Boolean) {
        set(NoxesiumRules.Server.RIPTIDE_PRE_CHARGING.createRule(value))
    }

    fun restrictDebugOptions(value: List<DebugOption>) {
        set(NoxesiumRules.Server.RESTRICT_DEBUG_OPTIONS.createRule(value.map { it.keyCode }))
    }

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