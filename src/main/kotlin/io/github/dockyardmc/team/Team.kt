package io.github.dockyardmc.team

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.BindableMutableList
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.AddEntitiesTeamPacketAction
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTeamsPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.UpdateTeamPacketAction
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf

enum class TeamNameTagVisibility(val value: String) {
    VISIBLE("always"),
    HIDE_OTHER_TEAMS("hideForOtherTeams"),
    HIDE_OWN_TEAM("hideForOwnTeam"),
    HIDDEN("never")
}

enum class TeamCollisionRule(val value: String) {
    ALWAYS("always"),
    PUSH_OTHER_TEAMS("pushOtherTeams"),
    PUSH_OWN_TEAM("pushOwnTeam"),
    NEVER("never")
}

class Team(
    val name: String,
    val displayName: Bindable<Component> = Bindable(name.toComponent()),
    val flags: Bindable<Int> = Bindable(0x00),
    val teamNameTagVisibility: Bindable<TeamNameTagVisibility> = Bindable(TeamNameTagVisibility.VISIBLE),
    val teamCollisionRule: Bindable<TeamCollisionRule> = Bindable(TeamCollisionRule.ALWAYS),
    val color: Bindable<LegacyTextColor> = Bindable(LegacyTextColor.WHITE),
    val prefix: Bindable<Component?> = Bindable(null),
    val suffix: Bindable<Component?> = Bindable(null)
) {

    constructor(
        name: String,
        displayName: String,
        flags: Int,
        teamNameTagVisibility: TeamNameTagVisibility,
        teamCollisionRule: TeamCollisionRule,
        color: LegacyTextColor,
        prefix: String? = null,
        suffix: String? = null
    ): this(
        name,
        Bindable<Component>(displayName.toComponent()),
        Bindable<Int>(flags),
        Bindable<TeamNameTagVisibility>(teamNameTagVisibility),
        Bindable<TeamCollisionRule>(teamCollisionRule),
        Bindable<LegacyTextColor>(color),
        Bindable<Component?>(prefix?.toComponent()),
        Bindable<Component?>(suffix?.toComponent())
    )

    val entities = BindableMutableList<Entity>()

    companion object {
        const val ALLOW_FRIENDLY_FIRE = 0x01
        const val SEE_INVISIBLE_TEAMMATES = 0x02
    }

    init {
        displayName.valueChanged { sendTeamUpdatePacket() }
        flags.valueChanged { sendTeamUpdatePacket() }
        teamNameTagVisibility.valueChanged { sendTeamUpdatePacket() }
        teamCollisionRule.valueChanged { sendTeamUpdatePacket() }
        color.valueChanged { sendTeamUpdatePacket() }
        prefix.valueChanged { sendTeamUpdatePacket() }
        suffix.valueChanged { sendTeamUpdatePacket() }

        entities.itemAdded { event ->
            require(event.item.team == this) { "Entity is still in another team!" }

            val packet = ClientboundTeamsPacket(AddEntitiesTeamPacketAction(this, listOf(event.item)))
            event.item.viewers.forEach { it.sendPacket(packet) }
        }
    }

    fun mapEntities(): List<String> {
        return entities.values.map {
            if (it is Player) {
                return@map it.username
            } else {
                return@map it.uuid.toString()
            }
        }
    }

    fun sendTeamUpdatePacket() {
        val packet = ClientboundTeamsPacket(UpdateTeamPacketAction(this))
        PlayerManager.players.sendPacket(packet)
    }
}

fun ByteBuf.writeTeamInfo(team: Team) {
    this.writeNBT(team.displayName.value.toNBT())
    this.writeByte(team.flags.value)
    this.writeUtf(team.teamNameTagVisibility.value.value)
    this.writeUtf(team.teamCollisionRule.value.value)
    this.writeVarInt(team.color.value.ordinal)
    this.writeNBT((team.prefix.value ?: "".toComponent()).toNBT())
    this.writeNBT((team.suffix.value ?: "".toComponent()).toNBT())
}
