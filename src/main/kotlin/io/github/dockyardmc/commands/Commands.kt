package io.github.dockyardmc.commands

object Commands {

    val commands: MutableMap<String, Command> = mutableMapOf()

    var autoCorrectCaseSensitivity: Boolean = false
    var warnAboutCaseSensitivity: Boolean = true
    var warnWithClosestMatchToInvalidCommand: Boolean = false

    inline fun add(name: String, builder: Command.() -> Unit): Command {
        val command = Command()
        builder.invoke(command)
        return add(name, command)
    }

    fun add(name: String, command: Command): Command {
        val sanitizedName = name.lowercase().removePrefix("/")
        command.name = sanitizedName

        commands[sanitizedName] = command
        command.aliases.forEach {

            val aliasCommand = command.clone()
            aliasCommand.isAlias = true
            aliasCommand.name = it
            commands[it] = aliasCommand
        }
        return command
    }
}