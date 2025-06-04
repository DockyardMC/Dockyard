package io.github.dockyardmc.maths.velocity

import cz.lukynka.bindables.BindableDispatcher
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.utils.Disposable

class VelocityPhysics(startLocation: Location, initialVelocity: Vector3f, val handlesCollision: Boolean = false) : Disposable {

    private val gravity: Vector3f = Vector3f(0f, -0.05f, 0f)
    private val friction: Vector3f = Vector3f(0.98f)

    private var currentLocation: Location = startLocation
    private var currentVelocity: Vector3f = initialVelocity

    val onTick: BindableDispatcher<Location> = BindableDispatcher()
    private var running: Boolean = false

    private val schedulerTask = startLocation.world.scheduler.runRepeating(1.ticks) {
        if (!running) return@runRepeating
        currentVelocity = currentVelocity + gravity
        currentVelocity = currentVelocity * friction

        currentLocation = Location(
            currentLocation.x + currentVelocity.x,
            currentLocation.y + currentVelocity.y,
            currentLocation.z + currentVelocity.z,
            currentLocation.yaw,
            currentLocation.pitch,
            currentLocation.world
        )

        if(!currentLocation.block.isAir()) {
            dispose()
            return@runRepeating
        }

        onTick.dispatch(currentLocation)
    }

    fun start() {
        running = true
    }

    fun stop() {
        running = false
    }

    override fun dispose() {
        schedulerTask.cancel()
        onTick.dispose()
    }
}