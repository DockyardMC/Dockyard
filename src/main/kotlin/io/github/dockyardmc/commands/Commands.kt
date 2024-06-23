package io.github.dockyardmc.commands

import cz.lukynka.prettylog.log

object Commands {

    val commands: MutableMap<String, Command> = mutableMapOf()

    var autoCorrectCaseSensitivity: Boolean = false
    var warnAboutCaseSensitivity: Boolean = true
    var warnWithClosestMatchToInvalidCommand: Boolean = false

    fun add(name: String, command: (Command) -> Unit) {

        val sanitizedName = name.lowercase().removePrefix("/")
        val builder = Command()
        command(builder)
        val finalCommand = builder.build()
        finalCommand.name = name

        commands[sanitizedName] = finalCommand
        finalCommand.aliases.forEach {

            val aliasCommand = finalCommand.clone()
            aliasCommand.isAlias = true
            aliasCommand.name = it
            commands[it] = aliasCommand
            log("Added command /$it - ${aliasCommand.name}")
        }
    }
}