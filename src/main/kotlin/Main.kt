import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.schematics.SchematicReader
import io.github.dockyardmc.schematics.placeSchematicAsync
import java.io.File

fun main() {

    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        useDebugMode(true)
    }

    Commands.add("/test") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
            val location = player.location
            val schematic = SchematicReader.read(File("./test.schem"))
            location.world.placeSchematicAsync(schematic, location).thenAccept {
                broadcastMessage("<lime>done")
            }
        }
    }

    server.start()
}