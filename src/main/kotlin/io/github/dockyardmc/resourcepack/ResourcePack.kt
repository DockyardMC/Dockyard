package io.github.dockyardmc.resourcepack

import io.github.dockyardmc.tide.stream.StreamCodec
import java.util.*

data class ResourcePack(
    val uuid: UUID,
    val url: String,
    val hash: String,
    val required: Boolean,
    val promptMessage: String?,
) {
    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.UUID, ResourcePack::uuid,
            StreamCodec.STRING, ResourcePack::url,
            StreamCodec.STRING, ResourcePack::hash,
            StreamCodec.BOOLEAN, ResourcePack::required,
            StreamCodec.STRING.optional(), ResourcePack::promptMessage,
            ::ResourcePack
        )
    }

    class Builder {
        private var url: String? = null
        private var required: Boolean = false
        private var hash: String = ""
        private var promptMessage: String? = null
        private var id: UUID = UUID.randomUUID()

        fun withUrl(url: String) {
            require(url.startsWith("https://")) { "URL must start with `https://` for minecraft client to accept it" }
            this.url = url
        }

        fun isRequired(required: Boolean) {
            this.required = required
        }

        fun withPromptMessage(message: String) {
            this.promptMessage = message
        }

        fun withId(uuid: UUID) {
            this.id = uuid
        }

        fun withHash(hash: String) {
            this.hash = hash
        }

        fun build(): ResourcePack {
            require(url != null) { "URL cannot be null" }
            return ResourcePack(id, url!!, hash, required, promptMessage)
        }
    }

    enum class Status {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_TO_DOWNLOAD,
        ACCEPTED,
        DOWNLOADED,
        INVALID_URL,
        FAILED_TO_RELOAD,
        DISCARDED
    }
}

fun resourcePack(invoker: ResourcePack.Builder.() -> Unit): ResourcePack {
    val builder = ResourcePack.Builder()
    invoker.invoke(builder)
    return builder.build()
}