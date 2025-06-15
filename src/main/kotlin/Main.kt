import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.dialog.createNoticeDialog
import io.github.dockyardmc.dialog.showDialog
import io.github.dockyardmc.registry.registries.DialogRegistry

fun main() {

    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        useDebugMode(true)
    }

    val dialog = createNoticeDialog("test") {
        title = "Important poll !!!!"
        canCloseWithEsc = false

        addBooleanInput("is_gay") {
            label = "Are you gay?"
        }
        addBooleanInput("uses_nix") {
            label = "Do you use NixOS?"
        }

        withButton("Submit") {
            withCustomClickAction("dockyard:gay_nix_poll")
        }
    }


    Commands.add("/test") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()

            player.showDialog(dialog)
        }
    }

    server.start()
}