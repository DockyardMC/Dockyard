package io.github.dockyard.tests.entity

import io.github.dockyardmc.player.systems.GameMode
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class GameModeTest {

    @Test
    fun toId() {
        assertEquals(GameMode.SURVIVAL.ordinal, 0)
        assertEquals(GameMode.CREATIVE.ordinal, 1)
        assertEquals(GameMode.ADVENTURE.ordinal, 2)
        assertEquals(GameMode.SPECTATOR.ordinal, 3)
    }

}