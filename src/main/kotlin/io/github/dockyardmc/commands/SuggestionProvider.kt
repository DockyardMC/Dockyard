package io.github.dockyardmc.commands

import io.github.dockyardmc.player.Player

object SuggestionProvider {

    fun simple(suggestion: List<String>): CommandSuggestions = withContext { suggestion }

    fun simple(vararg suggestion: String): CommandSuggestions = withContext { suggestion.toList() }

    fun withContext(suggestion: (executor: Player) -> List<String>): CommandSuggestions =
        CommandSuggestions(suggestion)

    fun simple(suggestion: String): CommandSuggestions = simple(listOf(suggestion))
}

class CommandSuggestions(val suggestions: (Player) -> List<String>)