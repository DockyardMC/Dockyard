package io.github.dockyardmc.dialog.form.submit

import io.github.dockyardmc.registry.DialogSubmitMethodTypes
import io.github.dockyardmc.registry.registries.DialogSubmitMethodType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

/**
 * @param template Command template. for example, `say $(arg1)`.
 *
 * will replace the keys matching input IDs and execute the command
 */
class DialogCommandTemplate(
    val template: String,
) : DialogSubmitMethod() {
    override val type: DialogSubmitMethodType = DialogSubmitMethodTypes.COMMAND_TEMPLATE

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("template", template)
        }
    }
}