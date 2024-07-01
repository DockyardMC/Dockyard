package io.github.dockyardmc.team

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.BindableMutableList
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.AddEntities
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTeamsPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.UpdateTeam
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent

object Flags {
    const val ALLOW_FRIENDLY_FIRE = 0x01
    const val SEE_INVISIBLE_TEAMMATES = 0x02
}

enum class NameTagVisibility(val value: String) {
    VISIBLE("always"),
    HIDE_OTHER_TEAMS("hideForOtherTeams"),
    HIDE_OWN_TEAM("hideForOwnTeam"),
    HIDDEN("never")
}

enum class CollisionRule(val value: String) {
    ALWAYS("always"),
    PUSH_OTHER_TEAMS("pushOtherTeams"),
    PUSH_OWN_TEAM("pushOwnTeam"),
    NEVER("never")
}

class Team(
    val name: String,
    val displayName: Bindable<Component> = Bindable(name.toComponent()),
    val flags: Bindable<Int> = Bindable(0x00),
    val nameTagVisibility: Bindable<NameTagVisibility> = Bindable(NameTagVisibility.VISIBLE),
    val collisionRule: Bindable<CollisionRule> = Bindable(CollisionRule.ALWAYS),
    val color: Bindable<Int> = Bindable(15),
    val prefix: Bindable<Component?> = Bindable(null),
    val suffix: Bindable<Component?> = Bindable(null)
) {
    val entities = BindableMutableList<Entity>()

    init {
        displayName.valueChanged { updateViewers() }
        flags.valueChanged { updateViewers() }
        nameTagVisibility.valueChanged { updateViewers() }
        collisionRule.valueChanged { updateViewers() }
        color.valueChanged { updateViewers() }
        prefix.valueChanged { updateViewers() }
        suffix.valueChanged { updateViewers() }

        entities.itemAdded { event ->
            if (event.item.team != this) {
                throw IllegalArgumentException("Entity is still in another team!")
            }

            val packet = ClientboundTeamsPacket(AddEntities(this, listOf(event.item)))
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

    fun updateViewers() {
        val packet = ClientboundTeamsPacket(UpdateTeam(this))
        PlayerManager.players.sendPacket(packet)
    }
}