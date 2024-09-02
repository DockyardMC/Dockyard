package io.github.dockyardmc.commands

import io.github.dockyardmc.extentions.reversed
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeVarIntArray
import io.netty.buffer.ByteBuf
import kotlin.experimental.or

// Increase this counter if you tried to implement command nodes and failed: 2


abstract class CommandNode(
    val type: CommandNodeType,
    var isExecutable: Boolean = true,
    val children: MutableList<CommandNode> = mutableListOf(),
    val redirectNode: CommandNode? = null,
    val suggestionType: String? = null
)


fun getCommandList(): MutableMap<Int, CommandNode> {

    val commands = mutableListOf("test", "command", "uwu", "owo")
    val rootNode = RootCommandNode()

    val indexedNodes = mutableMapOf<Int, CommandNode>()

    indexedNodes[0] = rootNode
    rootNode.isExecutable = true

    var index = 0
    commands.forEach {
        index++
        val node = LiteralCommandNode(it)
        indexedNodes[index] = node
        rootNode.children.add(node)
    }

    return indexedNodes
}

fun ByteBuf.writeCommandNode(node: CommandNode, indices: MutableMap<Int, CommandNode>) {
    this.writeByte(getCommandNodeFlags(node).toInt())
    this.writeVarInt(node.children.size)
    node.children.forEach {
        val childIndex = getCommandNodeIndex(it, indices)
        this.writeVarInt(childIndex)
    }
    if(node.redirectNode != null) this.writeVarInt(getCommandNodeIndex(node.redirectNode, indices))
    if(node is LiteralCommandNode) this.writeUtf(node.name)
    if(node is ArgumentCommandNode) this.writeUtf(node.name)
    if(node is ArgumentCommandNode) this.writeVarInt(node.parser.ordinal)
    if(node.suggestionType != null) this.writeUtf(node.suggestionType)
}

fun ByteBuf.writeCommands(nodes: MutableMap<Int, CommandNode>) {
    this.writeVarInt(nodes.size)
    nodes.forEach {
        this.writeCommandNode(it.value, nodes)
    }
    this.writeVarInt(0)
}

fun getCommandNodeIndex(node: CommandNode, indices: MutableMap<Int, CommandNode>): Int =
    indices.reversed()[node] ?: throw Exception("Child of node not in indices")

fun getCommandNodeFlags(node: CommandNode): Byte {
    var mask: Byte = 0x00
    mask = mask or node.type.byte
    if(node.isExecutable) mask = mask or 0x04
    if(node.redirectNode != null) mask = mask or 0x08
    if(node.suggestionType != null) mask = mask or 0x10

    return mask
}




class ArgumentCommandNode(
    val name: String,
    val parser: ArgumentCommandNodeParser,
): CommandNode(type = CommandNodeType.ARGUMENT)

class LiteralCommandNode(
    val name: String
): CommandNode(type = CommandNodeType.LITERAL)
class RootCommandNode(): CommandNode(type = CommandNodeType.ROOT)

enum class ArgumentCommandNodeParser {
    BOOL,
    FLOAT,
    DOUBLE,
    INTEGER,
    LONG,
    STRING,
    ENTITY,
    GAME_PROFILE,
    BLOCK_POS,
    COLUMN_POS,
    VECTOR_3,
    VECTOR_2,
    BLOCK_STATE,
    BLOCK_PREDICATE,
    ITEM_STACK,
    ITEM_PREDICATE,
    COLOR,
    COMPONENT,
    STYLE,
    MESSAGE,
    NBT,
    NBT_TAG,
    NBT_PATH,
    OBJECTIVE,
    OBJECTIVE_CRITERIA,
    OPERATION,
    PARTICLE,
    ANGLE,
    ROTATION,
    SCOREBOARD_SLOT,
    SCORE_HOLDER,
    SWIZZLE,
    TEAM,
    ITEM_SLOT,
    RESOURCE_LOCATION,
    FUNCTION,
    ENTITY_ANCHOR,
    INT_RANGE,
    FLOAT_RANGE,
    DIMENSION,
    GAMEMODE,
    TIME,
    RESOURCE_OR_TAG,
    RESOURCE_OR_TAG_KEY,
    RESOURCE,
    RESOURCE_KEY,
    TEMPLATE_MIRROR,
    TEMPLATE_ROTATION,
    HEIGHTMAP,
    UUID,
    FORGE_MOD_ID,
    FORGE_ENUM
}

fun ByteBuf.writeBrigadierDouble(min: Double?, max: Double?) {

}

fun ByteBuf.writeBrigadierFloat(min: Float?, max: Float?) {

}

fun ByteBuf.writeBrigadierInteger(min: Int?, max: Int?) {

}

fun ByteBuf.writeBrigadierLong(min: Long?, max: Long?) {

}

fun ByteBuf.writeBrigadierString(type: BrigadierStringType) {

}

enum class BrigadierStringType {
    SINGLE_WORD,
    QUOTABLE_PHRASE,
    GREEDY_PHRASE
}

enum class CommandNodeType(val byte: Byte) {
    ROOT(0x00),
    LITERAL(0x01),
    ARGUMENT(0x02)
}