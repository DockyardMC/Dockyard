@file:Suppress("UNCHECKED_CAST")

package io.github.dockyardmc.commands

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSuggestionsResponse
import io.github.dockyardmc.utils.getEnumEntries
import kotlin.reflect.KClass


object SuggestionHandler {

    fun handleSuggestion(transactionId: Int, inputCommand: String, player: Player) {
        val tokens = inputCommand.removePrefix("/").split(" ")
        val commandName = tokens[0]
        val command = Commands.commands[commandName]
        if (Commands.commands[commandName] == null) return

        val current = command!!.arguments.values.toList()[tokens.size - 2]
        val currentlyTyped = tokens.last()

        // Auto suggest enum entries if user defined suggestion is null
        if(current.argument is EnumArgument && current.suggestions == null) {
            val enum = current.argument.enumType as KClass<Enum<*>>
            current.suggestions = SuggestionProvider.withContext { getEnumEntries(enum).map { it.name.lowercase() } }
        }

        val suggestions = current.suggestions ?: return
        val startAt = inputCommand.length - currentlyTyped.length
        val suggestedValues = suggestions.suggestions.invoke(player)

        val filtered = suggestedValues.filter { it.startsWith(currentlyTyped) }

        player.sendPacket(ClientboundSuggestionsResponse(transactionId, startAt, inputCommand.length, filtered))
    }
}