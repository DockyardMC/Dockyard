package io.github.dockyard.tests.entity

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.systems.GameMode
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerTests {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @AfterTest
    fun cleanup() {
    }

    @Test
    fun testAbilities() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()

        player.gameMode.value = GameMode.CREATIVE
        assertAbilities(player, isInvulnerable = true, isFlying = false, canFly = true)
        player.gameMode.value = GameMode.SPECTATOR
        assertAbilities(player, isInvulnerable = true, isFlying = true, canFly = true)
        player.gameMode.value = GameMode.CREATIVE
        assertAbilities(player, isInvulnerable = true, isFlying = true, canFly = true)
        player.gameMode.value = GameMode.ADVENTURE
        assertAbilities(player, isInvulnerable = false, isFlying = false, canFly = false)
        player.gameMode.value = GameMode.SURVIVAL
        assertAbilities(player, isInvulnerable = false, isFlying = false, canFly = false)

    }

    private fun assertAbilities(player: Player, isInvulnerable: Boolean, isFlying: Boolean, canFly: Boolean) {
        assertEquals(isInvulnerable, player.isInvulnerable)
        assertEquals(isFlying, player.isFlying.value)
        assertEquals(canFly, player.canFly.value)
    }
}