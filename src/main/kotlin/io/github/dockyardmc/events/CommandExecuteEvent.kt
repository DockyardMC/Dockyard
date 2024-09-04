package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.commands.Command
import io.github.dockyardmc.commands.CommandExecutor

@EventDocumentation("when command gets executed", true)
class CommandExecuteEvent(var raw: String, var command: Command, var executor: CommandExecutor): CancellableEvent()