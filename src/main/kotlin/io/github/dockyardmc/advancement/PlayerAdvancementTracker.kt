package io.github.dockyardmc.advancement

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.EventListener
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerLoadedEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSelectAdvancementsTabPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateAdvancementsPacket
import io.github.dockyardmc.utils.Disposable
import kotlinx.datetime.Clock
import kotlin.collections.set

class PlayerAdvancementTracker(val player: Player) : Disposable {

    private val progress = mutableMapOf<String, MutableMap<String, Long?>>()
    private val visibleAdvancements = mutableSetOf<String>()
    private val updatedProgress = mutableMapOf<String, MutableSet<String>>()

    /**
     * Currently selected advancement tab
     *
     * Setting to this will send the packet to client
     * and update the tab on their end
     */
    var selectedTab: String? = null
        set(value) {
            if (field != value) {
                player.sendPacket(ClientboundSelectAdvancementsTabPacket(value))
            }
            field = value
        }

    init {
        var listener: EventListener<Event>? = null

        listener = Events.on<PlayerLoadedEvent> {
            sendToClient()
            Events.unregister(listener!!)
        }
    }

    fun grantAdvancement(advId: String): Boolean {
        return setAllCriteriaTo(advId, Clock.System.now().epochSeconds)
    }

    /**
     * Whether the advancement was revoked
     */
    fun revokeAdvancement(advId: String): Boolean {
        return setAllCriteriaTo(advId, null)
    }

    private fun setAllCriteriaTo(advId: String, timestamp: Long?): Boolean {
        val keys: Collection<String>? = synchronized(progress) {
            progress[advId]?.keys
        }
        var changed = false

        keys?.forEach { criterion ->
            changed = setProgress(advId, criterion, timestamp) || changed
        }
        if (changed) {
            sendToClient()
        }
        return changed
    }

    /**
     * @return whether something changed or not
     */
    private fun setProgress(advId: String, criterion: String, timestamp: Long?): Boolean {
        synchronized(this.progress) {
            val adv = progress[advId] ?: return false
            if (adv[criterion] == timestamp) return false

            adv[criterion] = timestamp
        }

        synchronized(updatedProgress) {
            updatedProgress.getOrPut(advId) {
                mutableSetOf()
            }.add(criterion)
        }

        return true
    }

    /**
     * @return whether something changed or not
     */
    fun grantCriterion(advId: String, criterion: String): Boolean {
        val changed = setProgress(advId, criterion, Clock.System.now().epochSeconds)
        if (changed) {
            sendToClient()
        }
        return changed
    }

    /**
     * @return whether something changed or not
     */
    fun revokeCriterion(advId: String, criterion: String): Boolean {
        val changed = setProgress(advId, criterion, null)
        if (changed) {
            sendToClient()
        }
        return changed
    }

    private fun sendToClient() = DockyardServer.scheduler.run {
        if (!player.isFullyInitialized) return@run

        val add = mutableMapOf<String, Advancement>()
        val remove = mutableSetOf<String>()

        val advancements = AdvancementManager.advancements

        synchronized(visibleAdvancements) {
            advancements.forEach { (id, adv) ->
                if (visibleAdvancements.add(id)) {
                    add[id] = adv
                }
            }
            visibleAdvancements.forEach {
                if (!advancements.containsKey(it)) {
                    visibleAdvancements.remove(it)
                    remove.add(it)
                }
            }
        }

        val updatedProgress: MutableMap<String, MutableSet<String>>
        synchronized(this.updatedProgress) {
            updatedProgress = this.updatedProgress.toMutableMap()
            this.updatedProgress.clear()
        }
        val progress: Map<String, Map<String, Long?>> = synchronized(this.progress) {
            updatedProgress.mapValues { (key, value) ->
                value.associateWith { this.progress[key]?.get(it) }
            }
        }

        if (add.isEmpty() && remove.isEmpty() && progress.isEmpty()) return@run

        player.sendPacket(
            ClientboundUpdateAdvancementsPacket(
                false, add, remove, progress
            )
        )
    }

    internal fun onAdvancementAdded(adv: Advancement) {
        synchronized(progress) {
            progress[adv.id] = mutableMapOf()

            adv.requirements.flatten().forEach { req: String ->
                progress[adv.id]!![req] = null
            }
        }
    }

    internal fun onAdvancementRemoved(adv: Advancement) {
        synchronized(progress) {
            progress.remove(adv.id)
        }
        synchronized(updatedProgress) {
            updatedProgress.remove(adv.id)
        }
        sendToClient()
    }

    override fun dispose() {
        AdvancementManager.removeAdvancementTracker(this)
    }
}
