package io.github.dockyardmc

import cz.lukynka.prettylog.AnsiPair
import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.annotations.AnnotationProcessor
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.protocol.PacketParser
import io.github.dockyardmc.registry.Biome

val TCP = CustomLogType("\uD83E\uDD1D TCP", AnsiPair.GRAY)

object Main {
    lateinit var instance: DockyardServer
    lateinit var customBiome: Biome
}

fun main(args: Array<String>) {

    if(args.contains("validate-packets")) {
        VerifyPacketIds()
        return
    }
    //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED)
    var argsString = ""
    args.forEach { argsString += it }
    log("Starting with args: $argsString", LogType.RUNTIME)

    ConfigManager.load()

    val packetClasses = AnnotationProcessor.getServerboundPacketClassInfo()
    PacketParser.idAndStatePairToPacketClass = packetClasses

    AnnotationProcessor.addIdsToClientboundPackets()

    Main.instance = DockyardServer()
    Main.instance.start()
}