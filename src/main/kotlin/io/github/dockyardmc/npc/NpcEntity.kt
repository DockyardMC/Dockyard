package io.github.dockyardmc.npc

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerDamageEntityEvent
import io.github.dockyardmc.events.PlayerInteractWithEntityEvent
import io.github.dockyardmc.events.PlayerMoveEvent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.team.Team
import io.github.dockyardmc.team.TeamManager

abstract class NpcEntity(location: Location) : Entity(location) {

    val nametagVisible: Bindable<Boolean> = Bindable(true)
    val hasCollision: Bindable<Boolean> = Bindable(true)
    val teamColor: Bindable<LegacyTextColor> = Bindable(LegacyTextColor.WHITE)
    var lookClose: LookCloseType = LookCloseType.NONE

    private var onRightClick: ((PlayerInteractWithEntityEvent) -> Unit)? = null
    private var onDamage: ((PlayerDamageEntityEvent) -> Unit)? = null

    val eventPool = EventPool()

    val npcTeam = TeamManager.create("npc-$uuid", teamColor.value, getTeamNametagVisibility(), getTeamCollision())

    fun onRightClick(event: ((PlayerInteractWithEntityEvent) -> Unit)) {
        onRightClick = event
    }

    fun onDamage(event: ((PlayerDamageEntityEvent) -> Unit)) {
        onDamage = event
    }

    private fun getTeamNametagVisibility(): Team.NameTagVisibility {
        return if (nametagVisible.value) Team.NameTagVisibility.VISIBLE else Team.NameTagVisibility.HIDDEN
    }

    private fun getTeamCollision(): Team.CollisionRule {
        return if (hasCollision.value) Team.CollisionRule.ALWAYS else Team.CollisionRule.NEVER
    }

    init {
        eventPool.on<PlayerMoveEvent> {
            if (lookClose == LookCloseType.NONE) return@on
            if (it.player.location.distance(location) <= 5) {
                if (lookClose == LookCloseType.NORMAL) lookAt(it.player) else lookAtClientside(it.player, it.player)
            }
        }

        eventPool.on<PlayerDamageEntityEvent> {
            if(it.entity != this) return@on
            onDamage?.invoke(it)
        }

        eventPool.on<PlayerInteractWithEntityEvent> {
            if(it.entity != this) return@on
            onRightClick?.invoke(it)
        }

        nametagVisible.valueChanged {
            npcTeam.nameTagVisibility.value = getTeamNametagVisibility(); team.value = npcTeam
        }
        hasCollision.valueChanged { npcTeam.collisionRule.value = getTeamCollision(); team.value = npcTeam }
        teamColor.valueChanged { npcTeam.color.value = it.newValue }
    }

    override fun dispose() {
        eventPool.dispose()
        TeamManager.remove(npcTeam)
        super.dispose()
    }
}