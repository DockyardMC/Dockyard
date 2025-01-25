package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.bossbar.Bossbar
import io.github.dockyardmc.bossbar.BossbarColor
import io.github.dockyardmc.bossbar.BossbarNotches
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.extentions.writeVarIntEnum
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scroll.extensions.toComponent

@WikiVGEntry("Boss Bar")
@ClientboundPacketInfo(0x0A, ProtocolState.PLAY)
class ClientboundBossbarPacket(action: BossbarPacketAction, bossbar: Bossbar): ClientboundPacket() {

    init {
        data.writeUUID(bossbar.uuid)
        data.writeVarIntEnum<BossbarPacketAction>(action)

        when(action) {
            BossbarPacketAction.ADD -> {
                data.writeNBT(bossbar.title.value.toComponent().toNBT())
                data.writeFloat(bossbar.progress.value)
                data.writeVarIntEnum<BossbarColor>(bossbar.color.value)
                data.writeVarIntEnum<BossbarNotches>(bossbar.notches.value)
                data.writeByte(0x00)
                // flags or something idk who even uses it
            }
            BossbarPacketAction.UPDATE_HEALTH -> {
                data.writeFloat(bossbar.progress.value)
            }
            BossbarPacketAction.UPDATE_TITLE -> {
                data.writeNBT(bossbar.title.value.toComponent().toNBT())
            }
            BossbarPacketAction.UPDATE_STYLE -> {
                data.writeVarIntEnum<BossbarColor>(bossbar.color.value)
                data.writeVarIntEnum<BossbarNotches>(bossbar.notches.value)
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