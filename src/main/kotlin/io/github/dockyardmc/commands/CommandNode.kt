package io.github.dockyardmc.commands

import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarIntArray
import io.netty.buffer.ByteBuf

data class CommandNode(
    var flags: Int,
    var children: MutableList<CommandNode>,
    var redirectNode: Int? = null,
    var name: String? = null,
    var parserId: Int? = null,
    var suggestionType: String? = null,
    var type: CommandNodeType,
)

val testCommand = CommandNode(0x00, mutableListOf(
    CommandNode(0x05, mutableListOf(), null, "test", type = CommandNodeType.ROOT)
), type = CommandNodeType.ROOT
)

fun ByteBuf.writeCommandNode(node: CommandNode) {
    this.writeByte(node.flags)
    if(node.type == CommandNodeType.ROOT) {
        this.writeVarIntArray(mutableListOf(0))
    } else {
        this.writeVarIntArray(mutableListOf())
    }
    node.redirectNode?.let { this.writeInt(it) }
    node.name?.let { this.writeUtf(it) }
}

enum class CommandNodeType {
    ROOT,
    LITERAL,
    ARGUMENT
}