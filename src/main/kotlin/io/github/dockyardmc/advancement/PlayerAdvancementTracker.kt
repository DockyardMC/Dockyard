package io.github.dockyardmc.advancement

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.EventListener
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerLoadedEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateAdvancementsPacket
import kotlinx.datetime.Clock
import kotlin.collections.set

class PlayerAdvancementTracker(val player: Player) {

    private val progress = mutableMapOf<String, MutableMap<String, Long?>>()
    private val visibleAdvancements = mutableSetOf<String>()

    var selectedTab: String? = null

    init {
        var listener: EventListener<Event>? = null

        listener = Events.on<PlayerLoadedEvent> {
            sendToClient()
            Events.unregister(listener!!)
        }
    }

    fun grantAdvancement(advId: String) {
        progress[advId]?.keys?.forEach {
            grantCriteria(advId, it)
        }
        sendToClient()
    }

    fun updateCriteria(advId: String, criteria: String, timestamp: Long?) {
        val adv = progress[advId] ?: return

        adv[criteria] = timestamp
    }

    fun grantCriteria(advId: String, criteria: String) {
        val advProgress = progress[advId] ?: return
        if (advProgress[criteria] != null) return

        updateCriteria(advId, criteria, Clock.System.now().epochSeconds)
        sendToClient()
    }

    fun sendToClient() = DockyardServer.scheduler.run {
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

        val progress = synchronized(this.progress) {
            this.progress.toMap()
        }

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
        sendToClient()
    }
}
