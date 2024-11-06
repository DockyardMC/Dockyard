package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import cz.lukynka.BindableMap
import cz.lukynka.BindablePool
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.blocks.BlockIterator
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.effects.PotionEffectImpl
import io.github.dockyardmc.entities.EntityManager.despawnEntity
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
import io.github.dockyardmc.registry.registries.DamageType
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.team.Team
import io.github.dockyardmc.team.TeamManager
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.mergeEntityMetadata
import io.github.dockyardmc.utils.ticksToMs
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.Vector3f
import io.github.dockyardmc.world.World
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

abstract class Entity(open var location: Location, open var world: World) : Disposable {

    val bindablePool = BindablePool()

    open var entityId: Int = EntityManager.entityIdCounter.incrementAndGet()
    open var uuid: UUID = UUID.randomUUID()
    abstract var type: EntityType
    open var velocity: Vector3 = Vector3()
    val viewers: MutableSet<Player> = mutableSetOf()
    open var isInvulnerable: Boolean = false
    val displayName: Bindable<String?> = bindablePool.provideBindable(null)
    open var isOnGround: Boolean = true
    val metadata: BindableMap<EntityMetadataType, EntityMetadata> = bindablePool.provideBindableMap()
    val pose: Bindable<EntityPose> = bindablePool.provideBindable(EntityPose.STANDING)
    abstract var health: Bindable<Float>
    abstract var inventorySize: Int
    val potionEffects: BindableMap<PotionEffect, AppliedPotionEffect> = bindablePool.provideBindableMap()
    val walkSpeed: Bindable<Float> = Bindable(0.15f)
    open var tickable: Boolean = true
    val metadataLayers: BindableMap<PersistentPlayer, MutableMap<EntityMetadataType, EntityMetadata>> =
        bindablePool.provideBindableMap()
    val isGlowing: Bindable<Boolean> = bindablePool.provideBindable(false)
    val isInvisible: Bindable<Boolean> = bindablePool.provideBindable(false)
    val team: Bindable<Team?> = bindablePool.provideBindable(null)
    val isOnFire: Bindable<Boolean> = bindablePool.provideBindable(false)
    val freezeTicks: Bindable<Int> = bindablePool.provideBindable(0)
    val equipment: BindableMap<EquipmentSlot, ItemStack> = bindablePool.provideBindableMap()
    val equipmentLayers: BindableMap<PersistentPlayer, Map<EquipmentSlot, ItemStack>> =
        bindablePool.provideBindableMap()
    var renderDistanceBlocks: Int = ConfigManager.config.implementationConfig.defaultEntityRenderDistanceBlocks
    var autoViewable: Boolean = true
    var hasNoGravity: Bindable<Boolean> = Bindable(true)

    constructor(location: Location) : this(location, location.world)

    init {

        hasNoGravity.valueChanged {
            val noGravityType = EntityMetadataType.HAS_NO_GRAVITY
            metadata[noGravityType] = EntityMetadata(noGravityType, EntityMetaValue.BOOLEAN, it.newValue)
        }

        equipment.itemSet {
            if (this !is Player) return@itemSet
            this.inventory.unsafeUpdateEquipmentSlot(it.key, this.heldSlotIndex.value, it.value)
        }

        equipment.mapUpdated {
            if (this is Player) sendEquipmentPacket(this)
            viewers.forEach { viewer -> sendEquipmentPacket(viewer) }
        }

        equipmentLayers.itemSet {
            val player = it.key.toPlayer()
            if (player != null) sendEquipmentPacket(player)
        }

        equipmentLayers.itemRemoved {
            val player = it.key.toPlayer()
            if (player != null) sendEquipmentPacket(player)
        }

        isOnFire.valueChanged {
            val meta = getEntityMetadataState(this) {
                isOnFire = it.newValue
            }
            metadata[EntityMetadataType.STATE] = meta
        }

        freezeTicks.valueChanged {
            val meta = EntityMetadata(EntityMetadataType.FROZEN_TICKS, EntityMetaValue.VAR_INT, it.newValue)
            metadata[EntityMetadataType.FROZEN_TICKS] = meta
        }

        metadata.mapUpdated {
            sendMetadataPacketToViewers()
            sendSelfMetadataIfPlayer()
        }

        metadata.itemSet {
            sendMetadataPacketToViewers()
            sendSelfMetadataIfPlayer()
        }

        isGlowing.valueChanged {
            metadata[EntityMetadataType.STATE] = getEntityMetadataState(this)
        }

        isInvisible.valueChanged {
            metadata[EntityMetadataType.STATE] = getEntityMetadataState(this)
        }

        metadataLayers.itemSet {
            val player = it.key.toPlayer()
            if (player != null) sendMetadataPacket(player)
        }

        metadataLayers.itemRemoved {
            val player = it.key.toPlayer()
            if (player != null) sendMetadataPacket(player)
        }

        pose.valueChanged {
            metadata[EntityMetadataType.POSE] =
                EntityMetadata(EntityMetadataType.POSE, EntityMetaValue.POSE, it.newValue)
        }

        //TODO add attribute modifiers
        walkSpeed.valueChanged {}

        potionEffects.itemSet {
            it.value.startTime = System.currentTimeMillis()
            val packet = ClientboundEntityEffectPacket(
                this,
                it.value.effect,
                it.value.level,
                it.value.duration,
                it.value.showParticles,
                it.value.showBlueBorder,
                it.value.showIconOnHud
            )

            viewers.sendPacket(packet)
            sendSelfPacketIfPlayer(packet)
            PotionEffectImpl.onEffectApply(this, it.value.effect)
        }

        potionEffects.itemRemoved {
            val packet = ClientboundRemoveEntityEffectPacket(this, it.value)
            viewers.sendPacket(packet)
            PotionEffectImpl.onEffectRemoved(this, it.value.effect)
            sendSelfPacketIfPlayer(packet)
        }

        team.valueChanged {
            if (it.newValue != null && !TeamManager.teams.values.containsKey(it.newValue!!.name)) throw IllegalArgumentException(
                "Team ${it.newValue!!.name} is not registered!"
            )
            it.oldValue?.entities?.remove(this)
            it.newValue?.entities?.add(this)
        }
    }

