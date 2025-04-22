package io.github.dockyardmc.player.permissions

import io.github.dockyardmc.extentions.addAllNonDuplicates
import java.lang.IllegalArgumentException

data class PermissionGroup(val id: String, private val permissions: List<String>): PermissionHolder(PermissionGroup::getCachedPermissions::call) {

    // includes inherited permissions
    private val cachedPermissions: MutableList<String> = permissions.toMutableList()

    @JvmName("getCachedPermissionsFunction")
    fun getCachedPermissions(): List<String> {
        return cachedPermissions
    }

    init {
        // cache inherited permissions
        permissions.forEach { permission ->
            if(!permission.startsWith("group")) return@forEach

            val inheritedGroupId = permission.split(".").getOrNull(1) ?: throw IllegalArgumentException("Group inheritance permissions string need be in the following format: `group.<group id>`")
            val inheritedGroup = PermissionManager[inheritedGroupId]

            cachedPermissions.addAllNonDuplicates(inheritedGroup.getCachedPermissions())
        }
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