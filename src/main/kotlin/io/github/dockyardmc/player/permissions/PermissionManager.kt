package io.github.dockyardmc.player.permissions

object PermissionManager {
    private val groups: MutableMap<String, PermissionGroup> = mutableMapOf<String, PermissionGroup>()

    fun getOrNull(id: String): PermissionGroup? {
        val indexId = if (id.contains("group.")) id.split(".").getOrNull(1)!! else id
        return groups[indexId]
    }

    operator fun get(id: String): PermissionGroup {
        return getOrNull(id) ?: throw IllegalArgumentException("Permissions group with id `$id` is not registered")
    }

    fun addGroup(group: PermissionGroup.Builder.() -> Unit): PermissionGroup {
        val builder = PermissionGroup.Builder()
        group.invoke(builder)

        val lowercaseId = requireNotNull(builder.id) { "Id of a permissions group cannot be empty" }
            .lowercase()

        require(!lowercaseId.contains(".")) { "Permissions group ids cannot contain dots" }
        require(!groups.containsKey(lowercaseId)) { "Permission group with the id of $lowercaseId already exists" }

        builder.id = lowercaseId
        val newGroup = PermissionGroup(lowercaseId, builder.permissions)
        groups[lowercaseId] = newGroup

        return newGroup
    }

    fun removeGroup(group: PermissionGroup) {
        groups.remove(group.id)
    }

    fun removeAll() {
        groups.clear()
    }

    fun removeGroup(id: String) {
        groups.remove(id)
    }
}