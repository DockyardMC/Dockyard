package io.github.dockyardmc.commands

object Commands {

    val commands: MutableMap<String, Command> = mutableMapOf()

    var autoCorrectCaseSensitivity: Boolean = false
    var warnAboutCaseSensitivity: Boolean = true
    var warnWithClosestMatchToInvalidCommand: Boolean = false

    fun subcommandBase(name: String): Command {
        val command = Command()
        command.name = name.lowercase().removePrefix("/")
        commands[command.name] = command
        return command
    }

    fun add(name: String, command: (Command) -> Unit): Command {

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
        }
        return finalCommand
    }
}