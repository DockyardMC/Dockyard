package io.github.dockyardmc.profiler

import cz.lukynka.prettylog.AnsiPair
import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.utils.debug
import java.time.Instant

class Profiler {

    companion object {
        var TimeLog = CustomLogType("⌛ Profiler", AnsiPair.GRAY)
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

    fun end() {
        endTime = Instant.now()
        val overall = endTime.toEpochMilli() - startTime.toEpochMilli()
        if(onlyLogAbove != null) {
            if(overall > onlyLogAbove!!) {
                log("Profiler \"$name\" ended. Took ${overall}ms, ${overall - onlyLogAbove!!}ms more than expected", LogType.WARNING)
            }
        } else {
            debug("Profiler \"$name\" ended. Took ${overall}ms", TimeLog)
        }
    }
}