package io.github.dockyardmc.location

import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.Vector2f
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.world.Chunk
import io.github.dockyardmc.world.World
import io.netty.buffer.ByteBuf
import kotlin.math.atan2
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

class Location(
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float = 0f,
    var pitch: Float = 0f,
    var world: World,
) {
    constructor(
        x: Int,
        y: Int,
        z: Int,
        yaw: Float = 0f,
        pitch: Float = 0f,
        world: World,
    ): this(x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch, world)

    constructor(
        x: Int,
        y: Int,
        z: Int,
        world: World,
    ): this(x.toDouble(), y.toDouble(), z.toDouble(), 0f, 0f, world)

    constructor(
        x: Double,
        y: Double,
        z: Double,
        world: World,
    ): this(x, y, z, 0f, 0f, world)

    val blockX: Int get() = floor(x).toInt()
    val blockY: Int get() = floor(y).toInt()
    val blockZ: Int get() = floor(z).toInt()

    fun getAdjacentLocations(): List<Location> {
        return listOf(
            getBlockLocation().add(1, 0, 0),
            getBlockLocation().add(-1, 0, 0),
            getBlockLocation().add(0, 1, 0),
            getBlockLocation().add(0, -1, 0),
            getBlockLocation().add(0, 0, 1),
            getBlockLocation().add(0, 0, -1),
        )
    }

    override fun toString(): String =
        "Location(${x.truncate(2)}, ${y.truncate(2)}, ${z.truncate(2)}, yaw: $yaw, pitch: $pitch, world: ${world.name})"

    fun toBlockString(): String {
        return "${this.blockX}${this.blockY}${this.blockZ}"
    }

    fun equalsBlock(location: Location): Boolean {
        return this.toBlockString() == location.toBlockString()
    }

    fun getChunk(): Chunk? = world.getChunkAt(this)

    fun add(vector: Vector3f): Location =
        Location(this.x + vector.x, this.y + vector.y, this.z + vector.z, this.yaw, this.pitch, this.world)

    fun add(x: Int, y: Int, z: Int): Location = Location(this.x + x, this.y + y, this.z + z, this.yaw, this.pitch, this.world)

    fun add(x: Double, y: Double, z: Double): Location = Location(this.x + x, this.y + y, this.z + z, this.yaw, this.pitch, this.world)

    fun add(vector: Vector3): Location =
        Location(this.x + vector.x, this.y + vector.y, this.z + vector.z, this.yaw, this.pitch, this.world)

    fun add(location: Location): Location =
        Location(this.x + location.x, this.y + location.y, this.z + location.z, this.yaw, this.pitch, this.world)

    fun clone(): Location = Location(this.x, this.y, this.z, this.yaw, this.pitch, this.world)

    fun distance(other: Location): Double =
        //surly it's not just me that pronounces it "squirt"
        sqrt((this.x - other.x).pow(2.0) + (this.y - other.y).pow(2.0) + (this.z - other.z).pow(2.0))

    fun getBlockLocation(): Location = Location(blockX, blockY, blockZ, world)

    fun getRotation(): Vector2f = Vector2f(yaw, pitch)

    fun setDirection(vector: Vector3f): Location {
        val loc = this.clone()
        val x = vector.x.toDouble()
        val y = vector.y.toDouble()
        val z = vector.z.toDouble()

        if (x == 0.0 && z == 0.0) {
            loc.yaw = if (y > 0.0) -90.0f else 90.0f
            return loc
        }

        loc.pitch = Math.toDegrees(atan2(-x, z)).toFloat()
        loc.yaw = Math.toDegrees(atan2(-y, sqrt(x * x + z * z))).toFloat()
        return loc
    }

    fun subtract(vec: Location): Location {
        val loc = this.clone()
        loc.x -= vec.x
        loc.y -= vec.y
        loc.z -= vec.z
        return loc
    }

    fun subtract(vector: Vector3f): Location {
        val loc = this.clone()
        loc.x -= vector.x
        loc.y -= vector.y
        loc.z -= vector.z
        return loc
    }

    fun subtract(vector: Vector3): Location {
        val loc = this.clone()
        loc.x -= vector.x
        loc.y -= vector.y
        loc.z -= vector.z
        return loc
    }

    fun subtract(x: Double, y: Double, z: Double): Location {
        val loc = this.clone()
        loc.x -= x
        loc.y -= y
        loc.z -= z
        return loc
    }

    fun subtract(x: Int, y: Int, z: Int): Location {
        val loc = this.clone()
        loc.x -= x
        loc.y -= y
        loc.z -= z
        return loc
    }

    fun withNoRotation(): Location = this.clone().apply { yaw = 0f; pitch = 0f }
    fun length(): Double = sqrt(x * x + y * y + z * z)
    fun getBlock(): Block = world.getBlock(this)
}

fun Block.isEmpty(): Boolean {
    return this.defaultBlockStateId == Blocks.AIR.defaultBlockStateId
}

fun ByteBuf.writeLocation(location: Location, rotDelta: Boolean = false) {
    this.writeLocationWithoutRot(location)
    if(rotDelta) {
        this.writeByte((location.pitch  * 256f / 360f).toInt())
        this.writeByte((location.yaw  * 256f / 360f).toInt())
    } else {
        this.writeByte(location.yaw.toInt())
        this.writeByte(location.pitch.toInt())
    }
}

fun ByteBuf.writeLocationWithoutRot(location: Location) {
    this.writeDouble(location.x)
    this.writeDouble(location.y)
    this.writeDouble(location.z)
}