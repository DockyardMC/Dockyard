package io.github.dockyardmc

import CustomLogType

const val version = 0.1

val TCP = CustomLogType("\uD83E\uDD1D TCP", AnsiPair.GRAY)
val TEMP = CustomLogType("\uD83D\uDC1B DEBUG", AnsiPair.ORANGE)
val DECRYPT = CustomLogType("\uD83D\uDD11 ENCRYPTION", AnsiPair.WHITE)

fun main(args: Array<String>) {

    val port = (args.getOrNull(0) ?: "25565").toInt()

    val server = DockyardServer(port)
    server.start()
}