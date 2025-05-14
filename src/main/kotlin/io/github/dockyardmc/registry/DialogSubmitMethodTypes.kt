package io.github.dockyardmc.registry

import io.github.dockyardmc.registry.registries.DialogSubmitMethodTypeRegistry

object DialogSubmitMethodTypes {
    val COMMAND_TEMPLATE = DialogSubmitMethodTypeRegistry["command_template"]
    val CUSTOM_TEMPLATE = DialogSubmitMethodTypeRegistry["custom_template"]
    val CUSTOM_FORM = DialogSubmitMethodTypeRegistry["custom_form"]
}