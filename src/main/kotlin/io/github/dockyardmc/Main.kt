package io.github.dockyardmc

import CustomLogType
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerListPingEvent

const val version = 0.1

val TCP = CustomLogType("\uD83E\uDD1D TCP", AnsiPair.GRAY)

fun main(args: Array<String>) {

    val port = (args.getOrNull(0) ?: "25565").toInt()
    val server = DockyardServer(port)

    Events.on<ServerListPingEvent> {
        it.status.version.name = "DockyardMC 1.20.4"
    }

    server.start()

}