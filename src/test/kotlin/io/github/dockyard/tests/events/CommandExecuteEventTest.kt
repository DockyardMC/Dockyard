package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.commands.Command
import io.github.dockyardmc.commands.CommandArgument
import io.github.dockyardmc.commands.CommandExecutor
import io.github.dockyardmc.commands.CommandHandler
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EntityArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.events.CommandExecuteEvent
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.utils.Console
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CommandExecuteEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<CommandExecuteEvent> {
            count.countDown()
        }

        Commands.add("real_command_that_would_definitely_exist_in_non_testing_environment") {
            addArgument("probably_a_pig", StringArgument())
        }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        CommandHandler.handleCommandInput(
            "/real_command_that_would_definitely_exist_in_non_testing_environment minecraft:pig",
            CommandExecutor(player, Console, "", true),
            true
        )

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}