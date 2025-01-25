package io.github.dockyardmc.resourcepack

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundAddResourcepackPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundRemoveResourcepackPacket
import java.util.UUID

object ResourcepackManager {
    val pending: MutableList<Resourcepack> = mutableListOf()
}

class Resourcepack {
    var onFail: ((ResourcepackResponseEvent) -> Unit)? = null
    var onSuccess: ((ResourcepackResponseEvent) -> Unit)? = null
    var url: String = ""
    var required: Boolean = false
    var promptMessage: String? = null

    var player: Player? = null
    var uuid: UUID = UUID.randomUUID()
    var name: String = uuid.toString()

    fun withPromptMessage(message: String) {
        this.promptMessage = message
    }

    fun setForced(forced: Boolean) {
        this.required = forced
    }

    fun withUrl(url: String) {
        this.url = url
    }

    fun onFail(unit: (ResourcepackResponseEvent) -> Unit) {
        onFail = unit
    }

    fun onSuccess(unit: (ResourcepackResponseEvent) -> Unit) {
        onSuccess = unit
    }

}

data class ResourcepackResponseEvent(
    val resourcepack: Resourcepack,
    val player: Player,
    val status: ResourcepackStatus
)

fun Player.addResourcepack(name: String, resourcepack: Resourcepack.() -> Unit) {
    val pack = Resourcepack()
    resourcepack.invoke(pack)

    pack.name = name
    pack.player = this

    ResourcepackManager.pending.add(pack)
    this.sendPacket(ClientboundAddResourcepackPacket(pack))
}

fun Player.removeResourcepack(name: String) {
    val pack = this.resourcepacks[name] ?: return
    this.sendPacket(ClientboundRemoveResourcepackPacket(pack.uuid))
}

fun Collection<Player>.addResourcepack(name: String, resourcepack: Resourcepack.() -> Unit) {
    this.forEach { it.addResourcepack(name, resourcepack) }
}

