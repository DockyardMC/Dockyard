package io.github.dockyardmc.team

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.AddEntitiesTeamPacketAction
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTeamsPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.UpdateTeamPacketAction
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf
import java.lang.IllegalArgumentException
import kotlin.experimental.or

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
    val teamNameTagVisibility: Bindable<TeamNameTagVisibility> = Bindable(TeamNameTagVisibility.VISIBLE),
    val teamCollisionRule: Bindable<TeamCollisionRule> = Bindable(TeamCollisionRule.ALWAYS),
    val color: Bindable<LegacyTextColor> = Bindable(LegacyTextColor.WHITE),
    val prefix: Bindable<Component?> = Bindable(null),
    val suffix: Bindable<Component?> = Bindable(null)
) {

    constructor(
        name: String,
        color: LegacyTextColor,
        teamNameTagVisibility: TeamNameTagVisibility = TeamNameTagVisibility.VISIBLE,
        teamCollisionRule: TeamCollisionRule = TeamCollisionRule.ALWAYS,
        displayName: String = name,
        prefix: String? = null,
        suffix: String? = null
    ): this(
        name,
        Bindable<Component>(displayName.toComponent()),
        Bindable<TeamNameTagVisibility>(teamNameTagVisibility),
        Bindable<TeamCollisionRule>(teamCollisionRule),
        Bindable<LegacyTextColor>(color),
        Bindable<Component?>(prefix?.toComponent()),
        Bindable<Component?>(suffix?.toComponent())
    )

    val entities = BindableList<Entity>()

    init {
        displayName.valueChanged { sendTeamUpdatePacket() }
        teamNameTagVisibility.valueChanged { sendTeamUpdatePacket() }
        teamCollisionRule.valueChanged { sendTeamUpdatePacket() }
        color.valueChanged { sendTeamUpdatePacket() }
        prefix.valueChanged { sendTeamUpdatePacket() }
        suffix.valueChanged { sendTeamUpdatePacket() }

        entities.itemAdded { event ->
            if(event.item.team != null && event.item.team != this) throw IllegalArgumentException("Entity is on another team! (${event.item.team?.name})")

            val packet = ClientboundTeamsPacket(AddEntitiesTeamPacketAction(this, listOf(event.item)))
            PlayerManager.players.sendPacket(packet)
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

    val allowFriendlyFire: Boolean = true
    val seeFriendlyInvisibles: Boolean = true

    fun getFlags(): Byte {
        var mask: Byte = 0x00
        if(allowFriendlyFire) mask = (mask or 0x01)
        if(seeFriendlyInvisibles) mask = (mask or 0x02)
        return mask
    }
}


fun ByteBuf.writeTeamInfo(team: Team) {
    this.writeNBT(team.displayName.value.toNBT())
    this.writeByte(team.getFlags().toInt())
    this.writeUtf(team.teamNameTagVisibility.value.value)
    this.writeUtf(team.teamCollisionRule.value.value)
    this.writeVarInt(team.color.value.ordinal)
    this.writeNBT((team.prefix.value ?: "".toComponent()).toNBT())
    this.writeNBT((team.suffix.value ?: "".toComponent()).toNBT())
}
