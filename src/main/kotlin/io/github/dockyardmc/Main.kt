package io.github.dockyardmc

import cz.lukynka.prettylog.AnsiPair
import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.annotations.AnnotationProcessor
import io.github.dockyardmc.commands.BooleanArgument
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.protocol.PacketParser

val TCP = CustomLogType("\uD83E\uDD1D TCP", AnsiPair.GRAY)

object Main {
    lateinit var instance: DockyardServer
}

fun main(args: Array<String>) {

    if(args.contains("validate-packets")) {
        VerifyPacketIds()
        return
    }

    Commands.add("/report") {
        it.description = "example command"
        it.addArgument("player", PlayerArgument())
        it.addArgument("reason", StringArgument())
        it.addOptionalArgument("stay anonymous", BooleanArgument())
        it.execute {

        }
    }

    //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED)
    var argsString = ""
    args.forEach { argsString += it }
    log("Starting with args: $argsString", LogType.RUNTIME)

    ConfigManager.load()

    val packetClasses = AnnotationProcessor.getServerboundPacketClassInfo()
    PacketParser.idAndStatePairToPacketClass = packetClasses

    AnnotationProcessor.addIdsToClientboundPackets()

    val port = ConfigManager.currentConfig.port
    // make sure if you call server with port arg it uses that instead of config port
//    if(args.getOrNull(0) != null) port = args[0].toInt()

    Main.instance = DockyardServer(port)
    Main.instance.start()
}

