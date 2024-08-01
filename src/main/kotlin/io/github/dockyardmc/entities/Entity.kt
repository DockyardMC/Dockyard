package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.*
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.team.Team
import io.github.dockyardmc.team.TeamManager
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.ticksToMs
import io.github.dockyardmc.utils.toVector3f
import io.github.dockyardmc.world.World
import java.util.UUID

abstract class Entity {
    open var entityId: Int = EntityManager.entityIdCounter.incrementAndGet()
    open var uuid: UUID = UUID.randomUUID()
    abstract var type: EntityType
    abstract var location: Location
    open var velocity: Vector3 = Vector3()
    val viewers: MutableList<Player> = mutableListOf()
    open var hasGravity: Boolean = true
    open var isInvulnerable: Boolean = false
    open var hasCollision: Boolean = true
    abstract var world: World
    open var displayName: String = this::class.simpleName.toString()
    open var isOnGround: Boolean = true
    val metadata: BindableList<EntityMetadata> = BindableList()
    val pose: Bindable<EntityPose> = Bindable(EntityPose.STANDING)
    abstract var health: Bindable<Float>
    abstract var inventorySize: Int
    val potionEffects: BindableList<AppliedPotionEffect> = BindableList()
    val walkSpeed: Bindable<Float> = Bindable(0.15f)
    open var tickable: Boolean = true

    init {

        pose.valueChanged {
            metadata.addOrUpdate(EntityMetadata(EntityMetaIndex.POSE, EntityMetadataType.POSE, it.newValue))
            sendMetadataPacketToViewers()
            sendSelfMetadataIfPlayer()
        }

        //TODO add attribute modifiers
        walkSpeed.valueChanged {}

        potionEffects.itemAdded {
            val existingPotionEffect = potionEffects.values.firstOrNull { filter -> filter.effect == it.item.effect }
            if(existingPotionEffect != null) potionEffects.setIndex(potionEffects.values.indexOf(existingPotionEffect), it.item)
            it.item.startTime = System.currentTimeMillis()

            val packet = ClientboundEntityEffectPacket(this, it.item.effect, it.item.level, it.item.duration, it.item.showParticles, it.item.showBlueBorder, it.item.showIconOnHud)
            viewers.sendPacket(packet)
            sendSelfPacketIfPlayer(packet)
        }

        potionEffects.itemRemoved {
            val packet = ClientboundRemoveEntityEffectPacket(this, it.item)
            viewers.sendPacket(packet)
            sendSelfPacketIfPlayer(packet)
        }
    }

    open fun tick() {
        potionEffects.values.forEach {
            if(System.currentTimeMillis() >= it.startTime!! + ticksToMs(it.duration)) {
                potionEffects.remove(it)
            }
        }
    }

    var team: Team? = null
        set(value) {
            require(value in TeamManager.teams.values) { "This team is not registered!" }

            this.team?.entities?.remove(this)
            field = value
            value?.entities?.addIfNotPresent(this)
        }

    open fun addViewer(player: Player) {
        val event = EntityViewerAddEvent(this, player)
        Events.dispatch(event)
        if(event.cancelled) return

        val entitySpawnPacket = ClientboundSpawnEntityPacket(entityId, uuid, type.id, location, location.yaw, 0, velocity)
        val metadataPacket = ClientboundSetEntityMetadataPacket(this)

        viewers.add(player)
        player.sendPacket(entitySpawnPacket)
        player.sendPacket(metadataPacket)
        sendMetadataPacketToViewers()
    }

    open fun removeViewer(player: Player, isDisconnect: Boolean) {
        val event = EntityViewerRemoveEvent(this, player)
        Events.dispatch(event)
        if(event.cancelled) return

        viewers.remove(player)
        val entityDespawnPacket = ClientboundEntityRemovePacket(this)
        player.sendPacket(entityDespawnPacket)
    }

    //TODO move to bindable
    open fun setEntityVelocity(velocity: Vector3) {
        val packet = ClientboundSetVelocityPacket(this, velocity)
        viewers.sendPacket(packet)
        sendSelfPacketIfPlayer(packet)

    }

    //TODO make this work
    open fun lookAt(target: Entity) {
        val newLoc = this.location.setDirection(target.location.subtract(this.location).toVector3f())
        this.location = newLoc

        this.location.yaw = (newLoc.yaw % 360) * 256 / 360
        val packet = ClientboundEntityTeleportPacket(this)
        viewers.sendPacket(packet)
    }

    open fun sendMetadataPacketToViewers() {
        val packet = ClientboundSetEntityMetadataPacket(this)
        viewers.sendPacket(packet)
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
        if(this is Player) this.sendPacket(ClientboundSetEntityMetadataPacket(this))
    }


    fun placeBlock(location: Location, block: Block) {

    }

    fun interact() {

    }

    fun breakBlock() {

    }

}