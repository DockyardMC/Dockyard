package io.github.dockyardmc.npc

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.metadata.EntityMetaValue
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.EntityMetadataType
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.DisplayedSkinPart
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerInfoUpdate
import io.github.dockyardmc.player.getBitMask
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoUpdatePacket
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

class FakePlayer(location: Location) : Entity(location) {
    override var type: EntityType = EntityTypes.PLAYER
    override val health: Bindable<Float> = bindablePool.provideBindable(20f)
    override var inventorySize: Int = 35

    private val eventPool = EventPool()
    val gameProfile = GameProfile(uuid, uuid.toString().substring(0, 16))
    val isListed: Bindable<Boolean> = bindablePool.provideBindable(false)
    val skin: Bindable<GameProfile.Property?> = bindablePool.provideBindable(null)
    val displayedSkinParts: BindableList<DisplayedSkinPart> = bindablePool.provideBindableList(DisplayedSkinPart.CAPE, DisplayedSkinPart.JACKET, DisplayedSkinPart.LEFT_PANTS, DisplayedSkinPart.RIGHT_PANTS, DisplayedSkinPart.LEFT_SLEEVE, DisplayedSkinPart.RIGHT_SLEEVE, DisplayedSkinPart.HAT)
    val hasCollision: Bindable<Boolean> = bindablePool.provideBindable(true)
    val teamColor: Bindable<LegacyTextColor> = bindablePool.provideBindable(LegacyTextColor.WHITE)
    var lookClose: LookCloseType = LookCloseType.NONE
    var lookCloseDistance: Int = 5

    val npcTeam = TeamManager.create("npc-$uuid", teamColor.value, Team.NameTagVisibility.HIDDEN, getTeamCollision())

    val tickTask: SchedulerTask = world.scheduler.runRepeating(1.ticks) {
        when (lookClose) {
            LookCloseType.NONE -> return@runRepeating
            LookCloseType.NORMAL -> {
                val closestPlayer = viewers
                    .filter { viewer -> viewer.location.distance(this.location) <= lookCloseDistance }
                    .minByOrNull { viewer -> viewer.location.distance(this.location) }

                if (closestPlayer == null) return@runRepeating
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

    init {
        hasCollision.valueChanged { npcTeam.collisionRule.value = getTeamCollision() }
        teamColor.valueChanged { event -> npcTeam.color.value = event.newValue }

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
            val viewersCopy = viewers.toList()
            viewersCopy.forEach { viewer -> removeViewer(viewer) }
            viewersCopy.forEach { viewer -> addViewer(viewer) }
        }

        displayedSkinParts.listUpdated {
            metadata[EntityMetadataType.PLAYER_DISPLAY_SKIN_PARTS] = EntityMetadata(EntityMetadataType.PLAYER_DISPLAY_SKIN_PARTS, EntityMetaValue.BYTE, displayedSkinParts.values.getBitMask())
        }
        team.value = npcTeam
    }

    override fun addViewer(player: Player): Boolean {

        val updates = mutableListOf(
            PlayerInfoUpdate.AddPlayer(gameProfile),
            PlayerInfoUpdate.UpdateListed(isListed.value),
            PlayerInfoUpdate.UpdateDisplayName(customName.value),
        )

        player.sendPacket(ClientboundPlayerInfoUpdatePacket(mapOf(uuid to updates)))
        if (!super.addViewer(player)) return false

        this.displayedSkinParts.triggerUpdate()
        sendMetadataPacket(player)
        sendEquipmentPacket(player)
        return true
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


}