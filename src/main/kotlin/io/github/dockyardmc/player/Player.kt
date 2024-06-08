package io.github.dockyardmc.player

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.BindableMutableList
import io.github.dockyardmc.entity.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.inventory.Inventory
import io.github.dockyardmc.inventory.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.Entities
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.world.World
import io.netty.channel.ChannelHandlerContext
import java.util.UUID

class Player(val username: String, override var entityId: Int, override var uuid: UUID, override var type: EntityType = Entities.PLAYER, override var location: Location = Location(0, 256, 0), override var world: World, val connection: ChannelHandlerContext, val address: String, val crypto: PlayerCrypto): Entity {

    override var velocity: Vector3 = Vector3(0, 0, 0)
    override var viewers: MutableList<Player> = mutableListOf()
    override var hasGravity: Boolean = true
    override var canBeDamaged: Boolean = true
    override var hasCollision: Boolean = true
    override var displayName: String = username
    override var metadata: BindableMutableList<EntityMetadata> = BindableMutableList()
    override var pose: Bindable<EntityPose> = Bindable(EntityPose.STANDING)
    override var isOnGround: Boolean = true
    var brand: String = "minecraft:vanilla"
    var profile: ProfilePropertyMap? = null
    var clientConfiguration: ClientConfiguration? = null
    var isFlying: Bindable<Boolean> = Bindable(false)
    var canFly: Bindable<Boolean> = Bindable(false)
    var isSneaking: Boolean = false
    var isSprinting: Boolean = false
    var selectedHotbarSlot: Bindable<Int> = Bindable(0)
    val permissions: MutableList<String> = mutableListOf()
    var isFullyInitialized: Boolean = false
    var inventory: Inventory = Inventory()
    var gameMode: Bindable<GameMode> = Bindable(GameMode.ADVENTURE)
    var flySpeed: Bindable<Float> = Bindable<Float>(0.05f)
    var displayedSkinParts: BindableMutableList<DisplayedSkinPart> = BindableMutableList(DisplayedSkinPart.CAPE, DisplayedSkinPart.JACKET, DisplayedSkinPart.LEFT_PANTS, DisplayedSkinPart.RIGHT_PANTS, DisplayedSkinPart.LEFT_SLEEVE, DisplayedSkinPart.RIGHT_SLEEVE, DisplayedSkinPart.HAT)

    init {
        selectedHotbarSlot.valueChanged { this.sendPacket(ClientboundSetHeldItemPacket(it.newValue)) }
        isFlying.valueChanged { this.sendPacket(ClientboundPlayerAbilitiesPacket(it.newValue, canBeDamaged, canFly.value, flySpeed.value)) }
        canFly.valueChanged { this.sendPacket(ClientboundPlayerAbilitiesPacket(isFlying.value, canBeDamaged, it.newValue, flySpeed.value)) }
        flySpeed.valueChanged { this.sendPacket(ClientboundPlayerAbilitiesPacket(isFlying.value, canBeDamaged, canFly.value, it.newValue)) }
        gameMode.valueChanged { this.sendPacket(ClientboundPlayerGameEventPacket(GameEvent.CHANGE_GAME_MODE, it.newValue.ordinal.toFloat())) }

        pose.valueChanged {
            metadata.addOrUpdate(EntityMetadata(EntityMetaIndex.POSE, EntityMetadataType.POSE, it.newValue))
            sendViewersMedataPacket()
            sendSelfMetadataPacket()
        }

        displayedSkinParts.listUpdated {
            metadata.addOrUpdate(EntityMetadata(EntityMetaIndex.DISPLAY_SKIN_PARTS, EntityMetadataType.BYTE, displayedSkinParts.values.getBitMask()))
            sendViewersMedataPacket()
            sendSelfMetadataPacket()
        }
    }

    override fun addViewer(player: Player) {
        val infoUpdatePacket = PlayerInfoUpdate(uuid, AddPlayerInfoUpdateAction(ProfilePropertyMap(username, mutableListOf(profile!!.properties[0]))))
        player.sendPacket(ClientboundPlayerInfoUpdatePacket(0x01, mutableListOf(infoUpdatePacket)))

        super.addViewer(player)

        val packetIn = ClientboundEntityMetadataPacket(player)
        this.sendPacket(packetIn)

        val packetOut = ClientboundEntityMetadataPacket(this)
        player.sendPacket(packetOut)
    }

    fun getHeldItem(hand: PlayerHand): ItemStack {
        //TODO Offhand hand support
        return inventory.get(selectedHotbarSlot.value)
    }

    override fun removeViewer(player: Player, isDisconnect: Boolean) {
        if(isDisconnect) {
            val playerRemovePacket = ClientboundPlayerInfoRemovePacket(this)
            player.sendPacket(playerRemovePacket)
        }
        viewers.remove(player)
        super.removeViewer(player, isDisconnect)
    }

    // Hold messages client receives before state is PLAY, then send them after state changes to PLAY
    private var queuedMessages = mutableListOf<Pair<Component, Boolean>>()
    fun releaseMessagesQueue() {
        queuedMessages.forEach { sendSystemMessage(it.first, it.second) }
        queuedMessages.clear()
    }

    override fun toString(): String { return username }
    fun kick(reason: String) { this.kick(reason.toComponent()) }
    fun kick(reason: Component) { connection.sendPacket(ClientboundDisconnectPacket(reason)) }
    fun sendMessage(message: String) { this.sendMessage(message.toComponent()) }
    fun sendMessage(component: Component) { sendSystemMessage(component, false) }
    fun sendActionBar(message: String) { this.sendActionBar(message.toComponent()) }
    fun sendActionBar(component: Component) { sendSystemMessage(component, true) }
    private fun sendSystemMessage(component: Component, isActionBar: Boolean) {
        val processor = PlayerManager.playerToProcessorMap[this.uuid]
        processor.let {
            if(processor!!.state != ProtocolState.PLAY) {
                queuedMessages.add(Pair(component, isActionBar))
                return
            }
            connection.sendPacket(ClientboundSystemChatMessagePacket(component, isActionBar))
        }
    }

    fun sendPacket(packet: ClientboundPacket) {
        connection.sendPacket(packet)
    }

    fun sendToViewers(packet: ClientboundPacket) {
        viewers.forEach { viewer ->
            if(PlayerManager.playerToProcessorMap[viewer.uuid]!!.state != ProtocolState.PLAY) return@forEach
            viewer.sendPacket(packet)
        }
    }

    fun teleport(location: Location) {
        this.location = location
        val teleportPacket = ClientboundPlayerSynchronizePositionPacket(location)
        this.sendPacket(teleportPacket)
    }

    fun hasPermission(permission: String): Boolean {
        if(permission.isEmpty()) return true
        return permissions.contains(permission)
    }

    fun sendSelfMetadataPacket() {
        val packet = ClientboundEntityMetadataPacket(this)
        this.sendPacket(packet)
    }
}