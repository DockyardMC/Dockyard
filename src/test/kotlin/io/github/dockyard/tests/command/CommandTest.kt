package io.github.dockyard.tests.command

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyardmc.player.Player

abstract class CommandTest {

    inline fun run(command: String, assert: (Player) -> Unit) {
        PlayerTestUtil.getOrCreateFakePlayer().runCommand(command)
        assert.invoke(PlayerTestUtil.getOrCreateFakePlayer())
    }

}