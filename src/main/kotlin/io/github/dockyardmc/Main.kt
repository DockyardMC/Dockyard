package io.github.dockyardmc

import CustomLogType
import io.github.dockyardmc.annotations.AnnotationProcessor
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.protocol.PacketParser

val TCP = CustomLogType("\uD83E\uDD1D TCP", AnsiPair.GRAY)

object Main {
    lateinit var instance: DockyardServer
}

fun main(args: Array<String>) {
    //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED)
    ConfigManager.load()

    val packetClasses = AnnotationProcessor.getServerboundPacketClassInfo()
    PacketParser.idAndStatePairToPacketClass = packetClasses

    var port = ConfigManager.currentConfig.port
    // make sure if you call server with port arg it uses that instead of config port
    if(args.getOrNull(0) != null) port = args[0].toInt()

    Main.instance = DockyardServer(port)
    Main.instance.start()
}

