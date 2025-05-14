package io.github.dockyardmc.dialog.form

import io.github.dockyardmc.dialog.Dialog
import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.form.submit.DialogFormSubmit
import io.github.dockyardmc.dialog.form.submit.DialogSubmitMethod
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogType
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList
import org.jglrxavpok.hephaistos.nbt.NBTType

class MultiActionInputFormDialog(
    title: String,
    externalTitle: String?,
    canCloseWithEsc: Boolean,
    body: List<DialogBody>,
    val inputs: Collection<DialogFormInput>,
    val actions: Collection<DialogFormSubmit>
) : Dialog(title, externalTitle, canCloseWithEsc, body) {
    override val type: DialogType = DialogTypes.MULTI_ACTION_INPUT_FORM

    override fun getNbt(): NBT {
        return (super.getNbt() as NBTCompound).kmodify {
            put("inputs", NBTList(NBTType.TAG_Compound, inputs.map(NbtWritable::getNbt)))
            put("actions", NBTList(NBTType.TAG_Compound, actions.map(NbtWritable::getNbt)))
        }
    }
}