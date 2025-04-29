package io.github.dockyardmc.utils

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.EntityMetadata
import io.github.dockyardmc.entity.EntityMetadataType
import io.github.dockyardmc.entity.getEntityMetadataState
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.toPersistent
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.SocketException
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and
import kotlin.reflect.KClass

fun isValidMinecraftUsername(username: String): Boolean {
    if(username.isEmpty()) return false
    if(username.contains(" ")) return false
    if(!username.matches(Regex("^[a-zA-Z0-9]*$"))) return false
    if(username.length <= 2) return false
    if(username.length > 16) return false

    return true
}

fun now(): Long {
    return System.currentTimeMillis()
}

fun bitMask(mask: Byte, compare: Byte): Boolean {
    return mask and compare != (0).toByte()
}

fun bitMask(mask: Byte, compare: Int): Boolean {
    return bitMask(mask, compare.toByte())
}

fun ticksToMs(ticks: Int): Int = ticks * 50

fun getEnumEntries(enumClass: KClass<Enum<*>>): List<Enum<*>> = enumClass.java.enumConstants.toList()

fun Entity.setGlowingFor(player: Player, state: Boolean) {
    val entityState = getEntityMetadataState(this) {
        isGlowing = state
    }
    val playerMetadataLayer = this.metadataLayers[player.toPersistent()] ?: mutableMapOf<EntityMetadataType, EntityMetadata>()

    playerMetadataLayer[EntityMetadataType.STATE] = entityState
    this.metadataLayers[player.toPersistent()] = playerMetadataLayer
}

fun Entity.setInvisibleFor(player: Player, state: Boolean) {
    val entityState = getEntityMetadataState(this) {
        isInvisible = state
    }
    val playerMetadataLayer = this.metadataLayers[player.toPersistent()] ?: mutableMapOf<EntityMetadataType, EntityMetadata>()

    playerMetadataLayer[EntityMetadataType.STATE] = entityState
    this.metadataLayers[player.toPersistent()] = playerMetadataLayer
}

fun Entity.setIsOnFireFor(player: Player, state: Boolean) {
    val entityState = getEntityMetadataState(this) {
        isOnFire = state
    }
    val playerMetadataLayer = this.metadataLayers[player.toPersistent()] ?: mutableMapOf<EntityMetadataType, EntityMetadata>()

    playerMetadataLayer[EntityMetadataType.STATE] = entityState
    this.metadataLayers[player.toPersistent()] = playerMetadataLayer
}

fun Entity.setIsCrouchingFor(player: Player, state: Boolean) {
    val entityState = getEntityMetadataState(this) {
        isCrouching = state
    }
    val playerMetadataLayer = this.metadataLayers[player.toPersistent()] ?: mutableMapOf<EntityMetadataType, EntityMetadata>()

    playerMetadataLayer[EntityMetadataType.STATE] = entityState
    this.metadataLayers[player.toPersistent()] = playerMetadataLayer
}


fun generateSHA1(input: String): String {
    val messageDigest = MessageDigest.getInstance("SHA-1")
    val bytes = messageDigest.digest(input.toByteArray())

    val hexString = StringBuilder()
    for (byte in bytes) {
        val hex = Integer.toHexString(byte.toInt() and 0xff)
        if (hex.length == 1) {
            hexString.append('0')
        }
        hexString.append(hex)
    }

    return hexString.toString()

}

fun isAddressInUse(host: String, port: Int): Boolean {
    try {
        val socket = ServerSocket()
        socket.bind(InetSocketAddress(host, port))
        socket.close()
    } catch (exception: SocketException) {
        val message = exception.message ?: ""
        if (message.contains("Address already in use")) {
            return true;
        }
    }

    return false
}

fun intArrayToUuid(array: IntArray): UUID {
    val uuidMost = array[0].toLong() shl 32 or (array[1].toLong() and 0xFFFFFFFFL)
    val uuidLeast = array[2].toLong() shl 32 or (array[3].toLong() and 0xFFFFFFFFL)

    return UUID(uuidMost, uuidLeast)
}

fun uuidToIntArray(uuid: UUID): IntArray {
    val uuidMost = uuid.mostSignificantBits
    val uuidLeast = uuid.leastSignificantBits
    return intArrayOf(
        (uuidMost shr 32).toInt(),
        uuidMost.toInt(),
        (uuidLeast shr 32).toInt(),
        uuidLeast.toInt()
    )
}