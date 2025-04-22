package io.github.dockyardmc.player.permissions

import io.github.dockyardmc.extentions.addAllNonDuplicates

abstract class PermissionHolder {

    protected val cachedPermissions: MutableList<String> = mutableListOf()

    // includes inherited permissions
    @JvmName("getCachedPermissionsFunction")
    fun getCachedPermissions(): List<String> {
        return cachedPermissions
    }


    fun buildPermissionCache(permissions: Collection<String>) {
        cachedPermissions.clear()

        permissions.forEach { permission ->
            if (!permission.startsWith("group")) {
                cachedPermissions.add(permission)
            } else {

                val inheritedGroupId = permission.split(".").getOrNull(1) ?: throw IllegalArgumentException("Group inheritance permissions string need be in the following format: `group.<group id>`")
                val inheritedGroup = PermissionManager[inheritedGroupId]

                cachedPermissions.addAllNonDuplicates(inheritedGroup.getCachedPermissions())
            }
        }
    }

    fun hasPermission(permission: String): Boolean {
        if(permission.isEmpty()) return true
        if (cachedPermissions.contains(permission)) return true

        cachedPermissions.forEach { loopPermission ->
            if (loopPermission == "*") return true

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