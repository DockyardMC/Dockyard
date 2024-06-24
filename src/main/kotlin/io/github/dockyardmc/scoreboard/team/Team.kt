package io.github.dockyardmc.scoreboard.team

import io.github.dockyardmc.bindables.BindableMutableList
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.AddEntities
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTeamsPacket
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
    val displayName: Component = name.toComponent(),
    val flags: Int = 0x00,
    val nameTagVisibility: NameTagVisibility = NameTagVisibility.VISIBLE,
    val collisionRule: CollisionRule = CollisionRule.ALWAYS,
    val color: Int = 0x00,
    val prefix: Component = "".toComponent(),
    val suffix: Component = "".toComponent()
) {
    val entities = BindableMutableList<Entity>()

    init {
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
}