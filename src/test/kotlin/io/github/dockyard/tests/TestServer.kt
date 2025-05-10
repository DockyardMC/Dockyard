package io.github.dockyard.tests

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.VoidWorldGenerator
import org.junit.jupiter.api.BeforeAll
import java.util.concurrent.CountDownLatch

object TestServer {

    private var server: DockyardServer? = null
    lateinit var testWorld: World

    fun getServer(): DockyardServer {
        return server ?: throw IllegalStateException("Server is null")
    }

    fun getOrSetupServer(): DockyardServer {
        if (server == null) beforeAll()
        return getServer()
    }

    @BeforeAll
    @JvmStatic
    fun beforeAll() {
        server = DockyardServer {
            withIp("0.0.0.0")
            withPort(25565)
            withNetworkCompressionThreshold(-1)
            useMojangAuth(false)
            withUpdateChecker(false)
            withImplementations { spark = false }
            useDebugMode(true)
        }
        server!!.start()

        val mainWorldCountdownLatch = CountDownLatch(1)
        val secondWorldCountDownLatch = CountDownLatch(1)

        testWorld = WorldManager.create("test", VoidWorldGenerator(Biomes.THE_VOID), DimensionTypes.OVERWORLD)

        testWorld.schedule { world ->
            secondWorldCountDownLatch.countDown()
        }

        WorldManager.mainWorld.schedule { world ->
            mainWorldCountdownLatch.countDown()
        }

        mainWorldCountdownLatch.await()
        secondWorldCountDownLatch.await()

        PlayerTestUtil.getOrCreateFakePlayer()
    }
}

