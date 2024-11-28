package io.github.dockyard.tests

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.WorldFinishLoadingEvent
import io.github.dockyardmc.world.WorldManager
import org.junit.jupiter.api.BeforeAll
import java.lang.IllegalStateException
import java.util.concurrent.CountDownLatch

object TestServer {

    private var server: DockyardServer? = null
    val countdownLatch = CountDownLatch(1)

    fun getServer(): DockyardServer {
        return server ?: throw IllegalStateException("Server is null")
    }

    fun getOrSetupServer(): DockyardServer {
        if(server == null) beforeAll()
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
        }
        server!!.start()

        val pool = EventPool()
        pool.on<WorldFinishLoadingEvent> {
            if(it.world == WorldManager.mainWorld) {
                countdownLatch.countDown()
            }
        }
        countdownLatch.await()
        pool.dispose()
    }
}

