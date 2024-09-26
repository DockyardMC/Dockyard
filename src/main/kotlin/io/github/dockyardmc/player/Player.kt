package io.github.dockyardmc.player

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import io.github.dockyardmc.commands.buildCommandGraph
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
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.registry.registries.DamageType
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.resourcepack.Resourcepack
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.ui.DrawableContainerScreen
import io.github.dockyardmc.utils.*
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.Vector3f
import io.github.dockyardmc.world.ConcurrentChunkEngine
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import io.netty.channel.ChannelHandlerContext
import java.time.Instant
import java.util.*

class Player(
    val username: String,
    override var entityId: Int,
    override var uuid: UUID,
    override var type: EntityType = EntityTypes.PLAYER,
    override var world: World,
    override var location: Location = world.defaultSpawnLocation,
    val connection: ChannelHandlerContext,
    val address: String,
    var crypto: PlayerCrypto,
): Entity(location) {
    override var velocity: Vector3 = Vector3(0, 0, 0)
    override var hasGravity: Boolean = true
    override var isInvulnerable: Boolean = true
    override var hasCollision: Boolean = true
    override var displayName: String = username
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
    val permissions: BindableList<String> = BindableList()
    var isFullyInitialized: Boolean = false
    var inventory: Inventory = Inventory(this)
    var gameMode: Bindable<GameMode> = Bindable(GameMode.ADVENTURE)
    var displayedSkinParts: BindableList<DisplayedSkinPart> = BindableList(DisplayedSkinPart.CAPE, DisplayedSkinPart.JACKET, DisplayedSkinPart.LEFT_PANTS, DisplayedSkinPart.RIGHT_PANTS, DisplayedSkinPart.LEFT_SLEEVE, DisplayedSkinPart.RIGHT_SLEEVE, DisplayedSkinPart.HAT)
    var isConnected: Boolean = true
    val tabListHeader: Bindable<Component> = Bindable("".toComponent())
    val tabListFooter: Bindable<Component> = Bindable("".toComponent())
    val isListed: Bindable<Boolean> = Bindable(true)

    val saturation: Bindable<Float> = Bindable(0f)
    val food: Bindable<Double> = Bindable(20.0)
    val experienceLevel: Bindable<Int> = Bindable(0)
    val experienceBar: Bindable<Float> = Bindable(0f)
    val currentOpenInventory: Bindable<DrawableContainerScreen?> = Bindable(null)
    var hasSkin = false
    var itemInUse: ItemInUse? = null
    var lastRightClick = 0L
    val flySpeed: Bindable<Float> = Bindable(0.05f)
    val redVignette: Bindable<Float> = Bindable(0f)
    val time: Bindable<Long> = Bindable(-1)

    val resourcepacks: MutableMap<String, Resourcepack> = mutableMapOf()

    val chunkEngine = ConcurrentChunkEngine(this)
    var visibleEntities: MutableList<Entity> = mutableListOf()

    var lastInteractionTime: Long = -1L

    lateinit var lastSentPacket: ClientboundPacket

    init {
        selectedHotbarSlot.valueChanged {
            this.sendPacket(ClientboundSetHeldItemPacket(it.newValue))
            val item = inventory[it.newValue]
            equipment.value = equipment.value.apply { mainHand = item }
        }

        isFlying.valueChanged { this.sendPacket(ClientboundPlayerAbilitiesPacket(it.newValue, isInvulnerable, canFly.value, flySpeed.value)) }
        canFly.valueChanged { this.sendPacket(ClientboundPlayerAbilitiesPacket(isFlying.value, isInvulnerable, it.newValue, flySpeed.value)) }
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

            isInvisible.value = it.newValue == GameMode.SPECTATOR
            refreshAbilities()
            val updatePacket = ClientboundPlayerInfoUpdatePacket(PlayerInfoUpdate(uuid, UpdateGamemodeInfoUpdateAction(gameMode.value)))
            sendPacket(updatePacket)
            sendToViewers(updatePacket)
        }

        permissions.itemAdded { rebuildCommandNodeGraph() }
        permissions.itemRemoved { rebuildCommandNodeGraph() }

        health.valueChanged { sendHealthUpdatePacket() }
        food.valueChanged { sendHealthUpdatePacket() }
        saturation.valueChanged { sendHealthUpdatePacket() }

        tabListHeader.valueChanged { sendPacket(ClientboundTabListPacket(it.newValue, tabListFooter.value)) }
        tabListFooter.valueChanged { sendPacket(ClientboundTabListPacket(tabListHeader.value, it.newValue)) }

        redVignette.valueChanged {
            val distance = percentOf(it.newValue * 10, world.worldBorder.diameter).toInt()
            sendPacket(ClientboundSetWorldBorderWarningDistance(distance))
        }

        pose.valueChanged {
            metadata[EntityMetadataType.POSE] = EntityMetadata(EntityMetadataType.POSE, EntityMetaValue.POSE, it.newValue)
            sendMetadataPacketToViewers()
            sendSelfMetadataPacket()
        }

        displayedSkinParts.listUpdated {
            metadata[EntityMetadataType.POSE] = EntityMetadata(EntityMetadataType.DISPLAY_SKIN_PARTS, EntityMetaValue.BYTE, displayedSkinParts.values.getBitMask())
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
        time.valueChanged { updateWorldTime() }
    }

    override fun tick() {
        val entities = world.entities.values.filter { it.autoViewable && it != this }

        val add = entities.filter { it.location.distance(this.location) <= it.renderDistanceBlocks && !visibleEntities.contains(it) }
        val remove = entities.filter { it.location.distance(this.location) > it.renderDistanceBlocks && visibleEntities.contains(it) }

        add.forEach { it.addViewer(this) }
        remove.forEach { it.removeViewer(this, false) }

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
                    viewers.playSound("minecraft:entity.generic.eat", location, 1f, randomFloat(0.9f, 1.3f))
                    viewers.spawnParticle(location.clone().apply { y += 1.5 }, Particles.ITEM, Vector3f(0.2f), 0.05f, 6, false, ItemParticleData(item))
                }

                if(world.worldAge - itemInUse!!.startTime >= itemInUse!!.time && itemInUse!!.time > 0) {
                    world.playSound("minecraft:entity.player.burp", location)
                    val component = item.components.firstOrNullByType<FoodItemComponent>(FoodItemComponent::class)!!

                    val foodToAdd = component.nutrition + food.value
                    if(foodToAdd > 20) {
                        val saturationToAdd = food.value - 20
                        food.value = 20.0
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
        super.tick()
    }

    fun sendHealthUpdatePacket() {
        val packet = ClientboundSetHealthPacket(health.value, food.value.toInt(), saturation.value)
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
        if(player == this) return
        val infoUpdatePacket = PlayerInfoUpdate(uuid, AddPlayerInfoUpdateAction(ProfilePropertyMap(username, mutableListOf(profile!!.properties[0]))))
        player.sendPacket(ClientboundPlayerInfoUpdatePacket(infoUpdatePacket))

        super.addViewer(player)

        player.sendMetadataPacket(this)
        this.displayedSkinParts.triggerUpdate()
        sendMetadataPacket(player)
        sendEquipmentPacket(player)
        player.sendPacket(ClientboundPlayerInfoUpdatePacket(PlayerInfoUpdate(uuid, SetListedInfoUpdateAction(isListed.value))))
    }

    //TODO Add off-hand support
    fun getHeldItem(hand: PlayerHand): ItemStack = inventory[selectedHotbarSlot.value]

    override fun removeViewer(player: Player, isDisconnect: Boolean) {
        if(player == this) return
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
    fun kick(reason: Component) { sendPacket(ClientboundDisconnectPacket(reason)) }
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
        connection.sendPacket(packet, getProcessor())
        lastSentPacket = packet
    }

    fun sendToViewers(packet: ClientboundPacket) {
        viewers.forEach { viewer ->
            if(viewer.getProcessor().state != ProtocolState.PLAY) return@forEach
            viewer.sendPacket(packet)
        }
    }

    override fun teleport(location: Location) {
        this.location = location
        if(!WorldManager.worlds.containsValue(location.world)) throw Exception("That world does not exist!")
        if(location.world != world) location.world.join(this)

        val teleportPacket = ClientboundPlayerSynchronizePositionPacket(location)
        this.sendPacket(teleportPacket)
        super.teleport(location)
    }

    fun hasPermission(permission: String): Boolean {
        if(permission.isEmpty()) return true
        if(permissions.values.contains("dockyard.all") || permissions.values.contains("dockyard.*")) return true
        return permissions.values.contains(permission)
    }

    fun sendSelfMetadataPacket() {
        sendMetadataPacket(this)
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
        isOnFire.value = false
        health.value = 20f

        sendPacket(ClientboundRespawnPacket(this, ClientboundRespawnPacket.RespawnDataKept.KEEP_ALL))
        location = this.world.defaultSpawnLocation

        this.world.chunks.values.toList().forEach {
            chunkEngine.loadChunk(ChunkUtils.getChunkIndex(it), world)
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
        refreshPotionEffects()

        pose.triggerUpdate()
        refreshAbilities()
        displayedSkinParts.triggerUpdate()
        sendPacket(ClientboundPlayerSynchronizePositionPacket(world.defaultSpawnLocation))
    }

    fun refreshAbilities() {
        val packet = ClientboundPlayerAbilitiesPacket(isFlying.value, isInvulnerable, canFly.value, flySpeed.value, 0.1f, this.gameMode.value)
        sendPacket(packet)
    }

    fun closeInventory() {
        sendPacket(ClientboundCloseInventoryPacket(0))
    }

    fun resetExperience() {
        experienceLevel.value = 0
        experienceBar.value = 0f
    }

    fun rebuildCommandNodeGraph() {
        this.sendPacket(ClientboundCommandsPacket(buildCommandGraph(this)))
    }

    fun updateWorldTime() {
        val time = if(time.value == -1L) world.time.value else time.value
        val packet = ClientboundUpdateTimePacket(world.worldAge, time)
        sendPacket(packet)
    }

    fun openDrawableScreen(screen: DrawableContainerScreen) {
        screen.open(this)
    }

    fun playTotemAnimation(customModelData: Int? = null) {
        val held = getHeldItem(PlayerHand.MAIN_HAND)
        if(customModelData != null) {
            val totem = ItemStack(Items.TOTEM_OF_UNDYING)
            totem.customModelData.value = customModelData
            inventory[selectedHotbarSlot.value] = totem
        }
        val packet = ClientboundEntityEventPacket(this, EntityEvent.PLAYER_PLAY_TOTEM_ANIMATION)
        sendPacket(packet)

        if(customModelData != null) {
            inventory[selectedHotbarSlot.value] = held
        }
    }
}