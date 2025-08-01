//package io.github.dockyardmc.resourcepack
//
//import io.github.dockyardmc.player.Player
//import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundAddResourcepackPacket
//import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundRemoveResourcepackPacket
//import java.util.UUID
//
//object ResourcepackManager {
//    val pending: MutableList<ResourcepackOLD> = mutableListOf()
//}
//
//class ResourcepackOLD {
//    var onFail: ((ResourcepackResponseEvent) -> Unit)? = null
//    var onSuccess: ((ResourcepackResponseEvent) -> Unit)? = null
//    var url: String = ""
//    var required: Boolean = false
//    var promptMessage: String? = null
//
//    var player: Player? = null
//    var uuid: UUID = UUID.randomUUID()
//    var name: String = uuid.toString()
//
//    fun withPromptMessage(message: String) {
//        this.promptMessage = message
//    }
//
//    fun setForced(forced: Boolean) {
//        this.required = forced
//    }
//
//    fun withUrl(url: String) {
//        this.url = url
//    }
//
//    fun onFail(unit: (ResourcepackResponseEvent) -> Unit) {
//        onFail = unit
//    }
//
//    fun onSuccess(unit: (ResourcepackResponseEvent) -> Unit) {
//        onSuccess = unit
//    }
//
//}
//
//data class ResourcepackResponseEvent(
//    val resourcepack: ResourcepackOLD,
//    val player: Player,
//    val status: ResourcepackStatus
//)
//
//inline fun Player.addResourcepack(name: String, resourcepack: ResourcepackOLD.() -> Unit) {
//    addResourcepack(
//        name,
//        ResourcepackOLD().apply(resourcepack)
//    )
//}
//
//fun Player.addResourcepack(name: String, pack: ResourcepackOLD) {
//    pack.name = name
//    pack.player = this
//
//    ResourcepackManager.pending.add(pack)
//    this.sendPacket(ClientboundAddResourcepackPacket(pack))
//}
//
//fun Player.removeResourcepack(name: String) {
//    val pack = this.resourcepacks[name] ?: return
//    this.sendPacket(ClientboundRemoveResourcepackPacket(pack.uuid))
//}
//
//inline fun Collection<Player>.addResourcepack(name: String, resourcepack: ResourcepackOLD.() -> Unit) {
//    this.forEach { it.addResourcepack(name, resourcepack) }
//}
//
//enum class ResourcepackStatus {
//    SUCCESSFULLY_LOADED,
//    DECLINED,
//    FAILED_TO_DOWNLOAD,
//    ACCEPTED,
//    DOWNLOADED,
//    INVALID_URL,
//    FAILED_TO_RELOAD,
//    DISCARDED
//}