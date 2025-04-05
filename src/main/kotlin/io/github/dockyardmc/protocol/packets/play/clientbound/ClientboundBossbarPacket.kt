package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.apis.bossbar.Bossbar
import io.github.dockyardmc.apis.bossbar.BossbarColor
import io.github.dockyardmc.apis.bossbar.BossbarNotches
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.extensions.toComponent

class ClientboundBossbarPacket(action: BossbarPacketAction, bossbar: Bossbar) : ClientboundPacket() {

    init {
        buffer.writeUUID(bossbar.uuid)
        buffer.writeEnum<BossbarPacketAction>(action)

        when (action) {
            BossbarPacketAction.ADD -> {
                buffer.writeNBT(bossbar.title.value.toComponent().toNBT())
                buffer.writeFloat(bossbar.progress.value)
                buffer.writeEnum<BossbarColor>(bossbar.color.value)
                buffer.writeEnum<BossbarNotches>(bossbar.notches.value)
                buffer.writeByte(0x00)
                // flags or something idk who even uses it
            }

            BossbarPacketAction.UPDATE_HEALTH -> {
                buffer.writeFloat(bossbar.progress.value)
            }

            BossbarPacketAction.UPDATE_TITLE -> {
                buffer.writeNBT(bossbar.title.value.toComponent().toNBT())
            }

            BossbarPacketAction.UPDATE_STYLE -> {
                buffer.writeEnum<BossbarColor>(bossbar.color.value)
                buffer.writeEnum<BossbarNotches>(bossbar.notches.value)
            }

            else -> {}
        }

    }

}

enum class BossbarPacketAction {
    ADD,
    REMOVE,
    UPDATE_HEALTH,
    UPDATE_TITLE,
    UPDATE_STYLE,
    UPDATE_FLAGS
}