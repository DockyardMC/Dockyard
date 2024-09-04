package io.github.dockyardmc.commands

object Commands {

    val commands: MutableMap<String, Command> = mutableMapOf()

    var autoCorrectCaseSensitivity: Boolean = false
    var warnAboutCaseSensitivity: Boolean = true
    var warnWithClosestMatchToInvalidCommand: Boolean = false

    fun add(name: String, builder: Command.() -> Unit): Command {
        val command = Command()
        val sanitizedName = name.lowercase().removePrefix("/")
        command.name = sanitizedName
        builder.invoke(command)

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