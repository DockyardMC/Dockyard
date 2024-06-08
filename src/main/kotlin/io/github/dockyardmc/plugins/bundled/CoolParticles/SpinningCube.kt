package io.github.dockyardmc.plugins.bundled.CoolParticles

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import kotlin.math.cos
import kotlin.math.sin

class SpinningCube {

    var enabled = false

    val spinningCubeCenter = Location(0, 207, 0)
    val world = WorldManager.worlds[0]

    val cubeSize = 3.0f
    val vertices = arrayOf(
        Vector3f(-cubeSize, -cubeSize, -cubeSize),
        Vector3f(cubeSize, -cubeSize, -cubeSize),
        Vector3f(cubeSize, cubeSize, -cubeSize),
        Vector3f(-cubeSize, cubeSize, -cubeSize),
        Vector3f(-cubeSize, -cubeSize, cubeSize),
        Vector3f(cubeSize, -cubeSize, cubeSize),
        Vector3f(cubeSize, cubeSize, cubeSize),
        Vector3f(-cubeSize, cubeSize, cubeSize)
    )

    var angleX = 0.0
    var angleY = 0.0

    val edges = arrayOf(
        Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 0), // bottom
        Pair(4, 5), Pair(5, 6), Pair(6, 7), Pair(7, 4), // top
        Pair(0, 4), Pair(1, 5), Pair(2, 6), Pair(3, 7)  // verticla
    )

    fun register() {

        Commands.add("/cube") {
            it.execute { exec ->
                enabled = !enabled
                val message = when(enabled) {
                    true -> "<lime>Spinning cube demo enabled"
                    else -> "<red>Spinning cube demo disabled"
                }
                exec.sendMessage(message)
            }
        }

        Events.on<ServerTickEvent> {

            if(!enabled) return@on

            angleX += 0.03f
            angleY += 0.03f

            val cosX = cos(angleX).toFloat()
            val sinX = sin(angleX).toFloat()
            val rotationMatrixX = arrayOf(
                arrayOf(1.0f, 0.0f, 0.0f),
                arrayOf(0.0f, cosX, -sinX),
                arrayOf(0.0f, sinX, cosX)
            )

            val cosY = cos(angleY).toFloat()
            val sinY = sin(angleY).toFloat()
            val rotationMatrixY = arrayOf(
                arrayOf(cosY, 0.0f, -sinY),
                arrayOf(0.0f, 1.0f, 0.0f),
                arrayOf(sinY, 0.0f, cosY)
            )

            val rotatedVertices = vertices.map { vertex -> rotateVertex(vertex, rotationMatrixX, rotationMatrixY) }

            for ((startIdx, endIdx) in edges) {
                val start = rotatedVertices[startIdx]
                val end = rotatedVertices[endIdx]
                drawLine(spinningCubeCenter.clone().add(start), spinningCubeCenter.clone().add(end), world)
            }
        }
    }


    fun drawLine(start: Location, end: Location, world: World) {
        val distance = start.distance(end)
        val direction = Vector3f(
            ((end.x - start.x) / distance).toFloat(),
            ((end.y - start.y) / distance).toFloat(),
            ((end.z - start.z) / distance).toFloat()
        )
        var currentLocation = start.clone()
        val step = 0.1f

        for (i in 0..(distance / step).toInt()) {
            if(enabled) world.spawnParticle(currentLocation, Particles.ELECTRIC_SPARK, count = 1, speed = 0f)
            currentLocation = currentLocation.add(Vector3f(direction.x * step, direction.y * step, direction.z * step))
        }
    }

    fun rotateVertex(vertex: Vector3f, rotationMatrixX: Array<Array<Float>>, rotationMatrixY: Array<Array<Float>>): Vector3f {
        val intermediateResult = arrayOf(
            vertex.x * rotationMatrixX[0][0] + vertex.y * rotationMatrixX[0][1] + vertex.z * rotationMatrixX[0][2],
            vertex.x * rotationMatrixX[1][0] + vertex.y * rotationMatrixX[1][1] + vertex.z * rotationMatrixX[1][2],
            vertex.x * rotationMatrixX[2][0] + vertex.y * rotationMatrixX[2][1] + vertex.z * rotationMatrixX[2][2]
        )
        val x = intermediateResult[0] * rotationMatrixY[0][0] + intermediateResult[1] * rotationMatrixY[0][1] + intermediateResult[2] * rotationMatrixY[0][2]
        val y = intermediateResult[0] * rotationMatrixY[1][0] + intermediateResult[1] * rotationMatrixY[1][1] + intermediateResult[2] * rotationMatrixY[1][2]
        val z = intermediateResult[0] * rotationMatrixY[2][0] + intermediateResult[1] * rotationMatrixY[2][1] + intermediateResult[2] * rotationMatrixY[2][2]
        return Vector3f(x, y, z)
    }
}