package io.github.dockyardmc.registry

import io.github.dockyardmc.registry.registries.DialogTypeRegistry

object DialogTypes {
    val NOTICE = DialogTypeRegistry["minecraft:notice"]
    val SERVER_LINKS = DialogTypeRegistry["minecraft:server_links"]
    val DIALOG_LIST = DialogTypeRegistry["minecraft:dialog_list"]
    val MULTI_ACTION = DialogTypeRegistry["minecraft:multi_action"]
    val MULTI_ACTION_INPUT_FORM = DialogTypeRegistry["minecraft:multi_action_input_form"]
    val SIMPLE_INPUT_FORM = DialogTypeRegistry["minecraft:simple_input_form"]
    val CONFIRMATION = DialogTypeRegistry["minecraft:confirmation"]
}
