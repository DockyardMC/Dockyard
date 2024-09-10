package io.github.dockyardmc.utils

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.entities.EntityMetadata
import io.github.dockyardmc.entities.EntityMetadataType
import io.github.dockyardmc.entities.getEntityMetadataState
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.toPersistent
import java.security.MessageDigest
import kotlin.reflect.KClass

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