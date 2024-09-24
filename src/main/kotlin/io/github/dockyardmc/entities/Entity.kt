package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import cz.lukynka.BindableMap
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.effects.PotionEffectImpl
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.PersistentPlayer
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.toPersistent
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.*
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

abstract class Entity(open var location: Location, open var world: World): Disposable {
    open var entityId: Int = EntityManager.entityIdCounter.incrementAndGet()
    open var uuid: UUID = UUID.randomUUID()
    abstract var type: EntityType
    open var velocity: Vector3 = Vector3()
    val viewers: MutableSet<Player> = mutableSetOf()
    open var hasGravity: Boolean = true
    open var isInvulnerable: Boolean = false
    open var hasCollision: Boolean = true
    open var displayName: String = this::class.simpleName.toString()
    open var isOnGround: Boolean = true
    val metadata: BindableMap<EntityMetadataType, EntityMetadata> = BindableMap()
    val pose: Bindable<EntityPose> = Bindable(EntityPose.STANDING)
    abstract var health: Bindable<Float>
    abstract var inventorySize: Int
    val potionEffects: BindableMap<PotionEffect, AppliedPotionEffect> = BindableMap()
    val walkSpeed: Bindable<Float> = Bindable(0.15f)
    open var tickable: Boolean = true
    val metadataLayers: BindableMap<PersistentPlayer, MutableMap<EntityMetadataType, EntityMetadata>> = BindableMap()
    val isGlowing: Bindable<Boolean> = Bindable(false)
    val isInvisible: Bindable<Boolean> = Bindable(false)
    val team: Bindable<Team?> = Bindable(null)
    val isOnFire: Bindable<Boolean> = Bindable(false)
    val freezeTicks: Bindable<Int> = Bindable(0)
    val equipment: Bindable<EntityEquipment> = Bindable(EntityEquipment())
    val equipmentLayers: BindableMap<PersistentPlayer, EntityEquipmentLayer> = BindableMap()
    var renderDistanceBlocks: Int = ConfigManager.config.implementationConfig.defaultEntityRenderDistanceBlocks
    var autoViewable: Boolean = true

    constructor(location: Location): this(location, location.world)

    init {

        equipment.valueChanged { viewers.forEach(::sendEquipmentPacket) }

        equipmentLayers.itemSet {
            val player = it.key.toPlayer()
            if(player != null) sendEquipmentPacket(player)
        }

        equipmentLayers.itemRemoved {
            val player = it.key.toPlayer()
            if(player != null) sendEquipmentPacket(player)
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
            if(player != null) sendMetadataPacket(player)
        }

        metadataLayers.itemRemoved {
            val player = it.key.toPlayer()
            if(player != null) sendMetadataPacket(player)
        }

        pose.valueChanged {
            metadata[EntityMetadataType.POSE] = EntityMetadata(EntityMetadataType.POSE, EntityMetaValue.POSE, it.newValue)
        }

        //TODO add attribute modifiers
        walkSpeed.valueChanged {}

        potionEffects.itemSet {
            it.value.startTime = System.currentTimeMillis()
            val packet = ClientboundEntityEffectPacket(this, it.value.effect, it.value.level, it.value.duration, it.value.showParticles, it.value.showBlueBorder, it.value.showIconOnHud)

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
            if(it.newValue != null && !TeamManager.teams.values.containsKey(it.newValue!!.name)) throw IllegalArgumentException("Team ${it.newValue!!.name} is not registered!")
            this.team.value?.entities?.remove(this)
            it.newValue?.entities?.add(this)
        }
    }

    override fun dispose() {
        autoViewable = false
        team.value?.entities?.remove(this)
        team.value = null
        equipmentLayers.clear()
        viewers.iterator().forEach { removeViewer(it, false) }
        metadataLayers.clear()
        EntityManager.entities.remove(this)
        world.entities.remove(this)
    }

    fun updateEntity(player: Player, respawn: Boolean = false) {
        sendMetadataPacketToViewers()
    }

