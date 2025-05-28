package io.github.dockyardmc.registry

import io.github.dockyardmc.registry.registries.DialogActionTypeRegistry

object DialogActionTypes {
    // i hate this
    // STATIC ACTIONS
    val OPEN_URL = DialogActionTypeRegistry["open_url"]
    val RUN_COMMAND = DialogActionTypeRegistry["run_command"]
    val SUGGEST_COMMAND = DialogActionTypeRegistry["suggest_command"]
    val CHANGE_PAGE = DialogActionTypeRegistry["change_page"]
    val COPY_TO_CLIPBOARD = DialogActionTypeRegistry["copy_to_clipboard"]
    val SHOW_DIALOG = DialogActionTypeRegistry["show_dialog"]
    val CUSTOM = DialogActionTypeRegistry["custom"]

    // DYNAMIC ACTIONS
    val DYNAMIC_RUN_COMMAND = DialogActionTypeRegistry["dynamic/run_command"]
    val DYNAMIC_CUSTOM = DialogActionTypeRegistry["dynamic/custom"]
}