package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.commands.Command
import io.github.dockyardmc.commands.CommandExecutor

@EventDocumentation("when command gets executed")
data class CommandExecuteEvent(var raw: String, var command: Command, var executor: CommandExecutor, override val context: Event.Context) : CancellableEvent()