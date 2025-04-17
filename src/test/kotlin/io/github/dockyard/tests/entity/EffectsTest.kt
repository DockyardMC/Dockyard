package io.github.dockyard.tests.entity

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class EffectsTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @AfterTest
    fun cleanup() {
        PlayerTestUtil.getOrCreateFakePlayer().clearPotionEffects()
    }

    @Test
    fun testEffectDurations() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()

        assertEquals(0, player.potionEffects.size)

        val map = mutableMapOf<Duration, Int>(
            1.seconds to 20,
            50.milliseconds to 1,
            10.minutes to 12000,
            Duration.INFINITE to -1,
        )

        map.forEach { (duration, ticks) ->
            player.addPotionEffect(PotionEffects.SPEED, duration, 1)
            assertEquals(ticks, player.potionEffects[PotionEffects.SPEED]!!.settings.duration.inWholeMinecraftTicks)
            PlayerTestUtil.getOrCreateFakePlayer().clearPotionEffects()
            assertEquals(0, player.potionEffects.size)
        }
    }
}