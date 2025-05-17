package io.github.dockyardmc.dialog.form.submit

import io.github.dockyardmc.registry.DialogSubmitMethodTypes
import io.github.dockyardmc.registry.registries.DialogSubmitMethodType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

/**
 * Client assembles payload from template and sends it in [io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundCustomClickActionPacket]
 *
 * @param id for the packet
 * @param template string template. for example, `something $(arg1)`.
 *
 * will replace the keys matching input IDs and send it as payload
 */
class DialogCustomTemplate(
    val id: String,
    val template: String
) : DialogSubmitMethod() {
    override val type: DialogSubmitMethodType = DialogSubmitMethodTypes.CUSTOM_TEMPLATE

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("id", id)
            put("template", template)
        }
    }
}