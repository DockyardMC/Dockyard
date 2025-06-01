package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.DataComponentHashable

enum class EquipmentSlot(val nbtName: String): DataComponentHashable {
    MAIN_HAND("mainhand"),
    OFF_HAND("offhand"),
    BOOTS("feet"),
    LEGGINGS("legs"),
    CHESTPLATE("chest"),
    HELMET("head"),
    BODY("body"),
    SADDLE("saddle");

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofString(nbtName))
    }

    companion object {
        fun isBody(equipmentSlot: EquipmentSlot?): Boolean {
            if (equipmentSlot == null) return false
            if (equipmentSlot == MAIN_HAND) return false
            if (equipmentSlot == OFF_HAND) return false
            if (equipmentSlot == SADDLE) return false
            return true
        }
    }
}