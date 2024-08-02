package io.github.dockyardmc

import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.schematics.SchematicReader
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator
import java.io.File

// This is just maya testing env.. do not actually run this
fun main(args: Array<String>) {

    if(args.contains("validate-packets")) {
        VerifyPacketIds()
        return
    }

    if(args.contains("schematic-test")) {
        val schem = File("./test.schem")
        SchematicReader.read(schem)
        return
    }

    //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED)
    var argsString = ""
    args.forEach { argsString += it }

    val testWorld = WorldManager.create("test", FlatWorldGenerator(), DimensionTypes.OVERWORLD)
    testWorld.defaultSpawnLocation = Location(0, 201, 0, testWorld)

    val server = DockyardServer()
    server.start()
}