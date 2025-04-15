package io.github.dockyardmc.advancement

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSelectAdvancementsTabPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateAdvancementsPacket
import io.github.dockyardmc.utils.Disposable
import kotlinx.datetime.Clock
import kotlin.collections.set

class PlayerAdvancementTracker(val player: Player) : Disposable {

    private val progress = mutableMapOf<String, MutableMap<String, Long>>()
    private val visible = mutableSetOf<Advancement>()

    /**
     * Currently selected advancement tab
     *
     * Setting to this will send the packet to client
     * and update the tab on their end
     */
    var selectedTab = Bindable<String?>(null)

    init {
        selectedTab.valueChanged {
            if (it.oldValue != it.newValue) {
                player.sendPacket(ClientboundSelectAdvancementsTabPacket(it.newValue))
            }
        }
    }

    fun grantAdvancement(advancement: Advancement) {
        val progress = progress[advancement.id] ?: return

        val timestamp = Clock.System.now().epochSeconds
        val changes: MutableMap<String, Long?> = mutableMapOf()

        advancement.requirements.flatten().forEach { criterion ->
            if (progress.put(criterion, timestamp) != timestamp) {
                changes[criterion] = timestamp
            }
        }

        this.player.sendPacket(makePacket(progress = mapOf(advancement.id to changes)))
    }

    fun revokeAdvancement(advancement: Advancement) {
        progress[advancement.id]?.clear()
        this.player.sendPacket(makePacket(progress = mapOf(advancement.id to mapOf())))
    }


    private fun setProgress(advId: String, criterion: String, timestamp: Long?) {
        val advProgress = progress[advId] ?: return

        if(timestamp != null) {
            advProgress[criterion] = timestamp
        } else {
            advProgress.remove(criterion)
        }

        player.sendPacket(makePacket(progress = mapOf(advId to mapOf(criterion to timestamp))))
    }

    /**
     * @return All progress of the advancement as a map
     * criteriaName -> timestamp?
     */
    fun getProgress(advId: String): Map<String, Long>? {
        synchronized(this.progress) {
            return this.progress[advId]?.toMap()
        }
    }

    /**
     * @return All progress of the advancement as a map
     * criteriaName -> timestamp?
     */
    fun getProgress(adv: Advancement) = getProgress(adv.id)

    /**
     * Get progress for a specific criterion of an advancement
     */
    fun getProgress(advId: String, criterion: String): Long? {
        synchronized(this.progress) {
            return this.progress[advId]?.get(criterion)
        }
    }

    /**
     * Get progress for a specific criterion of an advancement
     */
    fun getProgress(adv: Advancement, criterion: String) = getProgress(adv.id, criterion)

    fun grantCriterion(advId: String, criterion: String) {
        setProgress(advId, criterion, Clock.System.now().epochSeconds)
    }

    fun revokeCriterion(advId: String, criterion: String) {
        setProgress(advId, criterion, null)
    }

    internal fun onAdvancementAdded(adv: Advancement) {
        progress[adv.id] = mutableMapOf<String, Long>()

        this.visible.add(adv)
        this.player.sendPacket(makePacket(add = mapOf(adv.id to adv)))
    }

    internal fun onAdvancementRemoved(adv: Advancement) {
        this.visible.remove(adv)
        this.player.sendPacket(makePacket(remove = listOf(adv.id)))
    }

    fun makePacket(
        clear: Boolean = false,
        add: Map<String, Advancement> = mapOf(),
        remove: Collection<String> = listOf(),
        progress: Map<String, Map<String, Long?>> = mapOf()
    ): ClientboundUpdateAdvancementsPacket {
        return ClientboundUpdateAdvancementsPacket(
            clear, add, remove, progress
        )
    }

    override fun dispose() {
        visible.toList().forEach { advancement ->
            advancement.removeViewer(this.player)
        }
    }
}
