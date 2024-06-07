package io.github.dockyardmc.commands

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.CommandExecuteEvent
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.extentions.isUppercase
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Item
import io.github.dockyardmc.registry.Items
import java.util.*

object CommandHandler {
    fun handleCommand(inputCommand: String, executor: CommandExecutor) {
        val tokens = inputCommand.removePrefix("/").split(" ")
        val commandName = tokens[0]
        try {
            if(Commands.commands[commandName] == null) {
                var message = "Command with that name does not exist!"
                if(Commands.warnAboutCaseSensitivity && commandName.isUppercase()) message += " <gray>(check case sensitivity)"
                throw Exception(message)
            }
            val command = Commands.commands[commandName]!!
            if(executor.isPlayer && (!executor.player!!.hasPermission(command.permission))) throw Exception("You do not have enough perms! lol loser")

            var i = 0
            command.children.forEach { (key, value) ->
                i++
                if(tokens.getOrNull(i) == null && !value.optional) {
                    throw Exception("Parameter \"$key\" must be specified!")
                }
            }

            tokens.forEachIndexed { index, value ->
                if(index == 0) return@forEachIndexed
                if(index > command.children.size) return@forEachIndexed

                val childData = command.children.values.toList()[index - 1]

                childData.returnedValue = when(childData.expectedReturnValueType) {
                    String::class -> value
                    Player::class -> PlayerManager.players.firstOrNull { it.username == value }
                    Int::class -> value.toIntOrNull() ?: throw Exception("\"$value\" is not of type Int")
                    Double::class -> value.toDoubleOrNull() ?: throw Exception("\"$value\" is not of type Double")
                    Long::class -> value.toLongOrNull() ?: throw Exception("\"$value\" is not of type Long")
                    UUID::class -> UUID.fromString(value)
                    Item::class -> Items.idToItemMap.values.firstOrNull { it.namespace == value } ?: throw Exception("\"$value\" is not of type Item")
                    Block::class -> Blocks.idToBlockMap.values.firstOrNull { it.namespace == value } ?: throw Exception("\"$value\" is not of type Block")

                    //TODO: brigadier selectors @a @e @s @p @n
                    else -> null
                }
            }

            val event = CommandExecuteEvent(inputCommand, command, executor)
            Events.dispatch(event)
            if(event.cancelled) return

            command.internalExecutorDoNotUse.invoke(executor)
            command.children.values.forEach { it.returnedValue = null }

        } catch (ex: Exception) {
            val message = "<dark_red>Error <dark_gray>| <red>${ex.message}"
            if(executor.isPlayer) {
                executor.player!!.sendMessage(message)
            } else {
                DockyardServer.broadcastMessage(message)
            }
        }
    }
}