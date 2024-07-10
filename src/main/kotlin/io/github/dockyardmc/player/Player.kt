package io.github.dockyardmc.player

import cz.lukynka.prettylog.log
import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.BindableList
import io.github.dockyardmc.entities.*
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerDamageEvent
import io.github.dockyardmc.events.PlayerDeathEvent
import io.github.dockyardmc.events.PlayerRespawnEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.inventory.Inventory
import io.github.dockyardmc.item.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.particles.ItemParticleData
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.PlayerManager.getProcessor
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.DamageType
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import io.netty.channel.ChannelHandlerContext
import java.util.*

class Player(
    val username: String,
    override var entityId: Int,
    override var uuid: UUID,
    override var type: EntityType = EntityTypes.PLAYER,
    override var location: Location = Location(0, 0, 0, WorldManager.worlds.values.first()),
    override var world: World,
    val connection: ChannelHandlerContext,
    val address: String,
    val crypto: PlayerCrypto,
): Entity() {
    override var velocity: Vector3 = Vector3(0, 0, 0)
    override var viewers: MutableList<Player> = mutableListOf()
    override var hasGravity: Boolean = true
    override var isInvulnerable: Boolean = true
    override var hasCollision: Boolean = true
    override var displayName: String = username
    override var metadata: BindableList<EntityMetadata> = BindableList()
    override var pose: Bindable<EntityPose> = Bindable(EntityPose.STANDING)
    override var isOnGround: Boolean = true
    override var health: Bindable<Float> = Bindable(20f)
    override var inventorySize: Int = 35
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
    var inventory: Inventory = Inventory(this)
    var gameMode: Bindable<GameMode> = Bindable(GameMode.ADVENTURE)
    var flySpeed: Bindable<Float> = Bindable(0.05f) // 0.05 is the default fly speed in vanilla minecraft
    var displayedSkinParts: BindableList<DisplayedSkinPart> = BindableList(DisplayedSkinPart.CAPE, DisplayedSkinPart.JACKET, DisplayedSkinPart.LEFT_PANTS, DisplayedSkinPart.RIGHT_PANTS, DisplayedSkinPart.LEFT_SLEEVE, DisplayedSkinPart.RIGHT_SLEEVE, DisplayedSkinPart.HAT)
    var isConnected: Boolean = true
    val tabListHeader: Bindable<Component> = Bindable("".toComponent())
    val tabListFooter: Bindable<Component> = Bindable("".toComponent())
    val isListed: Bindable<Boolean> = Bindable(true)
    //TODO Implement
    val isOnFire: Bindable<Boolean> = Bindable(false)
    //TODO Implement
    val fireTicks: Bindable<Int> = Bindable(0)
    //TODO Implement
    val freezeTicks: Bindable<Int> = Bindable(0)
    val saturation: Bindable<Float> = Bindable(0f)
    val food: Bindable<Int> = Bindable(20)
    val experienceLevel: Bindable<Int> = Bindable(0)
    val experienceBar: Bindable<Float> = Bindable(0f)

    var itemInUse: ItemInUse? = null

    //for debugging
    lateinit var lastSentPacket: ClientboundPacket

    init {
        selectedHotbarSlot.valueChanged { this.sendPacket(ClientboundSetHeldItemPacket(it.newValue)) }
        isFlying.valueChanged { this.sendPacket(ClientboundPlayerAbilitiesPacket(it.newValue, isInvulnerable, canFly.value, flySpeed.value)) }
        canFly.valueChanged { this.sendPacket(ClientboundPlayerAbilitiesPacket(isFlying.value, isInvulnerable, it.newValue, flySpeed.value)) }
        flySpeed.valueChanged { this.sendPacket(ClientboundPlayerAbilitiesPacket(isFlying.value, isInvulnerable, canFly.value, it.newValue)) }
        gameMode.valueChanged {
            this.sendPacket(ClientboundPlayerGameEventPacket(GameEvent.CHANGE_GAME_MODE, it.newValue.ordinal.toFloat()))
            when(it.newValue) {
                GameMode.SPECTATOR,
                GameMode.CREATIVE -> {
                    canFly.value = true
                    isFlying.value = isFlying.value
                    isInvulnerable = true
                }
                GameMode.ADVENTURE,
                GameMode.SURVIVAL -> {
                    if (it.oldValue == GameMode.CREATIVE || it.oldValue == GameMode.SPECTATOR) {
                        canFly.value = false
                        isFlying.value = false
                        isInvulnerable = false
                    }
                }
            }
        }

        health.valueChanged { sendHealthUpdatePacket() }
        food.valueChanged { sendHealthUpdatePacket() }
        saturation.valueChanged { sendHealthUpdatePacket() }

        tabListHeader.valueChanged { sendPacket(ClientboundTabListPacket(it.newValue, tabListFooter.value)) }
        tabListFooter.valueChanged { sendPacket(ClientboundTabListPacket(tabListHeader.value, it.newValue)) }

        pose.valueChanged {
            metadata.addOrUpdate(EntityMetadata(EntityMetaIndex.POSE, EntityMetadataType.POSE, it.newValue))
            sendMetadataPacketToViewers()
            sendSelfMetadataPacket()
        }

        displayedSkinParts.listUpdated {
            metadata.addOrUpdate(EntityMetadata(EntityMetaIndex.DISPLAY_SKIN_PARTS, EntityMetadataType.BYTE, displayedSkinParts.values.getBitMask()))
            sendMetadataPacketToViewers()
            sendSelfMetadataPacket()
        }

        isListed.valueChanged {
            val update = PlayerInfoUpdate(uuid, SetListedInfoUpdateAction(it.newValue))
            val packet = ClientboundPlayerInfoUpdatePacket(update)
            sendToViewers(packet)
            sendPacket(packet)
        }

        experienceBar.valueChanged { sendUpdateExperiencePacket() }
        experienceLevel.valueChanged { sendUpdateExperiencePacket() }
    }

    fun tick() {
        if(itemInUse != null) {
            val item = itemInUse!!.item

            if(!item.isSameAs(getHeldItem(PlayerHand.MAIN_HAND))) {
                itemInUse = null
                return
            }

            val isFood = item.components.hasType(FoodItemComponent::class)
            if(isFood) {

                if((world.worldAge % 5) == 0L) {
                    val viewers = world.players.values.toMutableList().filter { it != this }
                    viewers.playSound("minecraft:entity.generic.eat", location, 1f, MathUtils.randomFloat(0.9f, 1.3f))
                    viewers.spawnParticle(location.clone().apply { y += 1.5 }, Particles.ITEM, Vector3f(0.2f), 0.05f, 6, false, ItemParticleData(item))
                }

                if(world.worldAge - itemInUse!!.startTime >= itemInUse!!.time && itemInUse!!.time > 0) {
                    world.playSound("minecraft:entity.player.burp", location)
                    val component = item.components.firstOrNullByType<FoodItemComponent>(FoodItemComponent::class)!!

                    val foodToAdd = component.nutrition + food.value
                    if(foodToAdd > 20) {
                        val saturationToAdd = food.value - 20
                        food.value = 20
                        saturation.value = saturationToAdd.toFloat()
                    } else {
                        food.value = foodToAdd
                    }

                    // notify the client that eating is finished
                    sendPacket(ClientboundEntityEventPacket(this, EntityEvent.PLAYER_ITEM_USE_FINISHED))

                    val newItem = if(item.amount == 1) ItemStack.air else item.clone().apply { amount -= 1 }
                    inventory[selectedHotbarSlot.value] = newItem

                    // if new item is air, stop eating, if not, reset eating time
                    if(!newItem.isSameAs(ItemStack.air)) {
                        itemInUse!!.startTime = world.worldAge
                        itemInUse!!.item = newItem
                    } else {
                        itemInUse = null
                    }
                }
            }
        }
    }

    fun sendHealthUpdatePacket() {
        val packet = ClientboundSetHealthPacket(health.value, food.value, saturation.value)
        sendPacket(packet)
    }

    fun sendUpdateExperiencePacket() {
        val packet = ClientboundSetExperiencePacket(experienceBar.value, experienceLevel.value)
        sendPacket(packet)
    }

    override fun damage(damage: Float, damageType: DamageType, attacker: Entity?, projectile: Entity?) {

        val event = PlayerDamageEvent(this, damage, damageType, attacker, projectile)
        Events.dispatch(event)
        if(event.cancelled) return

        var location: Location? = null
        if(event.attacker != null) location = event.attacker!!.location
        if(event.projectile != null) location = event.projectile!!.location

        if(event.damage > 0) {
            if(!isInvulnerable) {
                if(health.value - event.damage <= 0) kill() else health.value -= event.damage
            }
        }
        val packet = ClientboundDamageEventPacket(this, event.damageType, event.attacker, event.projectile, location)
        sendPacket(packet)
    }

    override fun kill() {
        val event = PlayerDeathEvent(this)
        Events.dispatch(event)
        if(event.cancelled) {
            health.value = 0.1f
            return
        }
        health.value = 0f
    }

    override fun addViewer(player: Player) {
        val infoUpdatePacket = PlayerInfoUpdate(uuid, AddPlayerInfoUpdateAction(ProfilePropertyMap(username, mutableListOf(profile!!.properties[0]))))
        player.sendPacket(ClientboundPlayerInfoUpdatePacket(infoUpdatePacket))

        super.addViewer(player)

        val packetIn = ClientboundEntityMetadataPacket(player)
        this.sendPacket(packetIn)

        val packetOut = ClientboundEntityMetadataPacket(this)
        player.sendPacket(packetOut)
    }

    //TODO Add off-hand support
    fun getHeldItem(hand: PlayerHand): ItemStack = inventory[selectedHotbarSlot.value]

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

    override fun toString(): String = username
    fun kick(reason: String) { this.kick(reason.toComponent()) }
    fun kick(reason: Component) { connection.sendPacket(ClientboundDisconnectPacket(reason)) }
    fun sendMessage(message: String) { this.sendMessage(message.toComponent()) }
    fun sendMessage(component: Component) { sendSystemMessage(component, false) }
    fun sendActionBar(message: String) { this.sendActionBar(message.toComponent()) }
    fun sendActionBar(component: Component) { sendSystemMessage(component, true) }
    private fun sendSystemMessage(component: Component, isActionBar: Boolean) {
        if(!isConnected) return
        val processor = this.getProcessor()
        if(processor.state != ProtocolState.PLAY) {
            queuedMessages.add(component to isActionBar)
            return
        }
        this.sendPacket(ClientboundSystemChatMessagePacket(component, isActionBar))
    }

    fun sendPacket(packet: ClientboundPacket) {
        if(!isConnected) return
        if(packet.state != this.getProcessor().state) return
        connection.sendPacket(packet, this)
        lastSentPacket = packet
    }

    fun sendToViewers(packet: ClientboundPacket) {
        viewers.forEach { viewer ->
            if(viewer.getProcessor().state != ProtocolState.PLAY) return@forEach
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

    fun clearTitle(reset: Boolean) {
        sendPacket(ClientboundClearTitlePacket(reset))
    }

    fun sendTitle(title: String, subtitle: String = "", fadeIn: Int = 10, stay: Int = 60, fadeOut: Int = 10) {
        val packets = mutableListOf(
            ClientboundSubtitlePacket(subtitle.toComponent()),
            ClientboundTitleTimesPacket(fadeIn, stay, fadeOut),
            ClientboundSetTitlePacket(title.toComponent()),
        )

        packets.forEach {
            this.sendPacket(it)
        }
    }

    fun respawn(isBecauseDeath: Boolean = false) {
        fireTicks.value = 0
        isOnFire.value = false
        health.value = 20f

        sendPacket(ClientboundRespawnPacket(this, ClientboundRespawnPacket.RespawnDataKept.KEEP_ALL))
        location = this.world.defaultSpawnLocation

        log("Respawned $this")
        this.world.chunks.forEach {
            sendPacket(it.packet)
        }

        refreshClientStateAfterRespawn()

        if(isBecauseDeath) Events.dispatch(PlayerRespawnEvent(this))
    }

    fun refreshClientStateAfterRespawn() {
        sendPacket(ClientboundPlayerGameEventPacket(GameEvent.START_WAITING_FOR_CHUNKS, 1f))
        sendPacket(ClientboundChangeDifficultyPacket(world.difficulty.value, true))
        gameMode.value = gameMode.value
        sendHealthUpdatePacket()
        experienceBar.triggerUpdate()
        experienceLevel.triggerUpdate()
        inventory.sendFullInventoryUpdate()

        pose.triggerUpdate()
        refreshAbilities()
        sendPacket(ClientboundPlayerSynchronizePositionPacket(world.defaultSpawnLocation))
    }

    fun refreshAbilities() {
        val packet = ClientboundPlayerAbilitiesPacket(isFlying.value, isInvulnerable, canFly.value, flySpeed.value, 0.1f)
        sendPacket(packet)
    }

    fun closeInventory() {
        sendPacket(ClientboundCloseInventoryPacket(0))
    }

    fun resetExperience() {
        experienceLevel.value = 0
        experienceBar.value = 0f
    }
}