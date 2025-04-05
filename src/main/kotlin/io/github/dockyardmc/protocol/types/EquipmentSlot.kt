package io.github.dockyardmc.protocol.types

enum class EquipmentSlot {
    MAIN_HAND,
    BOOTS,
    LEGGINGS,
    CHESTPLATE,
    HELMET,
    OFF_HAND,
    BODY,
    SADDLE;

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