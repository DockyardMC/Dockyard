package io.github.dockyardmc.maths.velocity

import cz.lukynka.bindables.BindableDispatcher
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.world.World

class VelocityPhysics(startLocation: Location, initialVelocity: Vector3f, val handlesCollision: Boolean = false) : Disposable {

    private val gravity: Vector3f = Vector3f(0f, -0.05f, 0f)
    private val airFriction: Vector3f = Vector3f(0.98f)
    private val groundFriction: Vector3f = Vector3f(0.1f, 0.98f, 0.1f)

    private var currentLocation: Location = startLocation
    private var currentVelocity: Vector3f = initialVelocity

    val onTick: BindableDispatcher<Location> = BindableDispatcher()
    private var running: Boolean = false

    private val schedulerTask = startLocation.world.scheduler.runRepeating(1.ticks) {
        if (!running) return@runRepeating

        var friction = airFriction
        if(currentLocation.subtract(0.0, 0.03, 0.0).block.registryBlock.isSolid) {
            friction = groundFriction
        }

        currentVelocity = currentVelocity + gravity
        currentVelocity = currentVelocity * friction

        val newLocXOnly = newLocation(currentVelocity.x, 0f, 0f, currentLocation.yaw, currentLocation.pitch, currentLocation.world)
        val newLocYOnly = newLocation(0f, currentVelocity.y, 0f, currentLocation.yaw, currentLocation.pitch, currentLocation.world)
        val newLocZOnly = newLocation(0f, 0f, currentVelocity.z, currentLocation.yaw, currentLocation.pitch, currentLocation.world)

        if (newLocXOnly.block.registryBlock.isSolid) {
            currentVelocity.x = 0f
        }
        if (newLocYOnly.block.registryBlock.isSolid) {
            currentVelocity.y = 0f
        }
        if (newLocZOnly.block.registryBlock.isSolid) {
            currentVelocity.z = 0f
        }

        val newLocFull = newLocation(currentVelocity.x, currentVelocity.y, currentVelocity.z, currentLocation.yaw, currentLocation.pitch, currentLocation.world)
        if (handlesCollision && newLocFull.block.registryBlock.isSolid) {
            currentVelocity = Vector3f(0f, 0f, 0f)
            return@runRepeating
        }

        if (!handlesCollision || !newLocFull.block.registryBlock.isSolid) {
            currentLocation = Location(
                currentLocation.x + currentVelocity.x,
                currentLocation.y + currentVelocity.y,
                currentLocation.z + currentVelocity.z,
                currentLocation.yaw,
                currentLocation.pitch,
                currentLocation.world
            )
        }

        if (currentVelocity.isZero) {
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

    private fun newLocation(newX: Float, newY: Float, newZ: Float, yaw: Float, pitch: Float, world: World): Location {
        return Location(
            currentLocation.x + newX,
            currentLocation.y + newY,
            currentLocation.z + newZ,
            yaw,
            pitch,
            world
        )
    }
}