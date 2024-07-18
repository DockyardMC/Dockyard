package io.github.dockyardmc.player

enum class PlayerHand {
    MAIN_HAND,
    OFF_HAND
}

enum class PlayerAction {
    SNEAKING_START,
    SNEAKING_STOP,
    LEAVE_BED,
    SPRINTING_START,
    SPRINTING_END,
    HORSE_JUMP_START,
    HORSE_JUMP_END,
    VEHICLE_INVENTORY_OPEN,
    ELYTRA_FLYING_START
}

enum class EntityPose {
    STANDING,
    FALL_FLYING,
    SLEEPING,
    SWIMMING,
    SPIN_ATTACK,
    SNEAKING,
    LONG_JUMPING,
    DYING,
    CROAKING,
    USING_TONGUE,
    SITTING,
    ROARING,
    SNIFFING,
    EMERGING,
    DIGGING,
    SLIDING,
    SHOOTING,
    INHALING;
}


enum class DisplayedSkinPart(val bit: Int) {
    CAPE(0x01),
    JACKET(0x02),
    LEFT_SLEEVE(0x04),
    RIGHT_SLEEVE(0x08),
    LEFT_PANTS(0x10),
    RIGHT_PANTS(0x20),
    HAT(0x40),
    UNUSED(0x80)
}

fun List<DisplayedSkinPart>.getBitMask(): Int {
    var out = 0x00
    this.forEach { out += it.bit }
    return out
}

enum class Direction {
    DOWN,
    UP,
    NORTH,
    SOUTH,
    WEST,
    EAST
}