package io.github.dockyardmc.bounds

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.entities.EntityManager
import io.github.dockyardmc.events.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.utils.CustomDataHolder
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.Vector3f
import java.lang.IllegalArgumentException

class Bound(
    var firstLocation: Location,
    var secondLocation: Location,
): Disposable {
    val world get() = firstLocation.world
    val size: Vector3 get() = firstLocation.distanceVector(secondLocation).toVector3()

    private val members: MutableList<Player> = mutableListOf()
    val players: List<Player> get() = members.toList()

    private var onEnter: ((player: Player) -> Unit)? = null
    private var onLeave: ((player: Player) -> Unit)? = null

    val metadata: CustomDataHolder = CustomDataHolder()

    val eventPool = EventPool()


    fun onEnter(unit: (player: Player) -> Unit) {
        onEnter = unit
    }

    fun onLeave(unit: (player: Player) -> Unit) {
        onLeave = unit
    }

    fun getBlocks(): Map<Location, Block> {
        val allBlocks = mutableMapOf<Location, Block>()
        val first = firstLocation.getFullLocation()
        val second = secondLocation.getFullLocation()
        for (iX in first.x.toInt()..second.x.toInt()) {
            for (iY in first.y.toInt()..second.y.toInt()) {
                for (iZ in first.z.toInt()..second.z.toInt()) {
                    allBlocks[Location(iX, iY, iZ, world)] = world.getBlock(iX, iY, iZ)
                }
            }
        }
        return allBlocks.toMap()
    }

    fun resize(newFirstLocation: Location, newSecondLocation: Location) {
        if(newFirstLocation.world != newSecondLocation.world) throw IllegalArgumentException("The two locations cannot be in different worlds (${firstLocation.world.name} - ${secondLocation.world.name})")
        this.firstLocation = newFirstLocation.getFullLocation()
        this.secondLocation = newSecondLocation.getFullLocation()
    }

    fun getEntities(): List<Entity> {
        val entities = mutableListOf<Entity>()
        EntityManager.entities.toList().forEach { if (it.location.isWithinBound(this)) entities.add(it) }
        return entities
    }

    init {
        resize(firstLocation, secondLocation)

        eventPool.on<ServerTickEvent> {
            firstLocation.world.spawnParticle(firstLocation, Particles.ELECTRIC_SPARK, Vector3f(0f), 0f, 1)
            secondLocation.world.spawnParticle(secondLocation, Particles.ELECTRIC_SPARK, Vector3f(0f), 0f, 1)

            PlayerManager.players.toList().forEach { player ->
                if(player.world != world) return@forEach
                if(player.location.isWithinBound(this) && !members.contains(player)) {
                    val event = PlayerEnterBoundEvent(player, this)
                    Events.dispatch(event)

                    if(event.cancelled) {
                        player.teleport(player.location)
                        return@on
                    }

                    members.add(player)
                    onEnter?.invoke(player)
                } else if(!player.location.isWithinBound(this) && members.contains(player)) {
                    val event = PlayerLeaveBoundEvent(player, this)
                    Events.dispatch(event)

                    if(event.cancelled) {
                        player.teleport(player.location)
                        return@on
                    }

                    members.remove(player)
                    onLeave?.invoke(player)
                }
            }
        }
    }

    override fun dispose() {
        eventPool.dispose()
        onEnter = null
        onLeave = null
    }
}