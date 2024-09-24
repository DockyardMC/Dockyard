package io.github.dockyardmc.bossbar

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.packets.play.clientbound.BossbarPacketAction
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundBossbarPacket
import io.github.dockyardmc.utils.Disposable
import java.util.*

class Bossbar(
    val title: Bindable<String> = Bindable(""),
    val progress: Bindable<Float> = Bindable(0f),
    val color: Bindable<BossbarColor> = Bindable(BossbarColor.WHITE),
    val notches: Bindable<BossbarNotches> = Bindable(BossbarNotches.NO_NOTCHES),
    val viewers: BindableList<PersistentPlayer> = BindableList(),
): Disposable {

    constructor(
        title: String = "",
        progress: Float = 0f,
        color: BossbarColor = BossbarColor.WHITE,
        notches: BossbarNotches = BossbarNotches.NO_NOTCHES,
        viewers: MutableList<Player> = mutableListOf(),
    ) :
            this(
                Bindable(title),
                Bindable(progress),
                Bindable(color),
                Bindable(notches),
                BindableList(viewers.toPersistent())
            )

    val eventPool = EventPool()
    val uuid: UUID = UUID.randomUUID()

    init {
        title.valueChanged { viewers.sendPacket(ClientboundBossbarPacket(BossbarPacketAction.UPDATE_TITLE, this)) }
        progress.valueChanged { viewers.sendPacket(ClientboundBossbarPacket(BossbarPacketAction.UPDATE_HEALTH, this)) }
        color.valueChanged { viewers.sendPacket(ClientboundBossbarPacket(BossbarPacketAction.UPDATE_STYLE, this)) }
        notches.valueChanged { viewers.sendPacket(ClientboundBossbarPacket(BossbarPacketAction.UPDATE_STYLE, this)) }

        // player added to viewers
        viewers.itemAdded {
            val createPacket = ClientboundBossbarPacket(BossbarPacketAction.ADD, this)
            it.item.sendPacket(createPacket)
        }

        // player removed from viewers
        viewers.itemRemoved {
            val removePacket = ClientboundBossbarPacket(BossbarPacketAction.REMOVE, this)
            it.item.sendPacket(removePacket)
        }

        // if player joins and is part of viewers, the bossbar should show for them
        eventPool.on<PlayerJoinEvent> {
            if (viewers.contains(it.player)) {
                val createPacket = ClientboundBossbarPacket(BossbarPacketAction.ADD, this)
                it.player.sendPacket(createPacket)
            }
        }
    }

    override fun dispose() {
        eventPool.dispose()
        viewers.values.forEach(viewers::remove)
    }
}

enum class BossbarColor {
    PINK,
    BLUE,
    RED,
    GREEN,
    YELLOW,
    PURPLE,
    WHITE
}

enum class BossbarNotches {
    NO_NOTCHES,
    SIX,
    TEN,
    TWELVE,
    TWENTY
}