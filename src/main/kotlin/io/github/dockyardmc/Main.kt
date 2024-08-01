package io.github.dockyardmc

import cz.lukynka.prettylog.LogType
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator

// This is just maya testing env.. do not actually run this
fun main(args: Array<String>) {

    if(args.contains("validate-packets")) {
        VerifyPacketIds()
        return
    }
    //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED)
    var argsString = ""
    args.forEach { argsString += it }
    debug("Starting with args: $argsString", LogType.RUNTIME)

    val testWorld = WorldManager.create("test", FlatWorldGenerator(), DimensionTypes.OVERWORLD)
    testWorld.defaultSpawnLocation = Location(0, 201, 0, testWorld)

    val server = DockyardServer()
    server.start()
}