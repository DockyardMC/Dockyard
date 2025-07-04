package io.github.dockyard.tests.scheduler

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.scheduler.CustomRateScheduler
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

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
        val scheduler = CustomRateScheduler("test-scheduler")
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
        val scheduler = CustomRateScheduler("test-scheduler")
        val completed = AtomicBoolean(false)
        val countDownLatch = CountDownLatch(1)

        assertEquals(false, completed.get())
        assertEquals(0, scheduler.ticks)

        scheduler.runLaterAsync(1.ticks) {
            completed.set(true)
            countDownLatch.countDown()
        }

        scheduler.tick()
        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS))

        assertEquals(true, completed.get())
        assertEquals(1, scheduler.ticks)
        scheduler.dispose()
    }

    @Test
    fun testRepeatingScheduler() {
        val scheduler = CustomRateScheduler("test-scheduler")
        val counter = AtomicInteger(0)
        val countDownLatch = CountDownLatch(1)

        val repeatingTask = scheduler.runRepeating(1.ticks) {
            log("Ticking: ${counter.incrementAndGet()}", LogType.DEBUG)
        }

        scheduler.runLater(20.ticks) {
            repeatingTask.cancel()
            countDownLatch.countDown()
        }

        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS))
        assertEquals(true, repeatingTask.cancelled)
        assertEquals(20, counter.get())
        assertEquals(20, scheduler.ticks)
    }
}