package io.github.dockyardmc.advancement

import io.github.dockyardmc.player.Player

object AdvancementManager {
    private val innerTrackers = mutableListOf<PlayerAdvancementTracker>()
    private val innerAdvancements = mutableMapOf<String, Advancement>()

    val trackers get() = synchronized(innerTrackers) { innerTrackers.toList() }
    val advancements get() = synchronized(innerAdvancements) { innerAdvancements.toMap() }

    fun createAdvancementTracker(player: Player): PlayerAdvancementTracker {
        val tracker = PlayerAdvancementTracker(player)

        synchronized(innerTrackers) {
            innerTrackers.add(tracker)
        }

        synchronized(innerAdvancements) {
            innerAdvancements.values.forEach(tracker::onAdvancementAdded)
        }

        return tracker
    }

    fun addAdvancement(adv: Advancement) {
        synchronized(innerAdvancements) {
            innerAdvancements[adv.id] = adv
        }

        synchronized(innerTrackers) {
            innerTrackers.forEach {
                it.onAdvancementAdded(adv)
            }
        }
    }
}
