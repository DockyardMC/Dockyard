package io.github.dockyardmc.commands

import cz.lukynka.prettylog.log
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.events.CommandExecuteEvent
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.extentions.isUppercase
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Item
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import java.util.*

object CommandHandler {

    val prefix get() = ConfigManager.currentConfig.implementationConfig.commandErrorPrefix

    fun handleCommandInput(inputCommand: String, executor: CommandExecutor) {
        val tokens = inputCommand.removePrefix("/").split(" ").toMutableList()
        val commandName = tokens[0]
        try {
            if(Commands.commands[commandName] == null) {
                var message = "Command with that name does not exist!"
                if(Commands.warnAboutCaseSensitivity && commandName.isUppercase()) message += " <gray>(check case sensitivity)"
                throw CommandException(message)
            }
            val command = Commands.commands[commandName]!!
            executor.command = command.name

            if(tokens.size >= 2 && command.subcommands[tokens[1]] != null) {
                val current = command.subcommands[tokens[1]]!!
                tokens.removeFirst()
                handleCommand(current, executor, tokens, inputCommand, commandName)
            } else {
                if(command.subcommands.isNotEmpty()) {
                    var fullCommandString = "/${command.name.replace("/", "")} ("
                    command.subcommands.forEach {
                        fullCommandString += (it.key)
                        if(command.subcommands.size == 1 || command.subcommands.keys.last() == it.key) return@forEach else fullCommandString += ", "
                    }
                    fullCommandString += ")"
                    throw CommandException("Missing subcommand: <orange>$fullCommandString")
                }
                handleCommand(command, executor, tokens, inputCommand, commandName)
            }

        } catch (ex: Exception) {
            if(ex is CommandException) {
                val message = "$prefix${ex.message}"
                executor.sendMessage(message)
            } else {
                log(ex)
                if(ConfigManager.currentConfig.implementationConfig.notifyUserOfExceptionDuringCommand) {
                    executor.sendMessage("${prefix}A <orange>${ex::class.qualifiedName} <red>was thrown during execution of this command!")
                }
            }
        }
    }

    fun handleCommand(command: Command, executor: CommandExecutor, tokens: MutableList<String>, inputCommand: String, rootCommandName: String) {
        var fullCommandString = "/${rootCommandName.replace("/", "")} ${command.name} "
        command.arguments.forEach { argument ->
            if(argument.value.optional) fullCommandString +="["
            fullCommandString += "\\<${argument.key}"
            fullCommandString += if(argument.value.optional) ">] " else "> "
        }

        if(executor.isPlayer && (!executor.player!!.hasPermission(command.permission))) throw CommandException(ConfigManager.currentConfig.implementationConfig.commandNoPermissionsMessage)

        var i = 0
        command.arguments.forEach { (key, value) ->
            i++
            if(tokens.getOrNull(i) == null && !value.optional) {
                throw CommandException("Missing argument<orange> \\<$key><red> in <yellow>$fullCommandString")
            }
        }

        tokens.forEachIndexed { index, value ->
            if(index == 0) return@forEachIndexed
            if(index > command.arguments.size) return@forEachIndexed

            val argumentData = command.arguments.values.toList()[index - 1]

            argumentData.returnedValue = when(argumentData.expectedReturnValueType) {
                String::class -> value
                Player::class -> {
                    when(value) {
                        "@s" -> executor.player!!
                        "@p" -> executor.player!!
                        else -> PlayerManager.players.firstOrNull { it.username == value }
                    }
                }
                Int::class -> value.toIntOrNull() ?: throw CommandException("\"$value\" is not of type Int")
                Double::class -> value.toDoubleOrNull() ?: throw CommandException("\"$value\" is not of type Double")
                Float::class -> value.toFloatOrNull() ?: throw CommandException("\"$value\" is not of type Float")
                Long::class -> value.toLongOrNull() ?: throw CommandException("\"$value\" is not of type Long")
                UUID::class -> UUID.fromString(value)
                Item::class -> Items.idToItemMap.values.firstOrNull { it.identifier == value.replace("minecraft:", "") } ?: throw CommandException("\"$value\" is not of type Item")
                Block::class -> Blocks.idToBlockMap.values.firstOrNull { it.identifier == value.replace("minecraft:", "") } ?: throw CommandException("\"$value\" is not of type Block")
                World::class -> WorldManager.worlds[value]
                Sound::class -> Sound(value)

                //TODO: brigadier selectors @a @e @s @p @n
                else -> null
            }
        }

        val event = CommandExecuteEvent(inputCommand, command, executor)
        Events.dispatch(event)
        if(event.cancelled) return

        command.internalExecutorDoNotUse.invoke(executor)
        command.arguments.values.forEach { it.returnedValue = null }
    }
}