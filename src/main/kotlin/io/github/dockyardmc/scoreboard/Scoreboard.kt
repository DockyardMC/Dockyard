package io.github.dockyardmc.scoreboard

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateObjectivePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateScorePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.UpdateObjective
import io.github.dockyardmc.scroll.Component

enum class RenderType {
    INTEGER,
    HEARTS
}

enum class Display {
    LIST,
    SIDEBAR,
    BELOW_NAME,
    TEAM_BLACK,
    TEAM_DARK_BLUE,
    TEAM_DARK_GREEN,
    TEAM_DARK_AQUA,
    TEAM_DARK_RED,
    TEAM_DARK_PURPLE,
    TEAM_GOLD,
    TEAM_GRAY,
    TEAM_DARK_GRAY,
    TEAM_BLUE,
    TEAM_GREEN,
    TEAM_AQUA,
    TEAM_RED,
    TEAM_LIGHT_PURPLE,
    TEAM_YELLOW,
    TEAM_WHITE
}

class Scoreboard(
    val name: String,
    val displayName: Bindable<Component?> = Bindable(null),
    val renderType: Bindable<RenderType> = Bindable(RenderType.INTEGER),
    val numberFormat: Bindable<NumberFormat?> = Bindable(null)
) {
    init {
        displayName.valueChanged { updateViewers() }
        renderType.valueChanged { updateViewers() }
        numberFormat.valueChanged { updateViewers() }
    }

    fun updateScore(entry: Entry) {
        PlayerManager.players.forEach {
            val packet = ClientboundUpdateScorePacket(this, entry)
            it.sendPacket(packet)
        }
    }

    private fun updateViewers() {
        PlayerManager.players.forEach {
            val packet = ClientboundUpdateObjectivePacket(UpdateObjective(this))
            it.sendPacket(packet)
        }
    }
}

class Entry(
    val value: Int,
    val entity: String,
    val displayName: Component? = null,
    val numberFormat: NumberFormat? = null
) {
    constructor(
        value: Int,
        entity: Entity,
        displayName: Component? = null,
        numberFormat: NumberFormat? = null
    ) : this(value, entity.usernameOrUUID(), displayName, null)
}
