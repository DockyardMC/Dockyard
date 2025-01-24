package io.github.dockyardmc.commands

import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.events.CommandExecuteEvent
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.extentions.getLegacyTextColorNameFromVanilla
import io.github.dockyardmc.extentions.isUppercase
import io.github.dockyardmc.extentions.isValidUUID
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.registry.registries.*
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import java.util.*

object CommandHandler {

    val prefix get() = ConfigManager.config.implementationConfig.commandErrorPrefix

    fun handleCommandInput(inputCommand: String, executor: CommandExecutor, testEnv: Boolean = false) {
        DockyardServer.scheduler.run {
            val tokens = inputCommand.removePrefix("/").split(" ").toMutableList()
            val commandName = tokens[0].lowercase()
            try {
                if (Commands.commands[commandName] == null) {
                    var message = "Command with that name does not exist!"
                    if (Commands.warnAboutCaseSensitivity && commandName.isUppercase()) message += " <gray>(check case sensitivity)"
                    throw CommandException(message)
                }
                val command = Commands.commands[commandName]!!
                executor.command = command.name

                if (tokens.size >= 2 && command.subcommands[tokens[1]] != null) {
                    val current = command.subcommands[tokens[1]]!!
                    tokens.removeFirst()
                    handleCommand(current, executor, tokens, inputCommand, commandName)
                } else {
                    handleCommand(command, executor, tokens, inputCommand, commandName)
                }

            } catch (ex: Exception) {
                if (testEnv) throw IllegalArgumentException(ex)
                if (ex is CommandException) {
                    val message = "$prefix${ex.message}"
                    executor.sendMessage(message)
                } else {
                    log(ex)
                    if (ConfigManager.config.implementationConfig.notifyUserOfExceptionDuringCommand) {
                        executor.sendMessage("${prefix}A <orange>${ex::class.qualifiedName} <red>was thrown during execution of this command!")
                    }
                }
            }
        }
    }


    fun handleCommand(
        command: Command,
        executor: CommandExecutor,
        tokens: MutableList<String>,
        inputCommand: String,
        rootCommandName: String
    ) {
        var fullCommandString = "/${rootCommandName.replace("/", "")} ${command.name} "
        command.arguments.forEach { argument ->
            if (argument.value.optional) fullCommandString += "["
            fullCommandString += "\\<${argument.key}"
            fullCommandString += if (argument.value.optional) ">] " else "> "
        }

        if (executor.isPlayer && (!executor.player!!.hasPermission(command.permission))) throw CommandException(
            ConfigManager.config.implementationConfig.commandNoPermissionsMessage
        )

        var i = 0
        command.arguments.forEach { (key, value) ->
            i++
            if (tokens.getOrNull(i) == null && !value.optional) {
                throw CommandException("Missing argument<orange> \\<$key><red> in <yellow>$fullCommandString")
            }
        }

        var foundGreedyString: Boolean = false

        tokens.forEachIndexed { index, value ->
            if (index == 0) return@forEachIndexed
            if (index > command.arguments.size) return@forEachIndexed
            if (foundGreedyString) return@forEachIndexed

            val argumentData = command.arguments.values.toList()[index - 1]

            if (argumentData.argument is StringArgument) {
                val argument = argumentData.argument as StringArgument
                if (argument.type == BrigadierStringType.GREEDY_PHRASE) {
                    foundGreedyString = true
                    val string = tokens.subList(index, tokens.size).joinToString(" ")
                    argumentData.returnedValue = string
                    return@forEachIndexed
                }
            }


            argumentData.returnedValue = when (argumentData.expectedReturnValueType) {
                Boolean::class -> value == "true"
                String::class -> value
                Player::class -> PlayerManager.players.firstOrNull { it.username == value }
                    ?: throw CommandException("Player $value is not online or the supplied name is invalid!")

                Int::class -> value.toIntOrNull() ?: throw CommandException("\"$value\" is not of type Int")
                Double::class -> value.toDoubleOrNull() ?: throw CommandException("\"$value\" is not of type Double")
                Float::class -> value.toFloatOrNull() ?: throw CommandException("\"$value\" is not of type Float")
                Long::class -> value.toLongOrNull() ?: throw CommandException("\"$value\" is not of type Long")
                UUID::class -> UUID.fromString(value)
                Item::class -> ItemRegistry.get(value) ?: throw CommandException("\"$value\" is not of type Item")
                RegistryBlock::class -> {
                    if (value.contains("[")) {
                        //block state
                        val states = Block.parseBlockStateString(value)
                        val block =
                            BlockRegistry.protocolIdToBlock.values.firstOrNull { it.identifier == states.first }
                                ?: throw CommandException("\"${states.first}\" is not of type Block")
                        block.withBlockStates(states.second)
                    } else {
                        //not block state
                        BlockRegistry.protocolIdToBlock.values.firstOrNull { it.identifier == value }
                            ?: throw CommandException("\"$value\" is not of type Block")
                    }
                }

                World::class -> WorldManager.worlds[value]
                    ?: throw CommandException("World with name $value does not exist!")

                Sound::class -> Sound(value)
                Entity::class -> {
                    if (value.contains("-")) {
                        if (!isValidUUID(value)) throw CommandException("Provided UUID is not in valid UUID format!")
                        val uuid = UUID.fromString(value)
                        EntityManager.entities.firstOrNull { it.uuid == UUID.fromString(value) }
                            ?: throw CommandException("Entity with that UUID does not exist!")
                    } else {
                        val id = value.toIntOrNull() ?: throw CommandException("Provided entity id is not of type Int")
                        EntityManager.entities.firstOrNull { it.entityId == id }
                            ?: throw CommandException("Entity with that entity id does not exist!")
                    }
                }

                LegacyTextColor::class -> {
                    val name = getLegacyTextColorNameFromVanilla(value.lowercase())
                    if (!LegacyTextColor.entries.map { it.name.lowercase() }
                            .contains(name)) throw CommandException("$value is not valid LegacyTextColor!")
                    LegacyTextColor.valueOf(name.uppercase())
                }

                Particle::class -> {
                    ParticleRegistry.getOrNull(value)
                        ?: throw CommandException("$value is not valid Particle in the registry!")
                }

                else -> null
            }
        }


        val event = CommandExecuteEvent(inputCommand, command, executor)
        Events.dispatch(event)
        if (event.cancelled) return

        command.internalExecutorDoNotUse.invoke(executor)
        command.arguments.values.forEach { it.returnedValue = null }
    }
}