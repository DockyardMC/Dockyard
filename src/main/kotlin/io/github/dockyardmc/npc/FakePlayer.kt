package io.github.dockyardmc.npc

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableDispatcher
import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.apis.Hologram
import io.github.dockyardmc.apis.hologram
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.entity.metadata.EntityMetaValue
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.EntityMetadataType
import io.github.dockyardmc.events.*
import io.github.dockyardmc.events.system.EventFilter
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerAnimationPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoRemovePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoUpdatePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityAnimation
import io.github.dockyardmc.protocol.types.GameProfile
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.scheduler.SchedulerTask
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.team.Team
import io.github.dockyardmc.team.TeamManager
import io.github.dockyardmc.utils.MojangUtil
import java.util.*
import java.util.concurrent.CompletableFuture

class FakePlayer(location: Location, val name: String = UUID.randomUUID().toString().substring(0, 16)) : Entity(location) {
    override var type: EntityType = EntityTypes.PLAYER
    override val health: Bindable<Float> = bindablePool.provideBindable(20f)
    override var inventorySize: Int = 35
    val gameProfile = GameProfile(uuid, name)

    private val eventPool = EventPool(Events, "FakePlayer listeners").withFilter(EventFilter.containsEntity(this))

    val isListed: Bindable<Boolean> = bindablePool.provideBindable(false)
    val skin: Bindable<GameProfile.Property?> = bindablePool.provideBindable(null)
    val displayedSkinParts: BindableList<DisplayedSkinPart> = bindablePool.provideBindableList(DisplayedSkinPart.CAPE, DisplayedSkinPart.JACKET, DisplayedSkinPart.LEFT_PANTS, DisplayedSkinPart.RIGHT_PANTS, DisplayedSkinPart.LEFT_SLEEVE, DisplayedSkinPart.RIGHT_SLEEVE, DisplayedSkinPart.HAT)
    val hasCollision: Bindable<Boolean> = bindablePool.provideBindable(true)
    val teamColor: Bindable<LegacyTextColor> = bindablePool.provideBindable(LegacyTextColor.WHITE)
    val mirrorsSkin: Bindable<Boolean> = bindablePool.provideBindable(false)
    var lookClose: LookCloseType = LookCloseType.NONE
    var lookCloseDistance: Int = 5

    val npcTeam = TeamManager.create("npc-$uuid", teamColor.value, Team.NameTagVisibility.HIDDEN, getTeamCollision())
    val hologram = hologram(this.location) {}

    val onTick: BindableDispatcher<Unit> = bindablePool.provideBindableDispatcher()
    val onClick: BindableDispatcher<Pair<Player, ClickType>> = bindablePool.provideBindableDispatcher()

    val tickTask: SchedulerTask = world.scheduler.runRepeating(1.ticks) {
        onTick.dispatch(Unit)
        updateLookClose()
    }

    enum class ClickType {
        RIGHT,
        LEFT,
        MIDDLE
    }

    enum class LookCloseType {
        NONE,
        NORMAL,
        CLIENT_SIDE,
    }

    init {
        hologram.autoViewable = false
        hasCollision.valueChanged { npcTeam.collisionRule.value = getTeamCollision() }
        teamColor.valueChanged { event -> npcTeam.color.value = event.newValue }

        mirrorsSkin.valueChanged {
            if (viewers.isEmpty()) return@valueChanged
            refreshViewers()
        }

        eventPool.on<PlayerInteractWithEntityEvent> { event ->
            if (event.interactionHand == PlayerHand.OFF_HAND) return@on
            onClick.dispatch(event.player to ClickType.RIGHT)
        }

        eventPool.on<PlayerDamageEntityEvent> { event ->
            onClick.dispatch(event.player to ClickType.LEFT)
        }

        eventPool.on<PlayerPickItemFromEntityEvent> { event ->
            onClick.dispatch(event.player to ClickType.MIDDLE)
        }

        hologram.onLinesUpdated.subscribe {
            hologram.teleport(getHologramLocation())
        }

        skin.valueChanged { event ->
            if (event.newValue != null) {
                val texturesIndex: Int = this.gameProfile.properties.indexOfFirst { property -> property.name == "textures" }
                if (texturesIndex == -1) {
                    this.gameProfile.properties.add(event.newValue!!)
                } else {
                    this.gameProfile.properties[texturesIndex] = event.newValue!!
                }
            } else {
                this.gameProfile.properties.removeIf { property -> property.name == "textures" }
            }

            if (viewers.isEmpty()) return@valueChanged
            refreshViewers()
        }

        displayedSkinParts.listUpdated {
            metadata[EntityMetadataType.PLAYER_DISPLAY_SKIN_PARTS] = EntityMetadata(EntityMetadataType.PLAYER_DISPLAY_SKIN_PARTS, EntityMetaValue.BYTE, displayedSkinParts.values.getBitMask())
        }
        team.value = npcTeam
    }

