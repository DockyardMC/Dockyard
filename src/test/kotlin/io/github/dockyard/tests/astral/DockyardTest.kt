package io.github.dockyard.tests.astral

import cz.lukynka.astral.AstralTest
import cz.lukynka.bindables.BindablePool
import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.scheduler.runLaterAsync
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import kotlin.time.Duration

abstract class DockyardTest : AstralTest() {

    protected val eventPool = EventPool()
    protected val bindablePool = BindablePool()
    protected val player get() = PlayerTestUtil.getOrCreateFakePlayer()
    protected val mainWorld: World get() = WorldManager.mainWorld
    private val lastEvent: Event? = null

    abstract fun testSpecificSetup()
    abstract fun testSpecificCleanup()

    override fun setup() {
        TestServer.getOrSetupServer()
    }

    override fun cleanup() {
        eventPool.dispose()
        bindablePool.dispose()
    }

    inline fun <reified T : Event> addWaitForEvent(crossinline unit: (T) -> Unit) {
        var eventCalled: Boolean = false
        val pool = EventPool()
        pool.on<T> { event ->
            unit.invoke(event)
            eventCalled = true
            runLaterAsync(1.ticks) {
                pool.dispose()
            }
        }

        `access$addWaitUntil`("Wait for ${T::class.simpleName}", Duration.INFINITE) { eventCalled }
    }

    inline fun <reified T : Event> addWaitForEvent(timeout: Duration, crossinline unit: (T) -> Unit) {
        var eventCalled: Boolean = false
        val pool = EventPool()
        pool.on<T> { event ->
            unit.invoke(event)
            eventCalled = true
            runLaterAsync(1.ticks) {
                pool.dispose()
            }
        }

        `access$addWaitUntil`("Wait for ${T::class.simpleName}", timeout) { eventCalled }
    }

    @PublishedApi
    internal fun `access$addWaitUntil`(name: String, timeout: Duration, unit: () -> Boolean) = addWaitUntil(name, timeout, unit)


}