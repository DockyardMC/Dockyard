package io.github.dockyardmc.dialog

import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.input.DialogInput
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.registries.DialogType
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.scroll.extensions.toComponent
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList
import org.jglrxavpok.hephaistos.nbt.NBTType

sealed class Dialog : NbtWritable {
    abstract val title: String

    /** the title of this dialog on other screens, like [DialogListDialog] or pause menu */
    abstract val externalTitle: String?
    abstract val canCloseWithEsc: Boolean
    abstract val body: List<DialogBody>
    abstract val type: DialogType
    abstract val inputs: Collection<DialogInput>

    /**
     * what happens after click or submit actions
     *
     * Default: [AfterAction.CLOSE]
     */
    abstract val afterAction: AfterAction

    override fun getNbt(): NBTCompound {
        return NBT.Compound { builder ->
            builder.put("type", type.getEntryIdentifier())
            builder.put("title", title.toComponent().toNBT())

            externalTitle?.let {
                builder.put("external_title", it.toComponent().toNBT())
            }

            builder.put("can_close_with_escape", canCloseWithEsc)
            builder.put("body", NBTList(NBTType.TAG_Compound, body.map { it.getNbt() }))
            // you can't play singleplayer on dockyard
            builder.put("pause", false)
            builder.put("after_action", afterAction.name.lowercase())
            builder.put("inputs", NBTList(NBTType.TAG_Compound, inputs.map { it.getNbt() }))
        }
    }

    enum class AfterAction {
        /** closes the dialog */
        CLOSE,

        /** actually nothing happens */
        NONE,

        /**
         * The server is expected to replace
         * current screen with another dialog btw */
        WAIT_FOR_RESPONSE;
    }
}