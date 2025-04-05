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

    fun removeAdvancementTracker(tracker: PlayerAdvancementTracker) {
        synchronized(innerTrackers) {
            innerTrackers.remove(tracker)
        }
    }

    /**
     * @return same advancement that you passed as the argument,
     * with no changes
     */
    fun addAdvancement(adv: Advancement): Advancement {
        synchronized(innerAdvancements) {
            innerAdvancements[adv.id] = adv
        }

        synchronized(innerTrackers) {
            innerTrackers.forEach {
                it.onAdvancementAdded(adv)
            }
        }

        return adv
    }

    fun removeAdvancement(adv: Advancement) {
        synchronized(innerAdvancements) {
            innerAdvancements.remove(adv.id)
        }

        synchronized(innerTrackers) {
            innerTrackers.forEach { it.onAdvancementRemoved(adv) }
        }
    }
}
