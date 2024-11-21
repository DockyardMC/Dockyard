package io.github.dockyardmc.entity

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import cz.lukynka.BindableMap
import cz.lukynka.BindablePool
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.blocks.BlockIterator
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.entity.handlers.*
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.item.EquipmentSlot
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.PersistentPlayer
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.toPersistent
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.registry.AppliedPotionEffectSettings
import io.github.dockyardmc.registry.registries.DamageType
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.team.Team
import io.github.dockyardmc.team.TeamManager
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.Viewable
import io.github.dockyardmc.utils.mergeEntityMetadata
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.Vector3f
import io.github.dockyardmc.world.Chunk
import io.github.dockyardmc.world.ChunkPos
import io.github.dockyardmc.world.World
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

abstract class Entity(open var location: Location, open var world: World) : Disposable, Viewable() {

    val bindablePool = BindablePool()

    abstract var type: EntityType
    abstract var health: Bindable<Float>
    abstract var inventorySize: Int

    open var entityId: Int = EntityManager.entityIdCounter.incrementAndGet()
    open var uuid: UUID = UUID.randomUUID()
    open var velocity: Vector3 = Vector3()
    open var isInvulnerable: Boolean = false
    open var isOnGround: Boolean = true
    open var tickable: Boolean = true

    val displayName: Bindable<String?> = bindablePool.provideBindable(null)
    val metadata: BindableMap<EntityMetadataType, EntityMetadata> = bindablePool.provideBindableMap()
    val pose: Bindable<EntityPose> = bindablePool.provideBindable(EntityPose.STANDING)
    val walkSpeed: Bindable<Float> = Bindable(0.15f)
    val metadataLayers: BindableMap<PersistentPlayer, MutableMap<EntityMetadataType, EntityMetadata>> = bindablePool.provideBindableMap()
    val isOnFire: Bindable<Boolean> = bindablePool.provideBindable(false)
    val freezeTicks: Bindable<Int> = bindablePool.provideBindable(0)
    var hasNoGravity: Bindable<Boolean> = Bindable(true)

    val potionEffects: BindableMap<PotionEffect, AppliedPotionEffect> = bindablePool.provideBindableMap()
    val isInvisible: Bindable<Boolean> = bindablePool.provideBindable(false)
    val isGlowing: Bindable<Boolean> = bindablePool.provideBindable(false)

    val team: Bindable<Team?> = bindablePool.provideBindable(null)

    val equipment: BindableMap<EquipmentSlot, ItemStack> = bindablePool.provideBindableMap()
    val equipmentLayers: BindableMap<PersistentPlayer, Map<EquipmentSlot, ItemStack>> = bindablePool.provideBindableMap()

    var renderDistanceBlocks: Int = ConfigManager.config.implementationConfig.defaultEntityRenderDistanceBlocks

    var passengers: BindableList<Entity> = BindableList()
    var vehicle: Entity? = null

    val equipmentHandler = EntityEquipmentHandler(this)
    val metadataHandler = EntityMetadataHandler(this)
    val vehicleHandler = EntityVehicleHandler(this)
    val potionEffectsHandler = EntityPotionEffectsHandler(this)
    val itemPickupHandler = EntityItemPickupHandler(this)

    override var autoViewable: Boolean = true

    constructor(location: Location) : this(location, location.world)

    init {

        equipmentHandler.handle(equipment, equipmentLayers)
        vehicleHandler.handle(passengers)
        potionEffectsHandler.handle(potionEffects)
        metadataHandler.handle(
            hasNoGravity = hasNoGravity,
            entityIsOnFire = isOnFire,
            freezeTicks = freezeTicks,
            metadata = metadata,
            metadataLayers = metadataLayers,
            isGlowing = isGlowing,
            isInvisible = isInvisible,
            pose = pose
        )

        //TODO add attribute modifiers
        walkSpeed.valueChanged {}

        team.valueChanged {
            if (it.newValue != null && !TeamManager.teams.values.containsKey(it.newValue!!.name)) throw IllegalArgumentException(
                "Team ${it.newValue!!.name} is not registered!"
            )
            it.oldValue?.entities?.remove(this)
            it.newValue?.entities?.add(this)
        }
    }

