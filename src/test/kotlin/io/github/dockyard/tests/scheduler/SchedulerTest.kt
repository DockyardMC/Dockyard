package io.github.dockyard.tests.scheduler

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.scheduler.CustomRateScheduler
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class SchedulerTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @AfterTest
    fun cleanup() {
    }


    @Test
    fun testScheduler() {
        val scheduler = CustomRateScheduler()
        val completed = AtomicBoolean(false)

        assertEquals(false, completed.get())
        assertEquals(0, scheduler.ticks)

        scheduler.runLater(1.ticks) {
            completed.set(true)
        }

        scheduler.tick()

        assertEquals(true, completed.get())
        assertEquals(1, scheduler.ticks)
        scheduler.dispose()
    }

    @Test
    fun testSchedulerAsync() {
        val scheduler = CustomRateScheduler()
        val completed = AtomicBoolean(false)
        val countDownLatch = CountDownLatch(1)

        assertEquals(false, completed.get())
        assertEquals(0, scheduler.ticks)

        scheduler.runLaterAsync(1.ticks) {
            completed.set(true)
            countDownLatch.countDown()
        }

        scheduler.tick()
        countDownLatch.await()

        assertEquals(true, completed.get())
        assertEquals(1, scheduler.ticks)
        scheduler.dispose()
    }

    @Test
    fun testRepeatingScheduler() {
        val scheduler = CustomRateScheduler()
        val counter = AtomicInteger(0)
        val countDownLatch = CountDownLatch(1)

        val repeatingTask = scheduler.runRepeating(1.ticks) {
            log("Ticking: ${counter.incrementAndGet()}", LogType.DEBUG)
        }

        scheduler.runLater(5.seconds) {
            repeatingTask.cancel()
            countDownLatch.countDown()
        }

        countDownLatch.await()
        assertEquals(true, repeatingTask.cancelled)
        assertEquals(100, counter.get())
        assertEquals(100, scheduler.ticks)
    }
}