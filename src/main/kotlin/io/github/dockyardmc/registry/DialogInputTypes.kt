package io.github.dockyardmc.registry

import io.github.dockyardmc.registry.registries.DialogInputTypeRegistry

object DialogInputTypes {
    val BOOLEAN = DialogInputTypeRegistry["minecraft:boolean"]
    val NUMBER_RANGE = DialogInputTypeRegistry["minecraft:number_range"]
    val SINGLE_OPTION = DialogInputTypeRegistry["minecraft:single_option"]
    val TEXT = DialogInputTypeRegistry["minecraft:text"]
}
