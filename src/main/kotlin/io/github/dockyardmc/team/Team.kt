package io.github.dockyardmc.team

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindablePool
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.npc.FakePlayer
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.packets.play.clientbound.AddEntitiesTeamPacketAction
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTeamsPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.UpdateTeamPacketAction
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.utils.Disposable
import io.netty.buffer.ByteBuf
import kotlin.experimental.or

class Team(val name: String) : NetworkWritable, Disposable {

    val bindablePool = BindablePool()

    val displayName: Bindable<String> = bindablePool.provideBindable(name)
    val nameTagVisibility: Bindable<NameTagVisibility> = bindablePool.provideBindable(NameTagVisibility.VISIBLE)
    val collisionRule: Bindable<CollisionRule> = bindablePool.provideBindable(CollisionRule.ALWAYS)
    val color: Bindable<LegacyTextColor> = bindablePool.provideBindable(LegacyTextColor.WHITE)
    val prefix: Bindable<String?> = bindablePool.provideBindable(null)
    val suffix: Bindable<String?> = bindablePool.provideBindable(null)
    val entities = bindablePool.provideBindableList<Entity>()
    var allowFriendlyFire: Boolean = true
    var seeFriendlyInvisibles: Boolean = true

    enum class NameTagVisibility {
        VISIBLE,
        HIDDEN,
        HIDE_OTHER_TEAMS,
        HIDE_OWN_TEAM,
    }

    enum class CollisionRule {
        ALWAYS,
        NEVER,
        PUSH_OTHER_TEAMS,
        PUSH_OWN_TEAM,
    }

    init {
        displayName.valueChanged { sendTeamUpdatePacket() }
        nameTagVisibility.valueChanged { sendTeamUpdatePacket() }
        collisionRule.valueChanged { sendTeamUpdatePacket() }
        color.valueChanged { sendTeamUpdatePacket() }
        prefix.valueChanged { sendTeamUpdatePacket() }
        suffix.valueChanged { sendTeamUpdatePacket() }

        entities.itemAdded { event ->
            if (event.item.team.value != null && event.item.team.value != this) throw IllegalStateException("Entity is on another team! (${event.item.team.value?.name})")

            val packet = ClientboundTeamsPacket(AddEntitiesTeamPacketAction(this, listOf(event.item)))
            PlayerManager.players.sendPacket(packet)
        }
    }

    fun mapEntities(): List<String> {
        return entities.values.map { entity ->
            val value = when (entity) {
                is FakePlayer -> entity.gameProfile.username
                is Player -> entity.username
                else -> entity.uuid.toString()
            }
            return listOf(value)
        }
    }

    private fun sendTeamUpdatePacket() {
        val packet = ClientboundTeamsPacket(UpdateTeamPacketAction(this))
        PlayerManager.players.sendPacket(packet)
    }

    private fun getFlags(): Byte {
        var mask: Byte = 0x00
        if (allowFriendlyFire) mask = (mask or 0x01)
        if (seeFriendlyInvisibles) mask = (mask or 0x02)
        return mask
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeTextComponent(this.displayName.value)
        buffer.writeByte(this.getFlags().toInt())
        buffer.writeEnum(this.nameTagVisibility.value)
        buffer.writeEnum(this.collisionRule.value)
        buffer.writeVarInt(this.color.value.ordinal)
        buffer.writeTextComponent((this.prefix.value ?: ""))
        buffer.writeTextComponent((this.suffix.value ?: ""))
    }

    override fun dispose() {
        bindablePool.dispose()
    }
}
