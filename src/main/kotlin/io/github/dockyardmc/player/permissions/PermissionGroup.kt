package io.github.dockyardmc.player.permissions

data class PermissionGroup(val id: String, private val permissions: List<String>) : PermissionHolder() {

    init {
        buildPermissionCache(permissions)
    }

    class Builder {
        var id: String? = null
        val permissions: MutableList<String> = mutableListOf()

        fun withId(id: String) {
            this.id = id.lowercase()
        }

        fun withPermissions(permissions: List<String>) {
            this.permissions.addAll(permissions)
        }

        fun withPermissions(vararg permissions: String) {
            this.permissions.addAll(permissions.toList())
        }
    }
}