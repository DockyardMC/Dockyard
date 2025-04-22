package io.github.dockyardmc.apis.advancement

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.advancement.Advancement
import io.github.dockyardmc.advancement.AdvancementFrame
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateAdvancementsPacket
import kotlinx.datetime.Clock
import kotlin.random.Random

/**
 * Creates a completed advancement and deletes immediately,
 * resulting in an advancement toast
 */
fun Player.showToast(
    title: String,
    icon: ItemStack,
    frame: AdvancementFrame = AdvancementFrame.TASK,
) = DockyardServer.scheduler.run {
    val advId = "internal_dockyard:toast/${title.hashCode() + icon.hashCode() + frame.hashCode() + Random.nextInt()}"

    sendPacket(
        ClientboundUpdateAdvancementsPacket(
            false,
            mapOf(
                advId to Advancement(
                    advId,
                    null,
                    title, "", icon, frame,
                    showToast = true,
                    isHidden = false,
                    background = null,
                    x = 0f, y = 0f,
                    requirements = listOf(listOf("0"))
                )
            ),
            listOf(),
            mapOf(advId to mapOf("0" to Clock.System.now().epochSeconds))
        )
    )

    sendPacket(
        ClientboundUpdateAdvancementsPacket(
            false,
            mapOf(),
            listOf(advId),
            mapOf()
        )
    )
}