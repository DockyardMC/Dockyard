package io.github.dockyardmc.player

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.commands.buildCommandGraph
import io.github.dockyardmc.entity.*
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.inventory.ContainerInventory
import io.github.dockyardmc.inventory.PlayerInventory
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.particles.BlockParticleData
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.systems.*
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.registries.DamageType
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.resourcepack.Resourcepack
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.scheduler.GlobalScheduler
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.ui.DrawableContainerScreen
import io.github.dockyardmc.utils.*
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.Vector3f
import io.github.dockyardmc.world.PlayerChunkEngine
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import io.netty.channel.ChannelHandlerContext
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
    val networkManager: PlayerNetworkManager
): Entity(location) {
    override var velocity: Vector3 = Vector3(0, 0, 0)
    override var isInvulnerable: Boolean = true
    override var isOnGround: Boolean = true
    override var health: Bindable<Float> = bindablePool.provideBindable(20f)
    override var inventorySize: Int = 35

    var brand: String = "minecraft:vanilla"
    var profile: ProfilePropertyMap? = null
    var clientConfiguration: ClientConfiguration? = null

    var isSneaking: Boolean = false
    var isSprinting: Boolean = false
    var isFullyInitialized: Boolean = false
    var isConnected: Boolean = true
    var isDigging: Boolean = false

    var gameMode: Bindable<GameMode> = bindablePool.provideBindable(GameMode.ADVENTURE)
    var isFlying: Bindable<Boolean> = bindablePool.provideBindable(false)
    var canFly: Bindable<Boolean> = bindablePool.provideBindable(false)
    val flySpeed: Bindable<Float> = bindablePool.provideBindable(0.05f)

    val permissions: BindableList<String> = bindablePool.provideBindableList()

    var inventory: PlayerInventory = PlayerInventory(this)
    var heldSlotIndex: Bindable<Int> = bindablePool.provideBindable(0)
    val mainHandItem: Bindable<ItemStack> = bindablePool.provideBindable(ItemStack.AIR)
    val offHandItem: Bindable<ItemStack> = bindablePool.provideBindable(ItemStack.AIR)

    var displayedSkinParts: BindableList<DisplayedSkinPart> = bindablePool.provideBindableList(DisplayedSkinPart.CAPE, DisplayedSkinPart.JACKET, DisplayedSkinPart.LEFT_PANTS, DisplayedSkinPart.RIGHT_PANTS, DisplayedSkinPart.LEFT_SLEEVE, DisplayedSkinPart.RIGHT_SLEEVE, DisplayedSkinPart.HAT)
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

    val cooldownSystem = CooldownSystem(this)
    val foodEatingSystem = FoodEatingSystem(this)
    val chunkEngine = PlayerChunkEngine(this)
    val gameModeSystem = GameModeSystem(this)
    val playerInfoSystem = PlayerInfoSystem(this)
    val entityViewSystem = EntityViewSystem(this)

    val decoupledViewSystemScheduler = GlobalScheduler("${username}-view-engine-scheduler")

    val resourcepacks: MutableMap<String, Resourcepack> = mutableMapOf()

    var lastInteractionTime: Long = -1L
    var currentOpenInventory: ContainerInventory? = null
    var itemInUse: ItemInUse? = null

    lateinit var lastSentPacket: ClientboundPacket


    override fun toString(): String = username

    init {

        gameModeSystem.handle(gameMode)
        playerInfoSystem.handle(displayName, isListed)

        mainHandItem.valueChanged { setHeldItem(PlayerHand.MAIN_HAND, it.newValue) }
        offHandItem.valueChanged { setHeldItem(PlayerHand.OFF_HAND, it.newValue) }

        heldSlotIndex.valueChanged {
            this.sendPacket(ClientboundSetHeldItemPacket(it.newValue))
            val item = inventory[it.newValue]
            equipment[EquipmentSlot.MAIN_HAND] = item
        }

        isFlying.valueChanged { this.sendPacket(ClientboundPlayerAbilitiesPacket(it.newValue, isInvulnerable, canFly.value, flySpeed.value)) }
        canFly.valueChanged { this.sendPacket(ClientboundPlayerAbilitiesPacket(isFlying.value, isInvulnerable, it.newValue, flySpeed.value)) }

        fovModifier.valueChanged { this.sendPacket(ClientboundPlayerAbilitiesPacket(isFlying.value, isInvulnerable, canFly.value, flySpeed.value, it.newValue)) }

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
        }

        experienceBar.valueChanged { sendUpdateExperiencePacket() }
        experienceLevel.valueChanged { sendUpdateExperiencePacket() }
        time.valueChanged { updateWorldTime() }

        hasNoGravity.value = false

        // Keep this decoupled from world scheduler so when world ticking is paused or slowed down
        // it doesn't make entity and chunk loading slow/impossible
        decoupledViewSystemScheduler.runRepeating(1.ticks) {
            entityViewSystem.tick()
        }
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
        val namePacket = ClientboundPlayerInfoUpdatePacket(PlayerInfoUpdate(uuid, SetDisplayNameInfoUpdateAction(displayName.value)))
        player.sendPacket(namePacket)

        super.addViewer(player)

        player.sendMetadataPacket(this)
        this.displayedSkinParts.triggerUpdate()
        sendMetadataPacket(player)
        sendEquipmentPacket(player)
        player.sendPacket(ClientboundPlayerInfoUpdatePacket(PlayerInfoUpdate(uuid, SetListedInfoUpdateAction(isListed.value))))
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
        if(player == this) return
//        //
//        if(isDisconnect) {
//            val playerRemovePacket = ClientboundPlayerInfoRemovePacket(this)
//            player.sendPacket(playerRemovePacket)
//        }
        viewers.remove(player)
        super.removeViewer(player)
    }

    // Hold messages client receives before state is PLAY, then send them after state changes to PLAY
    private var queuedMessages = mutableListOf<Pair<Component, Boolean>>()
    fun releaseMessagesQueue() {
        queuedMessages.forEach { sendSystemMessage(it.first, it.second) }
        queuedMessages.clear()
    }


    fun kick(reason: String) { this.kick(reason.toComponent()) }
    fun kick(reason: Component) { sendPacket(ClientboundDisconnectPacket(reason)) }

    fun sendMessage(message: String) { this.sendMessage(message.toComponent()) }
    fun sendMessage(component: Component) { sendSystemMessage(component, false) }
    fun sendActionBar(message: String) { this.sendActionBar(message.toComponent()) }
    fun sendActionBar(component: Component) { sendSystemMessage(component, true) }

    private fun sendSystemMessage(component: Component, isActionBar: Boolean) {
        if(!isConnected) return
        if(networkManager.state != ProtocolState.PLAY) {
            queuedMessages.add(component to isActionBar)
            return
        }
        this.sendPacket(ClientboundSystemChatMessagePacket(component, isActionBar))
    }

    fun sendPacket(packet: ClientboundPacket) {
        if(!isConnected) return
        if(packet.state != networkManager.state) return
        connection.sendPacket(packet, networkManager)
        lastSentPacket = packet
    }

    fun sendToViewers(packet: ClientboundPacket) {
        viewers.toList().forEach { viewer ->
            if(networkManager.state != ProtocolState.PLAY) return@forEach
            viewer.sendPacket(packet)
        }
    }

    override fun teleport(location: Location) {
        if(!WorldManager.worlds.containsValue(location.world)) throw Exception("That world does not exist!")
        if(location.world != world) location.world.join(this)

        val teleportPacket = ClientboundPlayerSynchronizePositionPacket(location)
        this.sendPacket(teleportPacket)
        super.teleport(location)
        chunkEngine.update()
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

        chunkEngine.resendChunks()

        refreshClientStateAfterRespawn()

        Events.dispatch(PlayerRespawnEvent(this, isBecauseDeath))
        if(isBecauseDeath) {
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
    }

    fun refreshAbilities() {
        val packet = ClientboundPlayerAbilitiesPacket(isFlying.value, isInvulnerable, canFly.value, flySpeed.value, 0.1f, this.gameMode.value)
        sendPacket(packet)
    }

    fun closeInventory() {
        sendPacket(ClientboundCloseInventoryPacket(0))
        sendPacket(ClientboundCloseInventoryPacket(1))
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
        val packet = ClientboundUpdateTimePacket(world.worldAge, time, world.freezeTime)
        sendPacket(packet)
    }

    fun openInventory(inventory: ContainerInventory) {
        this.currentOpenInventory = inventory
        sendPacket(ClientboundOpenContainerPacket(InventoryType.valueOf("GENERIC_9X${inventory.rows}"), inventory.name))
        inventory.contents.forEach {
            sendPacket(ClientboundSetContainerSlotPacket(it.key, it.value))
        }
        if(inventory is DrawableContainerScreen) {
            inventory.slots.triggerUpdate()
            inventory.onOpen(this)
        }
    }

    fun playTotemAnimation(customModelData: Int? = null) {
        val held = getHeldItem(PlayerHand.MAIN_HAND)
        if(customModelData != null) {
            val totem = ItemStack(Items.TOTEM_OF_UNDYING)
            totem.customModelData.value = customModelData
            inventory[heldSlotIndex.value] = totem
        }
        val packet = ClientboundEntityEventPacket(this, EntityEvent.PLAYER_PLAY_TOTEM_ANIMATION)
        sendPacket(packet)

        if(customModelData != null) {
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
        if(event.cancelled) return

        cooldownSystem.cooldowns[group] = event.cooldown
        sendPacket(SetItemCooldownPacket(event.cooldown.group, event.cooldown.durationTicks))
    }

    fun isOnCooldown(group: String): Boolean {
        return cooldownSystem.cooldowns[group] != null
    }

    fun isOnCooldown(item: Item): Boolean {
        return isOnCooldown(item.identifier)
    }

    fun breakBlock(location: Location, block: Block, face: Direction) {

        val event = PlayerBlockBreakEvent(this, block, location)
        val item = this.getHeldItem(PlayerHand.MAIN_HAND)
        var cancelled = false

        Events.dispatch(event)
        if (event.cancelled) cancelled = true
        if (item.material == Items.DEBUG_STICK) cancelled = true

        if(cancelled) {
            this.world.getChunkAt(location)?.let { this.sendPacket(it.packet) }
            return
        }

        this.world.setBlock(event.location, Blocks.AIR)
        this.world.players.filter { it != this }.spawnParticle(
            event.location.add(0.5, 0.5, 0.5),
            Particles.BLOCK,
            amount = 50,
            offset = Vector3f(0.3f),
            particleData = BlockParticleData(block)
        )
        this.isDigging = false
    }

    override fun dispose() {
        decoupledViewSystemScheduler.dispose()
        super.dispose()
    }
}