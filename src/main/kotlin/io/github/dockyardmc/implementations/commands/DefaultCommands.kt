package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.implementations.DefaultImplementationModule

class DefaultCommands: DefaultImplementationModule {

    override fun register() {
        GamemodeCommand()
        VersionAndHelpCommand()
        WorldCommand()
        SoundCommand()
        GiveCommand()
        TeleportCommand()
        TimeCommand()
        SchedulerCommand()
        ClearCommand()
        ListCommand()
        if(ConfigManager.config.debug) {
            DebugCommand()
        }
    }
}