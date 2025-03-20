package io.github.dockyardmc.profiler

import cz.lukynka.prettylog.AnsiPair
import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.utils.debug
import java.time.Instant
import kotlin.system.measureTimeMillis

@Deprecated("Use spark tick monitor instead")
class Profiler {

    companion object {
        var TimeLog = CustomLogType("⌛ Profiler", AnsiPair.SALMON)
    }

    var name = "profiler"
    private lateinit var startTime: Instant
    private lateinit var endTime: Instant
    var onlyLogAbove: Long? = null

    fun start(name: String, onlyLogAbove: Long? = null) {
        this.name = name
        this.onlyLogAbove = onlyLogAbove
        startTime = Instant.now()
    }

    fun end(): Long {
        endTime = Instant.now()
        val overall = endTime.toEpochMilli() - startTime.toEpochMilli()
        if(onlyLogAbove != null) {
            if(overall > onlyLogAbove!!) {
                log("Profiler \"$name\" ended. Took ${overall}ms, ${overall - onlyLogAbove!!}ms more than expected", LogType.DEBUG)
            }
        } else {
            debug("Profiler \"$name\" ended. Took ${overall}ms", logType = TimeLog)
        }
        return overall
    }
}

inline fun profiler(name: String, block: () -> Unit) {
    val ms = measureTimeMillis(block)
    debug("\"$name\" took ${ms}ms", false)
}