    fun getCurrentChunk(): Chunk? {
        return world.chunks[getCurrentChunkPos().pack()]
    }

    fun getCurrentChunkPos(): ChunkPos {
        return ChunkPos.fromLocation(location)
    }

    fun updateEntity(player: Player, respawn: Boolean = false) {
        sendMetadataPacketToViewers()
    }

    open fun tick() {
        potionEffectsHandler.tick()
        itemPickupHandler.tick()
    }

    open fun canPickupItem(dropEntity: ItemDropEntity, item: ItemStack): Boolean {
        return false
    }

    override fun addViewer(player: Player) {
        val event = EntityViewerAddEvent(this, player)
        Events.dispatch(event)
        if (event.cancelled) return

        sendMetadataPacket(player)
        val entitySpawnPacket = ClientboundSpawnEntityPacket(entityId, uuid, type.getProtocolId(), location, location.yaw, 0, velocity)
        isOnGround = true

        synchronized(player.entityViewSystem.visibleEntities) {
            player.entityViewSystem.visibleEntities.add(this)
        }

        synchronized(viewers) {
            viewers.add(player)
        }

        player.sendPacket(entitySpawnPacket)
        sendMetadataPacket(player)
        sendMetadataPacketToViewers()
    }

    override fun removeViewer(player: Player) {

        val event = EntityViewerRemoveEvent(this, player)
        Events.dispatch(event)
        if (event.cancelled) return

        synchronized(viewers) {
            viewers.remove(player)
        }

        val entityDespawnPacket = ClientboundEntityRemovePacket(this)
        player.sendPacket(entityDespawnPacket)

        synchronized(player.entityViewSystem.visibleEntities) {
            player.entityViewSystem.visibleEntities.remove(this)
        }
    }

    //TODO move to bindable
    open fun setEntityVelocity(velocity: Vector3) {
        val packet = ClientboundSetEntityVelocityPacket(this, velocity)
        viewers.sendPacket(packet)
        sendSelfPacketIfPlayer(packet)
    }

    open fun lookAt(target: Entity) {
        val newLoc = this.location.setDirection(target.location.toVector3d() - (this.location).toVector3d())
        teleport(newLoc)
    }

    open fun lookAtClientside(target: Entity, player: Player) {
        lookAtClientside(target, listOf(player))
    }

    open fun lookAtClientside(target: Entity, players: Collection<Player>) {
        val clonedLoc = location.clone()
        val newLoc = clonedLoc.setDirection(target.location.toVector3d() - (this.location).toVector3d())
        teleportClientside(newLoc, players)
    }

    open fun sendMetadataPacketToViewers() {
        viewers.toList().forEach(this::sendMetadataPacket)
    }

    open fun sendMetadataPacket(player: Player) {
        val metadata = mergeEntityMetadata(this, metadataLayers[player.toPersistent()])
        val packet = ClientboundSetEntityMetadataPacket(this, metadata)
        player.sendPacket(packet)
    }

    open fun sendEquipmentPacket(player: Player) {
        val equipment = getMergedEquipmentData(equipment.values, equipmentLayers[player.toPersistent()])
        val packet = ClientboundSetEntityEquipmentPacket(this, equipment)
        player.sendPacket(packet)
    }

    open fun calculateBoundingBox(): BoundingBox {
        val width = type.dimensions.width
        val height = type.dimensions.height
        return BoundingBox(
            location.x - width / 2,
            location.x + width / 2,
            location.y - height / 2,
            location.y + height / 2,
            location.z - width / 2,
            location.z + width / 2
        )
    }


    open fun teleport(location: Location) {
        this.location = location
        viewers.sendPacket(ClientboundEntityTeleportPacket(this, location))
        viewers.sendPacket(ClientboundSetHeadYawPacket(this))

        if(passengers.values.isNotEmpty()) {
            viewers.sendPacket(ClientboundMoveVehiclePacket(this))
        }
    }

    open fun teleportClientside(location: Location, player: Player) {
        teleportClientside(location, listOf(player))
    }

