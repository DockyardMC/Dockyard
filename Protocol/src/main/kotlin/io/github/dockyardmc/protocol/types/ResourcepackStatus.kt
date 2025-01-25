package io.github.dockyardmc.protocol.types

enum class ResourcepackStatus {
    SUCCESSFULLY_LOADED,
    DECLINED,
    FAILED_TO_DOWNLOAD,
    ACCEPTED,
    DOWNLOADED,
    INVALID_URL,
    FAILED_TO_RELOAD,
    DISCARDED
}