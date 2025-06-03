package io.github.dockyardmc.dialog

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.body.DialogItemBody
import io.github.dockyardmc.dialog.body.PlainMessage
import io.github.dockyardmc.dialog.input.BooleanDialogInput
import io.github.dockyardmc.dialog.input.DialogInput
import io.github.dockyardmc.dialog.input.NumberRangeDialogInput
import io.github.dockyardmc.dialog.input.SingleOptionDialogInput
import io.github.dockyardmc.dialog.input.TextDialogInput
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundClearDialogPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundShowDialogPacket
import io.github.dockyardmc.registry.registries.DialogEntry
import io.github.dockyardmc.registry.registries.DialogType
import io.github.dockyardmc.registry.registries.Item
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

    @DialogDsl
    abstract class Builder {
        var title: String = ""
        var externalTitle: String? = null
        var canCloseWithEsc: Boolean = true
        val body = mutableListOf<DialogBody>()
        val inputs = mutableListOf<DialogInput>()
        var afterAction: AfterAction = AfterAction.CLOSE

        fun addItemBody(item: ItemStack, block: (DialogItemBody.Builder.() -> Unit)? = null) {
            val builder = DialogItemBody.Builder(item)
            block?.let { builder.apply(it) }
            body.add(builder.build())
        }

        fun addItemBody(item: Item, block: (DialogItemBody.Builder.() -> Unit)? = null) = addItemBody(item.toItemStack(), block)

        fun addPlainMessage(content: String, block: (PlainMessage.Builder.() -> Unit)? = null) {
            val builder = PlainMessage.Builder(content)
            block?.let { builder.apply(it) }
            body.add(builder.build())
        }

        fun addTextInput(key: String, block: (TextDialogInput.Builder.() -> Unit)? = null) {
            val builder = TextDialogInput.Builder(key)
            block?.let { builder.apply(it) }
            inputs.add(builder.build())
        }

        fun addBooleanInput(key: String, block: (BooleanDialogInput.Builder.() -> Unit)? = null) {
            val builder = BooleanDialogInput.Builder(key)
            block?.let { builder.apply(it) }
            inputs.add(builder.build())
        }

        fun addNumberRangeInput(key: String, range: ClosedFloatingPointRange<Double>, block: (NumberRangeDialogInput.Builder.() -> Unit)? = null) {
            val builder = NumberRangeDialogInput.Builder(key, range)
            block?.let { builder.apply(it) }
            inputs.add(builder.build())
        }

        fun addSingleOptionInput(key: String, block: SingleOptionDialogInput.Builder.() -> Unit) {
            inputs.add(SingleOptionDialogInput.Builder(key).apply(block).build())
        }

        abstract fun build(): Dialog
    }
}

fun Player.showDialog(dialog: DialogEntry) {
    sendPacket(ClientboundShowDialogPacket(dialog))
}

fun Player.clearDialog() {
    sendPacket(ClientboundClearDialogPacket())
}