package io.github.dockyardmc.apis.bossbar

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.BossbarPacketAction
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundBossbarPacket
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.Viewable
import java.util.*

class Bossbar(
    val title: Bindable<String> = Bindable(""),
    val progress: Bindable<Float> = Bindable(0f),
    val color: Bindable<BossbarColor> = Bindable(BossbarColor.WHITE),
    val notches: Bindable<BossbarNotches> = Bindable(BossbarNotches.NO_NOTCHES),
) : Disposable, Viewable() {

    override var autoViewable: Boolean = false

    constructor(
        title: String = "",
        progress: Float = 0f,
        color: BossbarColor = BossbarColor.WHITE,
        notches: BossbarNotches = BossbarNotches.NO_NOTCHES,
    ) :
            this(
                Bindable(title),
                Bindable(progress),
                Bindable(color),
                Bindable(notches),
            )

    val eventPool = EventPool()
    val uuid: UUID = UUID.randomUUID()

    init {
        title.valueChanged { viewers.sendPacket(ClientboundBossbarPacket(BossbarPacketAction.UPDATE_TITLE, this)) }
        progress.valueChanged { viewers.sendPacket(ClientboundBossbarPacket(BossbarPacketAction.UPDATE_HEALTH, this)) }
        color.valueChanged { viewers.sendPacket(ClientboundBossbarPacket(BossbarPacketAction.UPDATE_STYLE, this)) }
        notches.valueChanged { viewers.sendPacket(ClientboundBossbarPacket(BossbarPacketAction.UPDATE_STYLE, this)) }
    }

    override fun dispose() {
        eventPool.dispose()
        viewers.toList().forEach(::removeViewer)
    }

    override fun addViewer(player: Player) {
        val createPacket = ClientboundBossbarPacket(BossbarPacketAction.ADD, this)
        player.sendPacket(createPacket)
        viewers.add(player)
    }

    override fun removeViewer(player: Player) {
        val removePacket = ClientboundBossbarPacket(BossbarPacketAction.REMOVE, this)
        player.sendPacket(removePacket)
        viewers.remove(player)
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