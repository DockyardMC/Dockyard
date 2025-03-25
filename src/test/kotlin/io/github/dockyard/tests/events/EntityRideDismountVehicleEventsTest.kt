package io.github.dockyard.tests.events

import cz.lukynka.prettylog.log
import io.github.dockyard.tests.TestFor
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.Parrot
import io.github.dockyardmc.events.EntityDismountVehicleEvent
import io.github.dockyardmc.events.EntityRideVehicleEvent
import io.github.dockyardmc.events.EventPool
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@TestFor(EntityRideVehicleEvent::class, EntityDismountVehicleEvent::class)
class EntityRideDismountVehicleEventsTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventsFire() {
        val pool = EventPool()
        val mountCount = CountDownLatch(1)
        val dismountCount = CountDownLatch(1)
        log("a")

        pool.on<EntityRideVehicleEvent> {
            log("ride")
            mountCount.countDown()
        }
        pool.on<EntityDismountVehicleEvent> {
            log("dismount")
            dismountCount.countDown()
        }

        log("trying to spawn entity")

        val vehicle = TestServer.testWorld.spawnEntity(Parrot(TestServer.testWorld.locationAt(0,0,0)))
        val entity = TestServer.testWorld.spawnEntity(Parrot(TestServer.testWorld.locationAt(1,0,0)))

        log("spawned entities")

        vehicle.passengers.add(entity)
        assertTrue(mountCount.await(5L, TimeUnit.SECONDS))

        entity.dismountCurrentVehicle()
        assertTrue(dismountCount.await(5L, TimeUnit.SECONDS))

        pool.dispose()
        // two birds one dispose
        TestServer.testWorld.despawnEntity(vehicle)
        TestServer.testWorld.despawnEntity(entity)
    }
}