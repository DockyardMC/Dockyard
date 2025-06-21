package io.github.dockyardmc.world.waypoint

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindablePool
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerChangeWorldEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTrackedWaypointPacket
import io.github.dockyardmc.protocol.types.Either
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.viewable.Viewable
import java.util.*

class Waypoint(initialLocation: Location, val id: Either<UUID, String> = Either.left(UUID.randomUUID())) : Viewable(), Disposable {

    override var autoViewable: Boolean = false

    val eventPool = EventPool()
    val bindablePool = BindablePool()
    val location: Bindable<Location> = bindablePool.provideBindable(initialLocation)
    val icon: Bindable<WaypointData.Icon> = bindablePool.provideBindable(WaypointData.Icon.DEFAULT)

    private var cachedWaypointData = WaypointData(id, icon.value, WaypointData.Vec3(location.value))

    init {
        eventPool.on<PlayerChangeWorldEvent> { event ->
            val player = event.player
            if (!viewers.contains(player)) return@on
            if (event.newWorld != location.value.world) removeViewer(player)
        }

        eventPool.on<PlayerLeaveEvent> { event ->
            val player = event.player
            if (!viewers.contains(player)) return@on
            removeViewer(player)
        }

        location.valueChanged { event ->
            update()
            if (event.newValue.world != event.oldValue.world) {
                viewers.forEach { viewer ->
                    if (viewer.world != event.newValue.world) {
                        removeViewer(viewer)
                    }
                }
            }
        }

        icon.valueChanged {
            update()
        }
    }

    override fun addViewer(player: Player): Boolean {
        if (player.world != location.value.world) return false
        return if (super.addViewer(player)) {
            player.sendPacket(ClientboundTrackedWaypointPacket(ClientboundTrackedWaypointPacket.Operation.TRACK, cachedWaypointData))
            true
        } else {
            false
        }
    }

    override fun removeViewer(player: Player) {
        super.removeViewer(player)
        player.sendPacket(ClientboundTrackedWaypointPacket(ClientboundTrackedWaypointPacket.Operation.UNTRACK, cachedWaypointData))
    }

    private fun update() {
        cachedWaypointData = WaypointData(id, icon.value, WaypointData.Vec3(location.value))
        viewers.sendPacket(ClientboundTrackedWaypointPacket(ClientboundTrackedWaypointPacket.Operation.TRACK, cachedWaypointData))
    }

    override fun dispose() {
        clearViewers()
        eventPool.dispose()
        bindablePool.dispose()
    }
}