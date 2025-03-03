package io.github.dockyardmc.team

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableList
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.npc.PlayerNpc
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.AddEntitiesTeamPacketAction
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTeamsPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.UpdateTeamPacketAction
import io.github.dockyardmc.scroll.LegacyTextColor
import io.netty.buffer.ByteBuf
import kotlin.experimental.or

enum class TeamNameTagVisibility(val vanilla: String) {
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
    val displayName: Bindable<String> = Bindable(name),
    val teamNameTagVisibility: Bindable<TeamNameTagVisibility> = Bindable(TeamNameTagVisibility.VISIBLE),
    val teamCollisionRule: Bindable<TeamCollisionRule> = Bindable(TeamCollisionRule.ALWAYS),
    val color: Bindable<LegacyTextColor> = Bindable(LegacyTextColor.WHITE),
    val prefix: Bindable<String?> = Bindable(null),
    val suffix: Bindable<String?> = Bindable(null)
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
        Bindable<String>(displayName),
        Bindable<TeamNameTagVisibility>(teamNameTagVisibility),
        Bindable<TeamCollisionRule>(teamCollisionRule),
        Bindable<LegacyTextColor>(color),
        Bindable<String?>(prefix),
        Bindable<String?>(suffix)
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
            if(event.item.team.value != null && event.item.team.value != this) throw IllegalArgumentException("Entity is on another team! (${event.item.team.value?.name})")

            val packet = ClientboundTeamsPacket(AddEntitiesTeamPacketAction(this, listOf(event.item)))
            PlayerManager.players.sendPacket(packet)
        }
    }

    fun mapEntities(): List<String> {
        return entities.values.map {
            val value = when(it) {
                is PlayerNpc -> it.username.value
                is Player -> it.username
                else -> it.uuid.toString()
            }
            return listOf(value)
        }
    }

    fun sendTeamUpdatePacket() {
        val packet = ClientboundTeamsPacket(UpdateTeamPacketAction(this))
        PlayerManager.players.sendPacket(packet)
    }

    var allowFriendlyFire: Boolean = true
    var seeFriendlyInvisibles: Boolean = true

    fun getFlags(): Byte {
        var mask: Byte = 0x00
        if(allowFriendlyFire) mask = (mask or 0x01)
        if(seeFriendlyInvisibles) mask = (mask or 0x02)
        return mask
    }
}

fun ByteBuf.writeTeamInfo(team: Team) {
    this.writeTextComponent(team.displayName.value)
    this.writeByte(team.getFlags().toInt())
    this.writeString(team.teamNameTagVisibility.value.vanilla)
    this.writeString(team.teamCollisionRule.value.value)
    this.writeVarInt(team.color.value.ordinal)
    this.writeTextComponent((team.prefix.value ?: ""))
    this.writeTextComponent((team.suffix.value ?: ""))
}
