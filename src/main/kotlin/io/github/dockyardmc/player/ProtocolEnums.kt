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
    DIGGING
}

enum class Direction {
    DOWN,
    UP,
    NORTH,
    SOUTH,
    WEST,
    EAST
}