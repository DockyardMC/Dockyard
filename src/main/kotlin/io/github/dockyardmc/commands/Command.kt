package io.github.dockyardmc.commands

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.utils.Console

@Suppress("UNCHECKED_CAST")
class Command: Cloneable {
    lateinit var internalExecutorDoNotUse: (CommandExecutor) -> Unit
    var arguments: MutableMap<String, CommandArgumentData> = mutableMapOf()
    var permission: String = ""
    var description: String = ""
    var isAlias: Boolean = false
    var name: String = ""
    var aliases: List<String> = listOf<String>()
    val subcommands: MutableMap<String, Command> = mutableMapOf()

    fun withPermission(permission: String) {
        this.permission = permission
    }

    fun withDescription(description: String) {
        this.description = description
    }

    fun withAliases(aliases: List<String>) {
        this.aliases = aliases
    }

    fun withAliases(vararg aliases: String) {
        this.aliases = aliases.toList()
    }


    inline fun <reified T> getArgument(argumentName: String): T {
        if(T::class.java.isEnum && T::class != LegacyTextColor::class) throw IllegalStateException("Supplied generic is of type enum, please use getEnumArgument method instead.")
        if(arguments[argumentName] == null) throw IllegalStateException("Argument with name $argumentName does not exist")
        if(arguments[argumentName]!!.returnedValue == null) throw IllegalStateException("Argument value of $argumentName is null. Use getOrNull to get nullable value")

        return arguments[argumentName]!!.returnedValue as T
    }

    inline fun <reified T : Enum<T>> getEnumArgument(argumentName: String): T {
        val value = getArgument<String>(argumentName)
        return T::class.java.enumConstants.firstOrNull { it.name == value.uppercase() } ?: throw Exception("Enum ${T::class.simpleName} does not contain \"${value.uppercase()}\"")
    }

    inline fun <reified T : Enum<T>> getEnumArgumentOrNull(argumentName: String): T? {
        if(arguments[argumentName] == null) return null
        val value = getArgumentOrNull<String>(argumentName) ?: return null
        return T::class.java.enumConstants.firstOrNull { it.name == value.uppercase() } ?: throw Exception("Enum ${T::class.simpleName} does not contain \"${value.uppercase()}\"")
    }

    inline fun <reified T> getArgumentOrNull(argumentName: String): T? {
        if(T::class.java.isEnum && T::class != LegacyTextColor::class) throw IllegalStateException("Supplied generic is of type enum, please use getEnumArgumentOrNull method instead.")
        if(arguments[argumentName] == null) return null
        if(arguments[argumentName]!!.returnedValue == null) return null
        return arguments[argumentName]!!.returnedValue as T
    }

    fun addArgument(name: String, argument: CommandArgument, suggestions: ((Player) -> Collection<String>)? = null) {
        if(subcommands.isNotEmpty()) throw IllegalStateException("Command cannot have both arguments and subcommands!")
        val data = CommandArgumentData(argument, false, expectedReturnValueType = argument.expectedType, suggestions = suggestions)
        arguments[name] = data
        val before = arguments.values.indexOf(data) - 1
        if(before <= 0 ) return
        if(arguments.values.toList()[before].optional) throw IllegalStateException("Cannot put argument after optional argument!")

    }

    fun addOptionalArgument(name: String, argument: CommandArgument, suggestions: ((Player) -> Collection<String>)? = null) {
        if(subcommands.isNotEmpty()) throw IllegalStateException("Command cannot have both arguments and subcommands at the same time!")
        arguments[name] = CommandArgumentData(argument, true, expectedReturnValueType = argument.expectedType, suggestions = suggestions)
    }

    fun execute(function: (ctx: CommandExecutor) -> Unit) {
        if(subcommands.isNotEmpty()) throw IllegalStateException("Command cannot have executor and subcommands at the same time!")
        internalExecutorDoNotUse = function
    }

    fun build(): Command = this

    fun addSubcommand(name: String, builder: Command.() -> Unit) {
        if(arguments.isNotEmpty()) throw IllegalStateException("Command cannot have both arguments and subcommands at the same time!")
        val sanitizedName = name.lowercase().removePrefix("/")
        val subcommand = Command()
        builder.invoke(subcommand)
        subcommands[sanitizedName] = subcommand
        subcommand.name = sanitizedName
    }

    public override fun clone(): Command {
        val cloned = super.clone() as Command
        cloned.arguments = arguments.toMutableMap()
        cloned.aliases = aliases.toMutableList()
        cloned.description = description
        cloned.internalExecutorDoNotUse = internalExecutorDoNotUse
        cloned.permission = permission
        cloned.isAlias = isAlias
        cloned.name = name
        return cloned
    }
}

data class CommandExecutor(
    val player: Player? = null,
    val console: Console,
    var command: String = "",
    val isPlayer: Boolean = player != null,
) {

    fun getPlayerOrThrow(): Player {
        if(player == null) throw CommandException("Command was not executed by player")
        return player
    }

    fun sendMessage(message: String) {
        if(this.isPlayer) this.player!!.sendMessage(message) else this.console.sendMessage(message)
    }

    fun hasPermission(permission: String): Boolean =
        if(this.isPlayer) this.player!!.hasPermission(permission) else true
}
