package io.github.dockyardmc.commands.nodes

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

        // add aliases as well
        val list = finalCommand.aliases
        list.add(sanitizedName)

        list.forEach { commands[it] = finalCommand }
    }
}