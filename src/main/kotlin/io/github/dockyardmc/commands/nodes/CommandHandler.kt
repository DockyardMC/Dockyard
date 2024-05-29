package io.github.dockyardmc.commands.nodes

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.extentions.isUppercase
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
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
                    // material (block, item)
                    // brigadier selectors
                    else -> null
                }
            }

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