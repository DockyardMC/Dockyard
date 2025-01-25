package io.github.dockyardmc.apis

import cz.lukynka.Bindable
import cz.lukynka.BindablePool
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.Guardian
import io.github.dockyardmc.entity.Squid
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.scheduler.SchedulerTask
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.team.TeamCollisionRule
import io.github.dockyardmc.team.TeamManager
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.Viewable
import io.github.dockyardmc.utils.locationLerp
import io.github.dockyardmc.utils.percent
import java.util.UUID
import kotlin.math.round
import kotlin.time.Duration

class GuardianBeam(start: Location, end: Location): Viewable(), Disposable {

    private companion object {
        val NO_COLLISION_TEAM = TeamManager.create("beam_no_collision_${UUID.randomUUID()}", LegacyTextColor.WHITE)

        init {
            NO_COLLISION_TEAM.teamCollisionRule.value = TeamCollisionRule.NEVER
        }
    }

    private val bindablePool = BindablePool()

    val start: Bindable<Location> = bindablePool.provideBindable(start)
    val end: Bindable<Location> = bindablePool.provideBindable(end)

    private val guardianEntity: Guardian = start.world.spawnEntity(Guardian(start)) as Guardian
    private val targetEntity: Squid = start.world.spawnEntity(Squid(end)) as Squid

    private var currentStartMoveTask: SchedulerTask? = null
    private var currentEndMoveTask: SchedulerTask? = null

    override var autoViewable: Boolean = false // doesnt even work

    init {
        targetEntity.isInvisible.value = true
        guardianEntity.isInvisible.value = true

        targetEntity.team.value = NO_COLLISION_TEAM
        guardianEntity.team.value = NO_COLLISION_TEAM

        guardianEntity.target.value = targetEntity

        this.start.valueChanged { change ->
            guardianEntity.teleport(change.newValue)
        }

        this.end.valueChanged { change ->
            targetEntity.teleport(change.newValue)
        }
    }

    override fun addViewer(player: Player) {
        targetEntity.addViewer(player)
        guardianEntity.addViewer(player)
    }

    override fun removeViewer(player: Player) {
        targetEntity.removeViewer(player)
        guardianEntity.removeViewer(player)
    }

    override fun dispose() {
        start.value.world.despawnEntity(targetEntity)
        start.value.world.despawnEntity(guardianEntity)
        bindablePool.dispose()
    }

    fun moveEnd(newLocation: Location, interpolation: Duration = 0.ticks) {
        val scheduler = start.value.world.scheduler
        val totalTicks = round(interpolation.inWholeMilliseconds / 50f).toInt()

        currentEndMoveTask?.cancel()

        var currentTick = 0
        currentEndMoveTask = scheduler.runRepeating(1.ticks) {
            currentTick++
            if(currentTick == totalTicks) currentEndMoveTask?.cancel()

            val time = percent(totalTicks, currentTick) / 100f
            val loc = locationLerp(targetEntity.location, newLocation, time)
            end.value = loc
        }
    }

    fun moveStart(newLocation: Location, interpolation: Duration = 0.ticks) {
        val scheduler = start.value.world.scheduler
        val totalTicks = round(interpolation.inWholeMilliseconds / 50f).toInt()

        currentStartMoveTask?.cancel()

        var currentTick = 0
        currentStartMoveTask = scheduler.runRepeating(1.ticks) {
            currentTick++
            if(currentTick == totalTicks) currentStartMoveTask?.cancel()

            val time = percent(totalTicks, currentTick) / 100f
            val loc = locationLerp(guardianEntity.location, newLocation, time)
            start.value = loc
        }
    }

    fun cancelMovement() {
        currentStartMoveTask?.cancel()
        currentEndMoveTask?.cancel()
        currentStartMoveTask = null
        currentEndMoveTask = null
    }
}