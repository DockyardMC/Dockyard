package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.implementations.DefaultImplementationModule
import io.github.dockyardmc.utils.InstrumentationUtils

class DefaultCommands : DefaultImplementationModule {

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
        EffectCommand()
        WeatherCommand()
        SkinCommand()
        if(InstrumentationUtils.isDebuggerAttached()) DebugCommands.register()
    }
}