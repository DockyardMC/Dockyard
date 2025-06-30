package io.github.dockyardmc.commands

import io.github.dockyardmc.extentions.reversed
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.player.Player
import io.netty.buffer.ByteBuf
import kotlin.experimental.or

// Increase this counter if you tried to implement command nodes and failed: 2
// GET FUCKED I DID IT ON 3RD TRY

abstract class CommandNode(
    val type: CommandNodeType,
    var isOptional: Boolean = false,
    val children: MutableList<CommandNode> = mutableListOf(),
    val redirectNode: CommandNode? = null,
    var suggestionType: CommandArgumentSuggestionType? = null
)

fun buildCommandGraph(player: Player? = null): MutableMap<Int, CommandNode> {

    val commands = Commands.commands
    val rootNode = RootCommandNode()

    val indexedNodes = mutableMapOf<Int, CommandNode>()

    indexedNodes[0] = rootNode
    rootNode.isOptional = true

    var index = 0
    commands.toSortedMap().forEach {
        if (player != null && !player.hasPermission(it.value.permission)) return@forEach
        index++
        val nodeIndex = index
        val node = LiteralCommandNode(it.key)
        var nextChild: CommandNode = node
        it.value.subcommands.forEach subcommandForeach@{ subcommand ->
            if (player != null && !player.hasPermission(subcommand.value.permission)) return@subcommandForeach

            index++
            val subNode = LiteralCommandNode(subcommand.key)
            subNode.isOptional = false
            indexedNodes[index] = subNode
            node.children.add(subNode)

            var innerChildNext: CommandNode = subNode
            subcommand.value.arguments.forEach { arg ->
                index++
                val argument = ArgumentCommandNode(arg.key, arg.value.argument)
                argument.isOptional = arg.value.optional
                indexedNodes[index] = argument
                innerChildNext.children.add(argument)
                innerChildNext = argument
            }
        }
        it.value.arguments.forEach { arg ->
            index++
            val argument = ArgumentCommandNode(arg.key, arg.value.argument)
            argument.isOptional = arg.value.optional
            indexedNodes[index] = argument
            nextChild.children.add(argument)
            nextChild = argument
        }

        indexedNodes[nodeIndex] = node
        rootNode.children.add(node)
    }

    return indexedNodes.toSortedMap()
}

fun ByteBuf.writeCommandNode(node: CommandNode, indices: MutableMap<Int, CommandNode>) {
    if (node is ArgumentCommandNode) {
        when (node.argument) {
            is StringArgument -> node.suggestionType = CommandArgumentSuggestionType.ASK_SERVER
            is EntityArgument -> node.suggestionType = CommandArgumentSuggestionType.SUMMONABLE_MOBS
            is SoundArgument -> node.suggestionType = CommandArgumentSuggestionType.AVAILABLE_SOUNDS
            is WorldArgument -> node.suggestionType = CommandArgumentSuggestionType.ASK_SERVER
            is EnumArgument -> node.suggestionType = CommandArgumentSuggestionType.ASK_SERVER
            is PlayerArgument -> node.suggestionType = CommandArgumentSuggestionType.ASK_SERVER
            else -> {}
        }
    }

    val flags = getCommandNodeFlags(node)
    this.writeByte(flags.toInt())
    this.writeVarInt(node.children.size)
    node.children.forEach {
        val childIndex = getCommandNodeIndex(it, indices)
        this.writeVarInt(childIndex)
    }
    if (node.redirectNode != null) this.writeVarInt(getCommandNodeIndex(node.redirectNode, indices))
    if (node is LiteralCommandNode) this.writeString(node.name)
    if (node is ArgumentCommandNode) {
        val parser = node.argument.parser
        this.writeString(node.name)
        this.writeVarInt(parser.ordinal)
        node.argument.write(this)

        if (node.suggestionType != null) this.writeString(node.suggestionType!!.getIdentifier())
    }
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
    if (node.isOptional) mask = mask or 0x04
    if (node.redirectNode != null) mask = mask or 0x08
    if (node.suggestionType != null) mask = mask or 0x10

    return mask
}

class ArgumentCommandNode(
    val name: String,
    val argument: CommandArgument,
) : CommandNode(type = CommandNodeType.ARGUMENT)

class LiteralCommandNode(
    val name: String
) : CommandNode(type = CommandNodeType.LITERAL)

class RootCommandNode() : CommandNode(type = CommandNodeType.ROOT)

enum class CommandArgumentSuggestionType {
    ASK_SERVER,
    ALL_RECIPES,
    AVAILABLE_SOUNDS,
    SUMMONABLE_MOBS;

    fun getIdentifier(): String = "minecraft:${this.name.lowercase()}"
}

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
    BLOCK,
    ITEM_STACK,
    ITEM,
    LEGACY_TEXT_COLOR,
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
    WORLD,
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
    FORGE_ENUM;

    fun getIdentifier(): String {
        return "minecraft:${this.name.lowercase()}"
    }
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