    open fun tick() {
        potionEffects.values.forEach {
            if(System.currentTimeMillis() >= it.value.startTime!! + ticksToMs(it.value.duration)) {
                potionEffects.remove(it.key)
            }
        }
    }

    open fun addViewer(player: Player) {
        val event = EntityViewerAddEvent(this, player)
        Events.dispatch(event)
        if(event.cancelled) return

        sendMetadataPacket(player)
        val entitySpawnPacket = ClientboundSpawnEntityPacket(entityId, uuid, type.id, location, location.yaw, 0, velocity)
        isOnGround = true

        player.visibleEntities.add(this)
        viewers.add(player)
        player.sendPacket(entitySpawnPacket)
        sendMetadataPacket(player)
        sendMetadataPacketToViewers()
    }

    open fun removeViewer(player: Player, isDisconnect: Boolean) {

        val event = EntityViewerRemoveEvent(this, player)
        Events.dispatch(event)
        if(event.cancelled) return

        viewers.remove(player)
        val entityDespawnPacket = ClientboundEntityRemovePacket(this)
        player.sendPacket(entityDespawnPacket)
        player.visibleEntities.remove(this)
    }

    //TODO move to bindable
    open fun setEntityVelocity(velocity: Vector3) {
        val packet = ClientboundSetVelocityPacket(this, velocity)
        viewers.sendPacket(packet)
        sendSelfPacketIfPlayer(packet)
    }

    open fun lookAt(target: Entity) {
        val newLoc = this.location.setDirection(target.location.toVector3d() - (this.location).toVector3d())
        teleport(newLoc)
    }

    open fun sendMetadataPacketToViewers() {
        viewers.forEach(this::sendMetadataPacket)
    }

    open fun sendMetadataPacket(player: Player) {
        val metadata = mergeEntityMetadata(this, metadataLayers[player.toPersistent()])
        val packet = ClientboundSetEntityMetadataPacket(this, metadata)
        player.sendPacket(packet)
    }

    open fun sendEquipmentPacket(player: Player) {
        val equipment = getMergedEquipmentData(equipment.value, equipmentLayers[player.toPersistent()])
        val packet = ClientboundSetEquipmentPacket(this, equipment)
        player.sendPacket(packet)
    }

    open fun calculateBoundingBox(): BoundingBox {
        val width = type.width
        val height = type.height
        return BoundingBox(
            location.x - width / 2,
            location.x + width / 2,
            location.y - height / 2,
            location.y + height /2,
            location.z - width / 2,
            location.z + width / 2
        )
    }

    open fun teleport(location: Location) {
        this.location = location
        viewers.sendPacket(ClientboundEntityTeleportPacket(this, location))
        viewers.sendPacket(ClientboundSetHeadYawPacket(this))
    }

    open fun damage(damage: Float, damageType: DamageType, attacker: Entity? = null, projectile: Entity? = null) {
        val event = EntityDamageEvent(this, damage, damageType, attacker, projectile)
        Events.dispatch(event)
        if(event.cancelled) return

        var location: Location? = null
        if(attacker != null) location = attacker.location
        if(projectile != null) location = projectile.location

        if(event.damage > 0) {
            if(!isInvulnerable) {
                if(health.value - event.damage <= 0) kill() else health.value -= event.damage
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
        if(event.cancelled) {
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
        val maxZ: Double
    )

    private fun sendSelfPacketIfPlayer(packet: ClientboundPacket) {
        if(this is Player) this.sendPacket(packet)
    }

    private fun sendSelfMetadataIfPlayer() {
        if(this is Player) sendMetadataPacket(this)
    }

    fun placeBlock(location: Location, block: Block) {

    }

    fun interact() {

    }

    fun breakBlock() {

    }

    fun getFacingDirectionVector(): Vector3f {
        val yawRadians = Math.toRadians(location.yaw.toDouble())
        val pitchRadians = Math.toRadians(location.pitch.toDouble())

        val x = -sin(yawRadians) * cos(pitchRadians)
        val y = -sin(pitchRadians)
        val z = cos(yawRadians) * cos(pitchRadians)

        return Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
    }

}