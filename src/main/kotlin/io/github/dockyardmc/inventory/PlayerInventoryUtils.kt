package io.github.dockyardmc.inventory

object PlayerInventoryUtils {
    const val OFFSET: Int = 9

    const val CRAFT_RESULT: Int = 36
    const val CRAFT_SLOT_1: Int = 37
    const val CRAFT_SLOT_2: Int = 38
    const val CRAFT_SLOT_3: Int = 39
    const val CRAFT_SLOT_4: Int = 40

    const val HELMET_SLOT: Int = 41
    const val CHESTPLATE_SLOT: Int = 42
    const val LEGGINGS_SLOT: Int = 43
    const val BOOTS_SLOT: Int = 44
    const val OFFHAND_SLOT: Int = 45

    fun convertPlayerInventorySlot(slot: Int, offset: Int): Int {
        return when (slot) {
            0 -> CRAFT_RESULT
            1 -> CRAFT_SLOT_1
            2 -> CRAFT_SLOT_2
            3 -> CRAFT_SLOT_3
            4 -> CRAFT_SLOT_4
            5 -> HELMET_SLOT
            6 -> CHESTPLATE_SLOT
            7 -> LEGGINGS_SLOT
            8 -> BOOTS_SLOT
            else -> PlayerInventoryUtils.convertSlot(slot, offset)
        }
    }

    fun convertSlot(slot: Int, offset: Int): Int {
        var properSlot = slot
        val rowSize = 9
        properSlot -= offset
        properSlot = if (properSlot >= rowSize * 3 && properSlot < rowSize * 4) {
            properSlot % 9
        } else {
            properSlot + rowSize
        }
        return properSlot
    }

    fun convertToPacketSlot(slot: Int): Int {
        var properSlot = slot
        if (properSlot > -1 && properSlot < 9) { // Held bar 0-8
            properSlot = properSlot + 36
        } else if (properSlot > 8 && properSlot < 36) { // Inventory 9-35
            properSlot = properSlot
        } else if (properSlot >= CRAFT_RESULT && properSlot <= CRAFT_SLOT_4) { // Crafting 36-40
            properSlot = properSlot - 36
        } else if (properSlot >= HELMET_SLOT && properSlot <= BOOTS_SLOT) { // Armor 41-44
            properSlot = properSlot - 36
        } else if (properSlot == OFFHAND_SLOT) { // Off hand
            properSlot = 45
        }
        return properSlot
    }

    fun convertClientInventorySlot(slot: Int): Int {
        if (slot == 36) return BOOTS_SLOT
        if (slot == 37) return LEGGINGS_SLOT
        if (slot == 38) return CHESTPLATE_SLOT
        if (slot == 39) return HELMET_SLOT
        if (slot == 40) return OFFHAND_SLOT
        return slot
    }
}