@file:Suppress("ArrayInDataClass")

package io.github.dockyardmc.schematics

import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.utils.Vector3

data class Schematic(
    var size: Vector3,
    var offset: Vector3,
    var pallete: MutableList<Block>,
    var blocks: ByteArray,
) {

    companion object {
        val empty = Schematic(Vector3(), Vector3(), mutableListOf(), ByteArray(0))

    }
}

enum class SchematicRotation {
    CLOCKWISE_90,
    CLOCKWISE_180,
    CLOCKWISE_270,
    CLOCKWISE_360
}