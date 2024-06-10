package io.github.dockyardmc

import CustomLogType
import io.github.dockyardmc.annotations.AnnotationProcessor
import io.github.dockyardmc.protocol.PacketParser

val TCP = CustomLogType("\uD83E\uDD1D TCP", AnsiPair.GRAY)

object Main {
    lateinit var instance: DockyardServer
}

fun main(args: Array<String>) {
    val port = (args.getOrNull(0) ?: "25565").toInt()
    //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED)

    val packetClasses = AnnotationProcessor.getServerboundPacketClassInfo()
    PacketParser.idAndStatePairToPacketClass = packetClasses

    Main.instance = DockyardServer(port)

    Main.instance.start()
}

