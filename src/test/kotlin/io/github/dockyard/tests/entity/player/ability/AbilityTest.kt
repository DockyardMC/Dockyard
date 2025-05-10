package io.github.dockyard.tests.entity.player.ability

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.astral.DockyardTest
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.systems.GameMode

class AbilityTest : DockyardTest() {


    override fun testSpecificSetup() {
    }

    override fun testSpecificCleanup() {
    }

    override fun createTestSteps() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()

        addStep("Set gamemode to Spectator") { player.gameMode.value = GameMode.SPECTATOR }
        assertAbilities(player, isInvulnerable = true, isFlying = true, canFly = true)

        addStep("Set gamemode to Creative") { player.gameMode.value = GameMode.CREATIVE }
        assertAbilities(player, isInvulnerable = true, isFlying = true, canFly = true)

        addStep("Set gamemode to Adventure") { player.gameMode.value = GameMode.ADVENTURE }
        assertAbilities(player, isInvulnerable = false, isFlying = false, canFly = false)

        addStep("Set gamemode to Survival") { player.gameMode.value = GameMode.SURVIVAL }

        assertAbilities(player, isInvulnerable = false, isFlying = false, canFly = true)

    }

    private fun assertAbilities(player: Player, isInvulnerable: Boolean, isFlying: Boolean, canFly: Boolean) {
        addAssert("Is Invulnerable is $isInvulnerable") { player.isInvulnerable == isInvulnerable }
        addAssert("Is Flying is $isFlying") { player.isFlying.value == isFlying }
        addAssert("Can Fly is $canFly") { player.canFly.value == canFly }
    }
}