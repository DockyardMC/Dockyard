package io.github.dockyardmc.utils

import cz.lukynka.prettylog.AnsiPair
import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.InstrumentationHotReloadEvent
import io.github.dockyardmc.extentions.broadcastMessage
import net.bytebuddy.agent.ByteBuddyAgent
import java.lang.instrument.ClassFileTransformer
import java.lang.management.ManagementFactory
import java.security.ProtectionDomain

object InstrumentationUtils {
    private const val RELOAD_THRESHOLD = 100L
    private var lastReloadTime = 0L
    private val reloadedClasses = mutableListOf<String>()

    val LOG_TYPE = CustomLogType("🛠️ Instrumentation", AnsiPair.WARM_ORANGE)

    fun isDebuggerAttached(): Boolean {
        val arguments = ManagementFactory.getRuntimeMXBean().inputArguments
        for (arg in arguments) {
            if (arg.contains("jdwp")) {
                return true
            }
        }
        return false
    }

    fun setupHotReloadDetection() {
        try {
            val instrumentation = ByteBuddyAgent.install()

            instrumentation.addTransformer(object : ClassFileTransformer {
                override fun transform(
                    loader: ClassLoader?,
                    className: String,
                    classBeingRedefined: Class<*>?,
                    protectionDomain: ProtectionDomain?,
                    classfileBuffer: ByteArray
                ): ByteArray? {
                    if (classBeingRedefined != null) {
                        val currentTime = System.currentTimeMillis()
                        val formattedClassName = className.replace('/', '.')

                        if (currentTime - lastReloadTime > RELOAD_THRESHOLD) {

                            reloadedClasses.clear()
                            broadcastMessage(" ")
                            broadcastMessage("<#fc6203>(\uD83D\uDD25) <#ffaf7d>Hot Reload Detected! ")

                            log("🔥🔄 Hot reload detected! ", LOG_TYPE)
                            lastReloadTime = currentTime
                        }

                        reloadedClasses.add(formattedClassName)
                        Events.dispatch(InstrumentationHotReloadEvent(classBeingRedefined.kotlin))
                        log("  └─ Reloaded: $formattedClassName", LOG_TYPE)

                        var chatName = formattedClassName.substringAfterLast('.')
                        if (chatName.length > 40) {
                            chatName = chatName.substring(0, 40) + "..."
                        }
                        broadcastMessage("<hover:show_text:'<white>$formattedClassName'> <#fc6203>✎ <aqua><u>${chatName}</hover> ", true)
                    }
                    return null
                }
            }, true)

            log("Successfully setup hot reload hook for jetbrains runtime!", LOG_TYPE)

        } catch (exception: Exception) {
            log("Failed to setup hot reload hook: $exception", LogType.ERROR)
            log(exception)
        }
    }
}