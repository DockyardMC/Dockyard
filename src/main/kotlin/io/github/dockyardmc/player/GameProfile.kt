package io.github.dockyardmc.player

import java.util.UUID

class GameProfile(
    val uuid: UUID,
    val name: String,
    val properties: MutableList<ProfileProperty>
) {
}