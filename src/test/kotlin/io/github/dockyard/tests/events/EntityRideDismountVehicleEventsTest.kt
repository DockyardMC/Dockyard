package io.github.dockyard.tests.events

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.entity.Parrot
import io.github.dockyardmc.events.EntityDismountVehicleEvent
import io.github.dockyardmc.events.EntityRideVehicleEvent
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.registry.DamageTypes
import io.github.dockyardmc.registry.registries.DamageType
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

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

        pool.on<EntityRideVehicleEvent> {
            mountCount.countDown()
        }
        pool.on<EntityDismountVehicleEvent> {
            dismountCount.countDown()
        }

        val vehicle = EntityManager.spawnEntity(Parrot(TestServer.testWorld.locationAt(0,0,0)))
        val entity = EntityManager.spawnEntity(Parrot(TestServer.testWorld.locationAt(0,0,0)))

        vehicle.passengers.add(entity)
        assertTrue(mountCount.await(5L, TimeUnit.SECONDS))

        entity.dismountCurrentVehicle()
        assertTrue(dismountCount.await(5L, TimeUnit.SECONDS))

        pool.dispose()
        // two birds one dispose
        vehicle.dispose()
        entity.dispose()
    }
}