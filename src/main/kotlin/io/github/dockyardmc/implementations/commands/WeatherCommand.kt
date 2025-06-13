package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.commands.WorldArgument
import io.github.dockyardmc.extentions.properStrictCase
import io.github.dockyardmc.world.Weather
import io.github.dockyardmc.world.World

class WeatherCommand {
    init {
        Commands.add("/weather") {
            withPermission("dockyard.commands.weather")
            withDescription("Sets a weather in specified world")

            addArgument("weather", EnumArgument(Weather::class))
            addOptionalArgument("world", WorldArgument())

            execute { ctx ->
                val weather = getEnumArgument<Weather>("weather")
                val world = getArgumentOrNull<World>("world") ?: ctx.getPlayerOrThrow().world

                world.weather.value = weather
                ctx.sendMessage("<gray>Set weather in world <white>${world.name} <gray>to <yellow>${weather.name.properStrictCase()}")
            }
        }
    }
}