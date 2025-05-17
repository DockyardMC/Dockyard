package io.github.dockyardmc.dialog.form.submit

import io.github.dockyardmc.registry.DialogSubmitMethodTypes
import io.github.dockyardmc.registry.registries.DialogSubmitMethodType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class DialogCustomForm(
    val id: String,
) : DialogSubmitMethod() {
    override val type: DialogSubmitMethodType = DialogSubmitMethodTypes.CUSTOM_FORM

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("id", id)
        }
    }
}