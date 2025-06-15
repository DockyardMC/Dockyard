import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands

fun main() {

    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        useDebugMode(true)
    }

    Commands.add("/test") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
            player.sendMessage("<click:open_url:'https://github.com/DockyardMC/Dockyard'>[Click to Open Github]")

        }
    }

    server.start()
}