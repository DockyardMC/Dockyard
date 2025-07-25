package io.github.dockyardmc.player

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.advancement.PlayerAdvancementTracker
import io.github.dockyardmc.attributes.PlayerAttributes
import io.github.dockyardmc.commands.buildCommandGraph
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.ItemDropEntity
import io.github.dockyardmc.entity.LightningBolt
import io.github.dockyardmc.entity.metadata.EntityMetaValue
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.EntityMetadataType
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.inventory.PlayerInventory
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.percentOf
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.particles.data.BlockParticleData
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.permissions.PermissionSystem
import io.github.dockyardmc.player.systems.*
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundChatCommandPacket
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundClientInputPacket
import io.github.dockyardmc.protocol.types.EquipmentSlot
import io.github.dockyardmc.protocol.types.GameProfile
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.registries.DamageType
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.resourcepack.Resourcepack
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.ui.Screen
import io.github.dockyardmc.utils.getPlayerEventContext
import io.github.dockyardmc.utils.now
import io.github.dockyardmc.world.PlayerChunkViewSystem
import io.github.dockyardmc.world.Weather
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.block.handlers.BlockHandlerManager
import io.netty.channel.ChannelHandlerContext
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class Player(
    val username: String,
    override var id: Int,
    override var uuid: UUID,
    override var type: EntityType = EntityTypes.PLAYER,
    override var world: World,
    override var location: Location = world.defaultSpawnLocation,
    val connection: ChannelHandlerContext,
    val address: String,
    var crypto: PlayerCrypto? = null,
    val networkManager: PlayerNetworkManager
) : Entity(location) {
    override var isInvulnerable: Boolean = true
    override var isOnGround: Boolean = true
    override val health: Bindable<Float> = bindablePool.provideBindable(20f)
    override var inventorySize: Int = 35

    var brand: String = "minecraft:vanilla"
    lateinit var gameProfile: GameProfile
    var clientConfiguration: ClientConfiguration? = null

    var isSneaking: Boolean = false
    var isSprinting: Boolean = false
    var isFullyInitialized: Boolean = false
    var isConnected: Boolean = true
    var isDigging: Boolean = false

    val gameMode: Bindable<GameMode> = bindablePool.provideBindable(GameMode.ADVENTURE)
    val isFlying: Bindable<Boolean> = bindablePool.provideBindable(false)
    val canFly: Bindable<Boolean> = bindablePool.provideBindable(false)
    val flySpeed: Bindable<Float> = bindablePool.provideBindable(0.05f)

    val permissions: BindableList<String> = bindablePool.provideBindableList()

    var inventory: PlayerInventory = PlayerInventory(this)
    val heldSlotIndex: Bindable<Int> = bindablePool.provideBindable(0)

    var mainHandItem: ItemStack
        get() = getHeldItem(PlayerHand.MAIN_HAND)
        set(value) = setHeldItem(PlayerHand.MAIN_HAND, value)

    var offHandItem: ItemStack
        get() = getHeldItem(PlayerHand.OFF_HAND)
        set(value) = setHeldItem(PlayerHand.OFF_HAND, value)

    val displayedSkinParts: BindableList<DisplayedSkinPart> = bindablePool.provideBindableList(DisplayedSkinPart.CAPE, DisplayedSkinPart.JACKET, DisplayedSkinPart.LEFT_PANTS, DisplayedSkinPart.RIGHT_PANTS, DisplayedSkinPart.LEFT_SLEEVE, DisplayedSkinPart.RIGHT_SLEEVE, DisplayedSkinPart.HAT)
    val isListed: Bindable<Boolean> = bindablePool.provideBindable(true)

    val tabListHeader: Bindable<Component> = bindablePool.provideBindable("".toComponent())
    val tabListFooter: Bindable<Component> = bindablePool.provideBindable("".toComponent())

    val saturation: Bindable<Float> = bindablePool.provideBindable(0f)
    val food: Bindable<Double> = bindablePool.provideBindable(20.0)
    val experienceLevel: Bindable<Int> = bindablePool.provideBindable(0)
    val experienceBar: Bindable<Float> = bindablePool.provideBindable(0f)

    val redVignette: Bindable<Float> = bindablePool.provideBindable(0f)
    val time: Bindable<Long> = bindablePool.provideBindable(-1)
    val fovModifier: Bindable<Float> = bindablePool.provideBindable(0.1f)
    val clientWeather: Bindable<Weather?> = bindablePool.provideBindable(null)

    val cooldownSystem = CooldownSystem(this)
    val foodEatingSystem = FoodEatingSystem(this)
    val chunkViewSystem = PlayerChunkViewSystem(this)
    val gameModeSystem = GameModeSystem(this)
    val playerInfoSystem = PlayerInfoSystem(this)
    val entityViewSystem = EntityViewSystem(this)
    val permissionSystem = PermissionSystem(this, permissions)
    val attributes = PlayerAttributes(this)
    val advancementTracker = PlayerAdvancementTracker(this)
    val noxesiumIntegration: NoxesiumIntegration = NoxesiumIntegration(this)

    val decoupledEntityViewSystemTicking = DockyardServer.scheduler.runRepeating(1.ticks) {
        entityViewSystem.tick()
    }

    val resourcepacks: MutableMap<String, Resourcepack> = mutableMapOf()

    var lastInteractionTime: Long = -1L
    var currentlyOpenScreen: Screen? = null
    val hasInventoryOpen: Boolean get() = currentlyOpenScreen != null
    var itemInUse: ItemInUse? = null

    private val pingRequestCounter = AtomicInteger(0)
    var ping = 0L
    var lastPingRequest: Long? = null
    var lastPingRequestFuture: CompletableFuture<Long>? = null

    val heldInputs = BindableList<ServerboundClientInputPacket.Input>()

    lateinit var lastSentPacket: ClientboundPacket

    override fun toString(): String = username

    init {
        gameModeSystem.handle(gameMode)
        playerInfoSystem.handle(customName, isListed)

        heldSlotIndex.valueChanged { index ->
            this.sendPacket(ClientboundSetHeldItemPacket(index.newValue))
            val item = inventory[index.newValue]
            equipment[EquipmentSlot.MAIN_HAND] = item
        }

        isFlying.valueChanged { event ->
            if (event.newValue) {
                // force standing pose if player is flying
                this.pose.value = EntityPose.STANDING
            } else if (this.isSneaking) {
                this.pose.value = EntityPose.SNEAKING
            }

            this.sendPacket(ClientboundPlayerAbilitiesPacket(event.newValue, isInvulnerable, canFly.value, flySpeed.value))
        }

        canFly.valueChanged { event -> this.sendPacket(ClientboundPlayerAbilitiesPacket(isFlying.value, isInvulnerable, event.newValue, flySpeed.value)) }

        fovModifier.valueChanged { event -> this.sendPacket(ClientboundPlayerAbilitiesPacket(isFlying.value, isInvulnerable, canFly.value, flySpeed.value, event.newValue)) }

        health.valueChanged { sendHealthUpdatePacket() }
        food.valueChanged { sendHealthUpdatePacket() }
        saturation.valueChanged { sendHealthUpdatePacket() }

        tabListHeader.valueChanged { event -> sendPacket(ClientboundTabListPacket(event.newValue, tabListFooter.value)) }
        tabListFooter.valueChanged { event -> sendPacket(ClientboundTabListPacket(tabListHeader.value, event.newValue)) }

        redVignette.valueChanged { event ->
            val distance = percentOf(event.newValue * 10, world.worldBorder.diameter).toInt()
            sendPacket(ClientboundSetWorldBorderWarningDistance(distance))
        }

        displayedSkinParts.listUpdated {
            metadata[EntityMetadataType.PLAYER_DISPLAY_SKIN_PARTS] = EntityMetadata(EntityMetadataType.PLAYER_DISPLAY_SKIN_PARTS, EntityMetaValue.BYTE, displayedSkinParts.values.getBitMask())
        }

        experienceBar.valueChanged { sendUpdateExperiencePacket() }
        experienceLevel.valueChanged { sendUpdateExperiencePacket() }
        time.valueChanged { updateWorldTime() }
        clientWeather.valueChanged { updateWeatherState() }

        heldInputs.itemAdded {
            if (it.item == ServerboundClientInputPacket.Input.SHIFT) {
                isSneaking = true

                // the only pose that allows sneaking
                if (pose.value == EntityPose.STANDING &&
                    !isFlying.value
                ) {
                    pose.value = EntityPose.SNEAKING
                }

                dismountCurrentVehicle()

                Events.dispatch(PlayerSneakToggleEvent(this, true))
            }
        }

        heldInputs.itemRemoved {
            if (it.item == ServerboundClientInputPacket.Input.SHIFT) {
                isSneaking = false

                if (pose.value == EntityPose.SNEAKING) {
                    pose.value = EntityPose.STANDING
                }

                Events.dispatch(PlayerSneakToggleEvent(this, false))
            }
        }

        hasNoGravity.value = false
    }

    override fun canPickupItem(dropEntity: ItemDropEntity, item: ItemStack): Boolean {
        return this.give(item)
    }

    override fun tick() {
        cooldownSystem.tick()
        foodEatingSystem.tick()
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
        if (event.cancelled) return

        var location: Location? = null
        if (event.attacker != null) location = event.attacker!!.location
        if (event.projectile != null) location = event.projectile!!.location

        if (event.damage > 0) {
            if (!isInvulnerable) {
                if (health.value - event.damage <= 0) kill() else health.value -= event.damage
            }
        }
        val packet = ClientboundDamageEventPacket(this, event.damageType, event.attacker, event.projectile, location)
        sendPacket(packet)
    }

    override fun kill() {
        val event = PlayerDeathEvent(this)
        Events.dispatch(event)
        if (event.cancelled) {
            health.value = 0.1f
            return
        }
        health.value = 0f
    }

    override fun addViewer(player: Player): Boolean {
        if (player == this) return false

        val updates = mutableListOf(
            PlayerInfoUpdate.AddPlayer(gameProfile),
            PlayerInfoUpdate.UpdateListed(isListed.value),
            PlayerInfoUpdate.UpdateDisplayName(customName.value),
        )
        player.sendPacket(ClientboundPlayerInfoUpdatePacket(mapOf(uuid to updates)))

        if (!super.addViewer(player)) return false

        player.sendMetadataPacket(this)
        this.displayedSkinParts.triggerUpdate()
        sendMetadataPacket(player)
        sendEquipmentPacket(player)
        return true
    }

    fun getHeldItem(hand: PlayerHand): ItemStack {
        if (hand == PlayerHand.MAIN_HAND) {
            return inventory[heldSlotIndex.value]
        } else if (hand == PlayerHand.OFF_HAND) {
            return equipment[EquipmentSlot.OFF_HAND] ?: ItemStack.AIR
        }
        return ItemStack.AIR
    }

    fun setHeldItem(hand: PlayerHand, item: ItemStack) {
        if (hand == PlayerHand.MAIN_HAND) {
            inventory[heldSlotIndex.value] = item
        } else if (hand == PlayerHand.OFF_HAND) {
            equipment[EquipmentSlot.OFF_HAND] = item
        }
    }

    override fun removeViewer(player: Player) {
        if (player == this) return
        super.removeViewer(player)
    }

    // Hold messages client receives before state is PLAY, then send them after state changes to PLAY
    private var queuedMessages = mutableListOf<Pair<String, Boolean>>()
    fun releaseMessagesQueue() {
        queuedMessages.forEach { message -> sendSystemMessage(message.first, message.second) }
        queuedMessages.clear()
    }

    fun runCommand(command: String) {
        val packet = ServerboundChatCommandPacket(command)
        packet.handle(networkManager, connection, 0, 0)
    }

    fun sendPingRequest(): CompletableFuture<Long> {
        lastPingRequestFuture = CompletableFuture<Long>()
        lastPingRequest = now()
        sendPacket(ClientboundPlayPingPacket(pingRequestCounter.getAndIncrement()))
        return lastPingRequestFuture!!
    }

    fun kick(reason: String) {
        this.networkManager.kick(reason, connection)
    }

    fun sendMessage(message: String, isSystem: Boolean = false) {
        this.sendSystemMessage(message, false, isSystem)
    }

    fun sendActionBar(message: String) {
        this.sendSystemMessage(message, isActionBar = true, isSystem = false)
    }

    private fun sendSystemMessage(message: String, isActionBar: Boolean, isSystem: Boolean = false) {
        if (!isConnected) return

        // this event and the `isSystem` arg is quite literally only for one feature in my other project,
        // but it's my server implementation anyway I can do whatever I want
        // - maya
        if (!isActionBar) {
            val event = ServerSendPlayerMessageEvent(this, message, isSystem, getPlayerEventContext(this))
            Events.dispatch(event)
            if (event.cancelled) return
        }

        if (networkManager.state != ProtocolState.PLAY) {
            queuedMessages.add(message to isActionBar)
            return
        }

        this.sendPacket(ClientboundSystemChatMessagePacket(message.toComponent(), isActionBar))
    }

    fun sendPackets(packets: Collection<ClientboundPacket>) {
        packets.forEach(::sendPacket)
    }

    fun sendPacket(packet: ClientboundPacket) {
        if (!isConnected) return
        if (packet.state != networkManager.state) return
        connection.sendPacket(packet, networkManager)
        lastSentPacket = packet
    }

    fun sendToViewers(packet: ClientboundPacket) {
        viewers.toList().forEach { viewer ->
            if (networkManager.state != ProtocolState.PLAY) return@forEach
            viewer.sendPacket(packet)
        }
    }

    fun refreshGameProfileState() {
        val currentLocation = this.location

        val removeInfo = ClientboundPlayerInfoRemovePacket(this)
        val entityRemovePacket = ClientboundEntityRemovePacket(this)
        val spawnEntityPacket = ClientboundSpawnEntityPacket(this.id, this.uuid, this.type.getProtocolId(), this.location, this.location.yaw, 0, this.velocity)
        val updates = mutableListOf(
            PlayerInfoUpdate.AddPlayer(this.gameProfile),
            PlayerInfoUpdate.UpdateListed(this.isListed.value),
            PlayerInfoUpdate.UpdateDisplayName(this.customName.value),
        )
        val addPacket = ClientboundPlayerInfoUpdatePacket(mapOf(this.uuid to updates.toList()))

        this.sendPacket(removeInfo)
        this.sendPacket(entityRemovePacket)
        this.sendPacket(addPacket)
        this.respawn(false)

        viewers.sendPacket(removeInfo)
        viewers.sendPacket(entityRemovePacket)
        viewers.sendPacket(addPacket)
        viewers.sendPacket(spawnEntityPacket)

        this.displayedSkinParts.triggerUpdate()
        this.teleport(currentLocation)
    }

    override fun teleport(location: Location) {
        if (!WorldManager.worlds.containsValue(location.world)) throw Exception("That world does not exist!")
        if (location.world != world) location.world.join(this, location)

        val teleportPacket = ClientboundPlayerSynchronizePositionPacket(location)
        this.sendPacket(teleportPacket)
        super.teleport(location)
        chunkViewSystem.update()
    }

    fun hasPermission(permission: String): Boolean {
        return permissionSystem.hasPermission(permission)
    }

    fun sendSelfMetadataPacket() {
        sendMetadataPacket(this)
    }

    fun clearTitle(reset: Boolean = false) {
        sendPacket(ClientboundClearTitlePacket(reset))
    }

    fun sendTitle(title: String, subtitle: String = "", fadeIn: Int = 10, stay: Int = 60, fadeOut: Int = 10) {
        val packets = mutableListOf(
            ClientboundSetSubtitlePacket(subtitle.toComponent()),
            ClientboundSetTitleTimesPacket(fadeIn, stay, fadeOut),
            ClientboundSetTitlePacket(title.toComponent()),
        )

        packets.forEach {
            this.sendPacket(it)
        }
    }

    fun respawn(isBecauseDeath: Boolean = false) {

        sendPacket(ClientboundRespawnPacket(this, ClientboundRespawnPacket.RespawnDataKept.KEEP_ALL))
        location = this.world.defaultSpawnLocation

        chunkViewSystem.resendChunks()

        refreshClientStateAfterRespawn()

        Events.dispatch(PlayerRespawnEvent(this, isBecauseDeath))
        if (isBecauseDeath) {
            isOnFire.value = false
            health.value = 20f
            food.value = 20.0
        }
    }

    fun refreshClientStateAfterRespawn() {
        sendPacket(ClientboundGameEventPacket(GameEvent.START_WAITING_FOR_CHUNKS, 1f))
        sendPacket(ClientboundChangeDifficultyPacket(world.difficulty.value, true))
        gameMode.value = gameMode.value
        sendHealthUpdatePacket()
        experienceBar.triggerUpdate()
        experienceLevel.triggerUpdate()
        inventory.sendFullInventoryUpdate()
        refreshPotionEffects()
        teleport(location)

        pose.triggerUpdate()
        refreshAbilities()
        displayedSkinParts.triggerUpdate()
        sendPacket(ClientboundPlayerSynchronizePositionPacket(location))
        sendPacketToViewers(ClientboundEntityTeleportPacket(this, location))
        updateWeatherState()
    }

    fun refreshAbilities() {
        val packet = ClientboundPlayerAbilitiesPacket(isFlying.value, isInvulnerable, canFly.value, flySpeed.value, 0.1f, this.gameMode.value)
        sendPacket(packet)
    }

    fun closeInventory() {
        if (currentlyOpenScreen != null) currentlyOpenScreen!!.dispose()

        sendPacket(ClientboundCloseInventoryPacket(0))
        sendPacket(ClientboundCloseInventoryPacket(1))
        if (inventory.cursorItem.value != ItemStack.AIR) {
            val giveSuccess = give(inventory.cursorItem.value)
            inventory.cursorItem.value = ItemStack.AIR
            if (!giveSuccess && ConfigManager.config.implementationConfig.itemDroppingAndPickup) inventory.drop(inventory.cursorItem.value)
        }
    }

    fun resetExperience() {
        experienceLevel.value = 0
        experienceBar.value = 0f
    }

    fun rebuildCommandNodeGraph() {
        this.sendPacket(ClientboundCommandsPacket(buildCommandGraph(this)))
    }

    fun updateWorldTime() {
        val time = if (time.value == -1L) world.time.value else time.value
        val packet = ClientboundUpdateTimePacket(world.worldAge, time, world.freezeTime)
        sendPacket(packet)
    }

    fun playTotemAnimation(customModelData: Float? = null) {
        val held = getHeldItem(PlayerHand.MAIN_HAND)
        if (customModelData != null) {
            val totem = ItemStack(Items.TOTEM_OF_UNDYING).withCustomModelData(customModelData)
            inventory[heldSlotIndex.value] = totem
        }
        val packet = ClientboundEntityEventPacket(this, EntityEvent.LIVING_ENTITY_PLAY_TOTEM_UNDYING_ANIMATION)
        sendPacket(packet)

        if (customModelData != null) {
            inventory[heldSlotIndex.value] = held
        }
    }

    fun setCooldown(item: Item, cooldownTicks: Int) {
        setCooldown(item.identifier, cooldownTicks)
    }

    fun setCooldown(group: String, cooldownTicks: Int) {
        val cooldown = ItemGroupCooldown(group, System.currentTimeMillis(), cooldownTicks)
        val event = ItemGroupCooldownStartEvent(this, cooldown, getPlayerEventContext(this))
        Events.dispatch(event)
        if (event.cancelled) return

        cooldownSystem.cooldowns[group] = event.cooldown
        sendPacket(SetItemCooldownPacket(event.cooldown.group, event.cooldown.durationTicks))
    }

    fun isOnCooldown(group: String): Boolean {
        return cooldownSystem.cooldowns[group] != null
    }

    fun isOnCooldown(item: Item): Boolean {
        return isOnCooldown(item.identifier)
    }

    fun breakBlock(location: Location, block: io.github.dockyardmc.world.block.Block, face: Direction) {

        val event = PlayerBlockBreakEvent(this, block, location)
        val item = this.getHeldItem(PlayerHand.MAIN_HAND)
        var cancelled = false

        Events.dispatch(event)
        if (event.cancelled) cancelled = true
        if (item.material == Items.DEBUG_STICK) cancelled = true

        if (cancelled) {
            this.world.getChunkAt(location)?.let { chunk -> this.sendPacket(chunk.packet) }
            return
        }

        this.world.setBlock(event.location, Blocks.AIR)

        BlockHandlerManager.getAllFromRegistryBlock(block.registryBlock).forEach { handler ->
            handler.onDestroy(block, world, location)
        }
        location.getNeighbours().forEach { (_, neighbourLocation) ->
            val handlers = BlockHandlerManager.getAllFromRegistryBlock(neighbourLocation.block.registryBlock)
            handlers.forEach { handler ->
                handler.onUpdateByNeighbour(neighbourLocation.block, neighbourLocation.world, neighbourLocation, block, location)
            }
        }

        this.world.players.filter { it != this }.spawnParticle(
            event.location.add(0.5, 0.5, 0.5),
            Particles.BLOCK,
            amount = 50,
            offset = Vector3f(0.3f),
            particleData = BlockParticleData(block)
        )
        this.isDigging = false
    }

    fun playChestAnimation(chestLocation: Location, animation: ChestAnimation) {
        sendPacket(ClientboundBlockActionPacket(chestLocation, 1, animation.ordinal.toByte(), location.block.registryBlock))
    }

    fun stopSound(sound: String? = null, category: SoundCategory? = null) {
        var flags = 0x0
        if (category != null) flags = flags or 0x1
        if (sound != null) flags = flags or 0x2

        sendPacket(ClientboundStopSoundPacket(flags.toByte(), category, sound))
    }

    fun stopSound(category: SoundCategory = SoundCategory.MASTER) {
        stopSound(null, category)
    }

    enum class ChestAnimation {
        CLOSE,
        OPEN
    }

    fun setVelocity(velocity: Vector3d) {
        //this.velocity = velocity //specifically NOT set, this would be wrong + simulation needs to be written, so It's accurate with client
        val packet = ClientboundSetEntityVelocityPacket(this, velocity * Vector3d(8000.0 / 20.0))
        sendPacket(packet)
        sendPacketToViewers(packet)
    }

    override fun dispose() {
        decoupledEntityViewSystemTicking.cancel()
        attributes.dispose()
        super.dispose()
    }

    fun strikeLightning(location: Location) {
        val entity = LightningBolt(location)
        entity.autoViewable = false
        world.spawnEntity(entity)
        entity.addViewer(this)
        world.scheduler.runLater(10.ticks) {
            world.despawnEntity(entity)
        }
    }

    fun updateWeatherState() {
        var raining: Boolean = world.weather.value.rain
        var thunder: Boolean = world.weather.value.thunder

        val playerWeather = clientWeather.value
        if (playerWeather != null) {
            raining = playerWeather.rain
            thunder = playerWeather.thunder
        }

        val rainPacket = if (raining) ClientboundGameEventPacket(GameEvent.START_RAINING, 1f) else ClientboundGameEventPacket(GameEvent.END_RAINING, 1f)
        val rainLevelPacket = if (raining) ClientboundGameEventPacket(GameEvent.RAIN_LEVEL_CHANGE, 1f) else ClientboundGameEventPacket(GameEvent.RAIN_LEVEL_CHANGE, 0f)
        val thunderPacket = if (thunder) ClientboundGameEventPacket(GameEvent.THUNDER_LEVEL_CHANGE, 1f) else ClientboundGameEventPacket(GameEvent.THUNDER_LEVEL_CHANGE, 0f)

        sendPacket(rainPacket)
        sendPacket(rainLevelPacket)
        sendPacket(thunderPacket)
    }
}
