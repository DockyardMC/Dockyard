package io.github.dockyardmc.spark.command

import io.github.dockyardmc.commands.Command

interface SparkSubCommand {
    fun register(command: Command)
}