    private fun refreshViewers() {
        val viewersCopy = viewers.toList()
        viewersCopy.forEach { viewer -> removeViewer(viewer) }
        viewersCopy.forEach { viewer -> addViewer(viewer) }
    }

    fun swingHand(hand: PlayerHand, players: List<Player> = viewers) {
        players.sendPacket(ClientboundPlayerAnimationPacket(this, if (hand == PlayerHand.MAIN_HAND) EntityAnimation.SWING_MAIN_ARM else EntityAnimation.SWING_OFFHAND))
    }

    fun swingMainHand(players: List<Player> = viewers) {
        swingHand(PlayerHand.MAIN_HAND, players)
    }

    fun swingOffHand(players: List<Player> = viewers) {
        swingHand(PlayerHand.OFF_HAND, players)
    }

    fun getPlayerInfoUpdates(player: Player): MutableList<PlayerInfoUpdate> {
        val updates = mutableListOf<PlayerInfoUpdate>()
        if (mirrorsSkin.value) {
            val textures = player.gameProfile.properties.firstOrNull { property -> property.name == "textures" }
            if (textures != null) {
                updates.add(PlayerInfoUpdate.AddPlayer(GameProfile(gameProfile.uuid, gameProfile.username, mutableListOf(textures))))
            }
        } else {
            updates.add(PlayerInfoUpdate.AddPlayer(gameProfile))
        }
        updates.add(PlayerInfoUpdate.UpdateListed(isListed.value))
        updates.add(PlayerInfoUpdate.UpdateDisplayName(customName.value))
        return updates
    }

    override fun teleport(location: Location) {
        super.teleport(location)
        hologram.teleport(getHologramLocation())
        hologram.viewDistanceBlocks = this.viewDistanceBlocks
    }

    override fun addViewer(player: Player): Boolean {

        player.sendPacket(ClientboundPlayerInfoUpdatePacket(mapOf(uuid to getPlayerInfoUpdates(player))))
        if (!super.addViewer(player)) return false

        this.displayedSkinParts.triggerUpdate()
        sendMetadataPacket(player)
        sendEquipmentPacket(player)
        hologram.addViewer(player)
        return true
    }

    override fun removeViewer(player: Player) {
        super.removeViewer(player)
        hologram.removeViewer(player)
        player.sendPacket(ClientboundPlayerInfoRemovePacket(this.gameProfile.uuid))
    }

    private fun getTeamCollision(): Team.CollisionRule {
        return if (hasCollision.value) Team.CollisionRule.ALWAYS else Team.CollisionRule.NEVER
    }

    fun setSkinFromUsername(username: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        MojangUtil.getSkinFromUsername(username).thenAccept { property ->
            if (property == null) {
                future.complete(false)
                return@thenAccept
            }
            skin.value = property
            future.complete(true)
        }
        return future
    }

    fun setSkinFromUUID(uuid: UUID): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        MojangUtil.getSkinFromUUID(uuid).thenAccept { property ->
            if (property == null) {
                future.complete(false)
                return@thenAccept
            }
            skin.value = property
        }
        return future
    }

    private fun getHologramLocation(): Location {
        return this.location.add(0f, EntityTypes.PLAYER.dimensions.height - 0.1f + hologram.lineAmount * Hologram.LINE_SIZE_MULTIPLIER.toFloat(), 0f)
    }

    private fun updateLookClose() {
        if (viewers.isEmpty()) return
        when (lookClose) {

            LookCloseType.NONE -> return
            LookCloseType.NORMAL -> {
                val closestPlayer = viewers
                    .filter { viewer -> viewer.location.distance(this.location) <= lookCloseDistance }
                    .minByOrNull { viewer -> viewer.location.distance(this.location) }

                if (closestPlayer == null) return
                this.lookAt(closestPlayer)
            }

            LookCloseType.CLIENT_SIDE -> {
                viewers
                    .filter { viewer -> viewer.location.distance(this.location) <= lookCloseDistance }
                    .forEach { viewer ->
                        this.lookAtClientside(viewer, viewer)
                    }
            }
        }
    }

    override fun dispose() {
        TeamManager.remove(npcTeam)
        tickTask.cancel()
        world.despawnEntity(hologram)
        hologram.dispose()
        super.dispose()
    }
}