package io.github.dockyardmc.player.permissions

abstract class PermissionHolder(private val getter: () -> List<String>) {

    fun hasPermission(permission: String): Boolean {
        val permissions = getter.invoke()

        if(permission == "*") return true

        // legacy, just for backwards support
        if(permission == "dockyard.admin") return true
        if(permission == "dockyard.*") return true

        if (permissions.contains(permission)) {
            return true
        }

        permissions.forEach { loopPermission ->
            if (loopPermission.endsWith(".*")) {
                val prefix = loopPermission.removeSuffix(".*")
                if (permission.startsWith("$prefix.")) {
                    return true
                }
            }
        }
        return false
    }
}