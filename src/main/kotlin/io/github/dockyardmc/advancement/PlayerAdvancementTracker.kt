package io.github.dockyardmc.advancement

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.EventListener
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerLoadedEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateAdvancementsPacket
import io.github.dockyardmc.utils.Disposable
import kotlinx.datetime.Clock
import kotlin.collections.set

class PlayerAdvancementTracker(val player: Player) : Disposable {

    private val progress = mutableMapOf<String, MutableMap<String, Long?>>()
    private val visibleAdvancements = mutableSetOf<String>()
    private val updatedProgress = mutableMapOf<String, MutableSet<String>>()

    var selectedTab: String? = null

    init {
        var listener: EventListener<Event>? = null

        listener = Events.on<PlayerLoadedEvent> {
            sendToClient()
            Events.unregister(listener!!)
        }
    }

    fun grantAdvancement(advId: String) {
        val adv = progress[advId] ?: return

        adv.keys.forEach { it: String ->
            if (adv[it] == null) {
                setProgress(advId, it, Clock.System.now().epochSeconds)
            }
        }
        if (updatedProgress.isNotEmpty()) {
            sendToClient()
        }
    }

    private fun setProgress(advId: String, criterion: String, timestamp: Long?) {
        synchronized(this.progress) {
            val adv = progress[advId] ?: return
            if (adv[criterion] == timestamp) return

            adv[criterion] = timestamp
        }

        synchronized(updatedProgress) {
            updatedProgress.getOrPut(advId) {
                mutableSetOf()
            }
                .add(criterion)
        }
    }

    fun grantCriterion(advId: String, criterion: String) {
        val adv = progress[advId] ?: return
        if (adv[criterion] != null) return

        setProgress(advId, criterion, Clock.System.now().epochSeconds)
        sendToClient()
    }

    fun sendToClient() = DockyardServer.scheduler.run {
        if (!player.isFullyInitialized) return@run

        val add = mutableMapOf<String, Advancement>()
        val remove = mutableSetOf<String>()

        val advancements = AdvancementManager.advancements

        synchronized(visibleAdvancements) {
            advancements.forEach { id, adv ->
                if (visibleAdvancements.add(id)) {
                    add.put(id, adv)
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

        if(add.isEmpty() && remove.isEmpty() && progress.isEmpty())
            return@run

        player.sendPacket(
            ClientboundUpdateAdvancementsPacket(
                false,
                add,
                remove,
                progress
            )
        )
    }

    fun onAdvancementAdded(adv: Advancement) {
        synchronized(progress) {
            progress[adv.id] = mutableMapOf()

            adv.requirements.flatten().forEach { req: String ->
                progress[adv.id]!![req] = null
            }
        }
    }

    fun onAdvancementRemoved(adv: Advancement) {
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