    open fun teleportClientside(location: Location, players: Collection<Player>) {
        players.sendPacket(ClientboundEntityTeleportPacket(this, location))
        players.sendPacket(ClientboundSetHeadYawPacket(this, location))
    }

    open fun damage(damage: Float, damageType: DamageType, attacker: Entity? = null, projectile: Entity? = null) {
        val event = EntityDamageEvent(this, damage, damageType, attacker, projectile)
        Events.dispatch(event)
        if (event.cancelled) return

        var location: Location? = null
        if (attacker != null) location = attacker.location
        if (projectile != null) location = projectile.location

        if (event.damage > 0) {
            if (!isInvulnerable) {
                if (health.value - event.damage <= 0) kill() else health.value -= event.damage
            }
        }

        val packet = ClientboundDamageEventPacket(this, event.damageType, event.attacker, event.projectile, location)
        viewers.sendPacket(packet)
    }

    fun playSoundToViewers(sound: Sound, location: Location? = this.location) {
        viewers.playSound(sound, location)
    }

    open fun kill() {
        val event = EntityDeathEvent(this)
        Events.dispatch(event)
        if (event.cancelled) {
            health.value = 0.1f
            return
        }
        health.value = 0f;
    }

    data class BoundingBox(
        val minX: Double,
        val maxX: Double,
        val minY: Double,
        val maxY: Double,
        val minZ: Double,
        val maxZ: Double,
    )

    fun sendSelfPacketIfPlayer(packet: ClientboundPacket) {
        if (this is Player) this.sendPacket(packet)
    }

    fun sendSelfMetadataIfPlayer() {
        if (this is Player) sendMetadataPacket(this)
    }

    fun addPotionEffect(
        effect: PotionEffect,
        duration: Int,
        amplifier: Int = 1,
        showParticles: Boolean = false,
        showBlueBorder: Boolean = false,
        showIconOnHud: Boolean = false,
    ) {
        val potionEffect = AppliedPotionEffect(effect, AppliedPotionEffectSettings(amplifier, duration, showBlueBorder, showParticles, showIconOnHud))
        this.potionEffects[effect] = potionEffect
    }

    fun removePotionEffect(effect: PotionEffect) {
        this.potionEffects.remove(effect)
    }

    fun removePotionEffect(effect: AppliedPotionEffect) {
        this.potionEffects.remove(effect.effect)
    }

    fun clearPotionEffects() {
        this.potionEffects.clear()
    }

    fun refreshPotionEffects() {
        viewers.forEach(::sendPotionEffectsPacket)
        if (this is Player) this.sendPotionEffectsPacket(this)
    }

    fun sendPotionEffectsPacket(player: Player) {
        potionEffects.values.values.forEach {
            val packet = ClientboundEntityEffectPacket(
                this,
                it.effect,
                it.settings.amplifier,
                it.settings.duration,
                it.settings.showParticles,
                it.settings.isAmbient,
                it.settings.showIcon
            )
            player.sendPacket(packet)
        }
    }

    fun placeBlock(location: Location, block: Block) {

    }

    fun interact() {

    }

    fun breakBlock() {

    }

    fun getTargetBlock(maxDistance: Int): Location? {
        val it: Iterator<Vector3> = BlockIterator(this, maxDistance)
        while (it.hasNext()) {
            val position: Location = it.next().toLocation(world)
            if (world.getBlock(position).isAir()) return position
        }
        return null
    }


    fun getFacingDirectionVector(): Vector3f {
        val yawRadians = Math.toRadians(location.yaw.toDouble())
        val pitchRadians = Math.toRadians(location.pitch.toDouble())

        val x = -sin(yawRadians) * cos(pitchRadians)
        val y = -sin(pitchRadians)
        val z = cos(yawRadians) * cos(pitchRadians)

        return Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun dismountCurrentVehicle() {
        if(vehicle != null && vehicle!!.passengers.contains(this)) {
            vehicle!!.passengers.remove(this)
        }
    }

    override fun dispose() {
        team.value = null
        equipmentLayers.clear()
        viewers.toList().forEach { removeViewer(it) }
        metadataLayers.clear()
        bindablePool.dispose()
        EntityManager.despawnEntity(this)
    }
}