package io.github.dockyardmc.sailboat

import io.github.dockyardmc.sailboat.server.PhaethonServer

fun main() {
    val server = PhaethonServer("0.0.0.0", 25565)
    server.start()
}