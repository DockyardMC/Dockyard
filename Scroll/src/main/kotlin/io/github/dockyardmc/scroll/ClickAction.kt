package io.github.dockyardmc.scroll

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ClickAction {
    @SerialName("open_url")
    OPEN_URL,
    @SerialName("open_file")
    OPEN_FILE,
    @SerialName("run_command")
    RUN_COMMAND,
    @SerialName("suggest_command")
    SUGGEST_COMMAND,
    @SerialName("change_page")
    CHANGE_PAGE,
    @SerialName("copy_to_clipboard")
    COPY_TO_CLIPBOARD
}