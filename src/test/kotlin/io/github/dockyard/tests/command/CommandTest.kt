package io.github.dockyard.tests.command

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyardmc.player.Player

abstract class CommandTest {

    fun run(command: String, assert: (Player) -> Unit) {
        PlayerTestUtil.getOrCreateFakePlayer().runCommand(command)
        assert.invoke(PlayerTestUtil.getOrCreateFakePlayer())
    }

}