package io.github.dockyardmc

import CustomLogType
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent

const val version = 0.1

val TCP = CustomLogType("\uD83E\uDD1D TCP", AnsiPair.GRAY)

object Main {
    lateinit var instance: DockyardServer
}

fun main(args: Array<String>) {
    val port = (args.getOrNull(0) ?: "25565").toInt()
    Main.instance = DockyardServer(port)

    Events.on<PlayerConnectEvent> {
        DockyardServer.broadcastMessage("<lime>→ <yellow>${it.player}")
    }

    Main.instance.start()
}