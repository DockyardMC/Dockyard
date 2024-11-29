package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.config.ConfigManager

class DockyardCommands {

    init {
        GamemodeCommand()
        VersionAndHelpCommand()
        WorldCommand()
        SoundCommand()
        GiveCommand()
        TeleportCommand()
        SchematicCommand()
        TimeCommand()
        TickRateCommand()
        ClearCommand()
        if(ConfigManager.config.debug) {
            ViewerCommand()
            DebugCommands()
        }
    }
}