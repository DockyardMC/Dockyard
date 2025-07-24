package io.github.dockyardmc.player.systems

import com.noxcrew.noxesium.api.util.DebugOption
import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.noxesium.Noxesium
import io.github.dockyardmc.noxesium.getWriters
import io.github.dockyardmc.noxesium.protocol.clientbound.ClientboundNoxesiumChangeServerRulesPacket
import io.github.dockyardmc.noxesium.protocol.clientbound.ClientboundNoxesiumResetExtraEntityDataPacket
import io.github.dockyardmc.noxesium.protocol.clientbound.ClientboundNoxesiumSetExtraEntityDataPacket
import io.github.dockyardmc.noxesium.rules.NoxesiumEntityRuleContainer
import io.github.dockyardmc.noxesium.rules.NoxesiumEntityRuleContainer.Companion.ALL_ENTITY_INDICES
import io.github.dockyardmc.noxesium.rules.NoxesiumRuleContainer
import io.github.dockyardmc.noxesium.rules.NoxesiumRules
import io.github.dockyardmc.player.Player

class NoxesiumIntegration(val player: Player) : PlayerSystem {

    val rulesContainer: NoxesiumRuleContainer = NoxesiumRuleContainer()
    val entityRulesContainer: NoxesiumEntityRuleContainer = NoxesiumEntityRuleContainer()
    val isUsingNoxesium: Bindable<Boolean> = player.bindablePool.provideBindable(false)

    private var scheduledActions: MutableList<() -> Unit> = mutableListOf()

    fun schedule(unit: () -> Unit) {
        if (!isUsingNoxesium.value) {
            scheduledActions.add(unit)
            return
        }
        unit.invoke()
    }

    init {
        isUsingNoxesium.valueChangedThenSelfDispose { event ->
            if (event.newValue) {
                scheduledActions.forEach { action -> action.invoke() }
            }
        }

        schedule {
            rulesContainer.addViewer(player)
            entityRulesContainer.addViewer(player)
        }
    }

    fun cameraLocked(value: Boolean) {
        rulesContainer.set(NoxesiumRules.Server.CAMERA_LOCKED.createRule(value))
    }

    fun heldItemOffset(value: Int) {
        rulesContainer.set(NoxesiumRules.Server.HELD_ITEM_NAME_OFFSET.createRule(value))
    }

    fun vanillaMusic(value: Boolean) {
        rulesContainer.set(NoxesiumRules.Server.DISABLE_VANILLA_MUSIC.createRule(value))
    }

    fun showMapUi(value: Boolean) {
        rulesContainer.set(NoxesiumRules.Server.SHOW_MAP_IN_UI.createRule(value))
    }

    fun disableMapUi(value: Boolean) {
        rulesContainer.set(NoxesiumRules.Server.DISABLE_MAP_UI.createRule(value))
    }

    fun disableBoatCollision(value: Boolean) {
        rulesContainer.set(NoxesiumRules.Server.DISABLE_BOAT_COLLISION.createRule(value))
    }

//    fun customCreativeItems(value: List<ItemStack>) {
//        rules.set(NoxesiumRules.Server.CUSTOM_CREATIVE_ITEMS.createRule(value))
//    }

    fun disableDefferedChunkUpdates(value: Boolean) {
        rulesContainer.set(NoxesiumRules.Server.DISABLE_DEFFERED_CHUNK_UPDATES.createRule(value))
    }

    fun overrideGraphicsMode(value: NoxesiumRules.Server.GraphicsType) {
        rulesContainer.set(NoxesiumRules.Server.OVERRIDE_GRAPHICS_MODE.createRule(value))
    }

    fun riptideCoyoteTime(value: Int) {
        rulesContainer.set(NoxesiumRules.Server.RIPTIDE_COYOTE_TIME.createRule(value))
    }

    fun riptidePreCharging(value: Boolean) {
        rulesContainer.set(NoxesiumRules.Server.RIPTIDE_PRE_CHARGING.createRule(value))
    }

    fun restrictDebugOptions(value: List<DebugOption>) {
        rulesContainer.set(NoxesiumRules.Server.RESTRICT_DEBUG_OPTIONS.createRule(value.map { it.keyCode }))
    }

    fun getRulesPacket(): ClientboundNoxesiumChangeServerRulesPacket {
        val mergedRules = Noxesium.globalRuleContainer.noxesiumRules.toMutableMap()
        this.rulesContainer.noxesiumRules.forEach { rule ->
            mergedRules[rule.key] = rule.value
        }

        return ClientboundNoxesiumChangeServerRulesPacket(mergedRules.getWriters())
    }

    fun getEntityRulesResetPackets(): Collection<ClientboundNoxesiumResetExtraEntityDataPacket> {
        val packets = mutableListOf<ClientboundNoxesiumResetExtraEntityDataPacket>()
        val mergedEntities = mutableSetOf<Entity>()
        mergedEntities.addAll(this.entityRulesContainer.entityToRulesMap.keys)
        mergedEntities.addAll(Noxesium.globalEntityRuleContainer.entityToRulesMap.keys)

        mergedEntities.forEach { entity ->
            packets.add(ClientboundNoxesiumResetExtraEntityDataPacket(entity.id, ALL_ENTITY_INDICES))
        }
        return packets
    }

    fun getEntityRulesPackets(): Collection<ClientboundNoxesiumSetExtraEntityDataPacket> {
        val mergedRules = Noxesium.globalEntityRuleContainer.entityToRulesMap.mapValues { it.value.toMutableMap() }.toMutableMap() // what the fuck

        broadcastMessage("<orange>Base $mergedRules")

        this.entityRulesContainer.entityToRulesMap.forEach { (entity, rules) ->
            broadcastMessage("<lime>Merging $rules")
            val entityRules = mergedRules.getOrPut(entity) { mutableMapOf() }
            entityRules.putAll(rules)
        }

        broadcastMessage("<pink>$mergedRules")
        return mergedRules.map { (entity, rules) ->
            ClientboundNoxesiumSetExtraEntityDataPacket(entity.id, rules.getWriters())
        }
    }


    override fun dispose() {
        rulesContainer.dispose()
        entityRulesContainer.dispose()
        isUsingNoxesium.dispose()
    }
}