    fun updateEntity(player: Player, respawn: Boolean = false) {
        sendMetadataPacketToViewers()
    }

    open fun tick(ticks: Int) {
        potionEffects.values.forEach {
            if (System.currentTimeMillis() >= it.value.startTime!! + ticksToMs(it.value.duration)) {
                potionEffects.remove(it.key)
            }
        }

        // try to pickup items
        if(!ConfigManager.config.implementationConfig.itemDroppingAndPickup) return
        val drops = world.entities.filterIsInstance<ItemDropEntity>()
        if (inventorySize <= 0) return
        drops.toList().forEach { drop ->
            if (this is Player && !drop.viewers.contains(this)) return@forEach
            if (!drop.canBePickedUp) return@forEach
            if (drop.location.distance(location) > drop.pickupDistance) return@forEach

            val itemStack = drop.itemStack.value

            val eventContext = Event.Context(
                setOf(),
                setOf(drop, this),
                setOf(this.world),
                setOf(this.location, drop.location)
            )
            val event = EntityPickupItemEvent(this, itemStack, eventContext)
            if (event.cancelled) return@forEach

            if (canPickupItem(drop, itemStack)) {
                val mutualViewers = drop.viewers.filter { viewers.contains(it) }
                if (drop.pickupAnimation) {
                    val packet = ClientboundPickupItemPacket(drop, this, itemStack)
                    mutualViewers.sendPacket(packet)
                    if (this is Player) this.sendPacket(packet)
                }
                drop.world.despawnEntity(drop)
            }
        }
    }

    open fun canPickupItem(dropEntity: ItemDropEntity, item: ItemStack): Boolean {
        return false
    }

    open fun addViewer(player: Player) {
        val event = EntityViewerAddEvent(this, player)
        Events.dispatch(event)
        if (event.cancelled) return

        sendMetadataPacket(player)
        val entitySpawnPacket =
            ClientboundSpawnEntityPacket(entityId, uuid, type.getProtocolId(), location, location.yaw, 0, velocity)
        isOnGround = true

        synchronized(player.visibleEntities) {
            player.visibleEntities.add(this)
        }
        synchronized(viewers) {
            viewers.add(player)
        }
        player.sendPacket(entitySpawnPacket)
        sendMetadataPacket(player)
        sendMetadataPacketToViewers()
    }

    open fun removeViewer(player: Player, isDisconnect: Boolean) {

        val event = EntityViewerRemoveEvent(this, player)
        Events.dispatch(event)
        if (event.cancelled) return

        synchronized(viewers) {
            viewers.remove(player)
        }

        val entityDespawnPacket = ClientboundEntityRemovePacket(this)
        player.sendPacket(entityDespawnPacket)

        synchronized(player.visibleEntities) {
            player.visibleEntities.remove(this)
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

    private fun sendSelfPacketIfPlayer(packet: ClientboundPacket) {
        if (this is Player) this.sendPacket(packet)
    }

    private fun sendSelfMetadataIfPlayer() {
        if (this is Player) sendMetadataPacket(this)
    }

    fun addPotionEffect(
        effect: PotionEffect,
        duration: Int,
        level: Int = 1,
        showParticles: Boolean = false,
        showBlueBorder: Boolean = false,
        showIconOnHud: Boolean = false,
    ) {
        val potionEffect = AppliedPotionEffect(effect, duration, level, showParticles, showBlueBorder, showIconOnHud)
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
                it.level,
                it.duration,
                it.showParticles,
                it.showBlueBorder,
                it.showIconOnHud
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

    override fun dispose() {
        team.value = null
        equipmentLayers.clear()
        viewers.toList().forEach { removeViewer(it, false) }
        metadataLayers.clear()
        bindablePool.dispose()
        EntityManager.despawnEntity(this)
    }
}