package io.github.dockyardmc.server.via

import com.viaversion.viaversion.ViaManagerImpl
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.ViaAPI
import com.viaversion.viaversion.api.configuration.ViaVersionConfig
import com.viaversion.viaversion.api.platform.PlatformTask
import com.viaversion.viaversion.api.platform.ProtocolDetectorService
import com.viaversion.viaversion.api.platform.ViaServerProxyPlatform
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.scheduler.AsyncSchedulerTask
import io.github.dockyardmc.scheduler.runnables.ticks
import java.io.File
import java.util.logging.Logger


class DockyardViaVersionPlatform: ViaServerProxyPlatform<Player> {

    val config = DockyardViaConfig()
    val api = Via.init(ViaManagerImpl.builder()
        .platform(this)

        .build()
    )

    override fun getLogger(): Logger {
        return Logger.getGlobal()
    }

    override fun getPlatformName(): String {
        return "DockyardMC"
    }

    override fun getPlatformVersion(): String {
        return DockyardServer.versionInfo.dockyardVersion
    }

    override fun getPluginVersion(): String {
        return DockyardServer.versionInfo.dockyardVersion
    }

    override fun runAsync(runnable: Runnable): PlatformTask<*> {
        DockyardServer.scheduler.runAsync {
            runnable.run()
        }
        return SchedulerTask()
    }

    override fun runRepeatingAsync(runnable: Runnable, ticks: Long): PlatformTask<*> {
        val task = DockyardServer.scheduler.runRepeatingAsync(ticks.toInt().ticks) {
            runnable.run()
        }
        return SchedulerTask(task)
    }

    override fun runSync(runnable: Runnable): PlatformTask<*> {
        runnable.run()
        return SchedulerTask()
    }

    override fun runSync(runnable: Runnable, ticks: Long): PlatformTask<*> {
        DockyardServer.scheduler.runLater(ticks.toInt().ticks) {
            runnable.run()
        }
        return SchedulerTask()
    }

    override fun runRepeatingSync(runnable: Runnable, ticks: Long): PlatformTask<*> {
        val task = DockyardServer.scheduler.runRepeating(ticks.toInt().ticks) {
            runnable.run()
        }
        return SyncSchedulerTask(task)
    }

    override fun getApi(): ViaAPI<Player> {
    }

    override fun getConf(): ViaVersionConfig {
        return CONFIG
    }

    override fun getDataFolder(): File {
        return File("./via/")
    }

    override fun hasPlugin(plugin: String): Boolean {
        return false
    }

    override fun protocolDetectorService(): ProtocolDetectorService {
    }

    class SchedulerTask(val task: AsyncSchedulerTask<Unit>? = null): PlatformTask<AsyncSchedulerTask<Unit>> {

        override fun cancel() {
            task?.cancel()
        }
    }

    class SyncSchedulerTask(val task: io.github.dockyardmc.scheduler.SchedulerTask? = null): PlatformTask<SchedulerTask> {

        override fun cancel() {
            task?.cancel()
        }
    }
}