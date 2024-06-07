package io.github.dockyardmc.events

import io.github.dockyardmc.commands.Command
import io.github.dockyardmc.commands.CommandExecutor

class CommandExecuteEvent(var raw: String, var command: Command, var executor: CommandExecutor): CancellableEvent()