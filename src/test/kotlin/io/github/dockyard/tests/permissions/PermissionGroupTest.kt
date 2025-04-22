package io.github.dockyard.tests.permissions

import io.github.dockyardmc.player.permissions.PermissionManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class PermissionGroupTest {

    @Test
    fun testPermissionGroup() {
        val group = PermissionManager.addGroup {
            withId("admin")
            withPermissions("dockyard.commands.gamemode.*")
            withPermissions("dockyard.commands.teleport")
            withPermissions("dockyard.commands.fly")
        }

        assertEquals(true, group.hasPermission("dockyard.commands.gamemode.creative"))
        assertEquals(true, group.hasPermission("dockyard.commands.teleport"))
        assertEquals(true, group.hasPermission("dockyard.commands.fly"))
        assertEquals(false, group.hasPermission("dockyard.commands.time"))

        PermissionManager.removeGroup(group)
    }

    @Test
    fun testInheritance() {
        val builderGroup = PermissionManager.addGroup {
            withId("builder")
            withPermissions("dockyard.commands.gamemode.*")
            withPermissions("dockyard.commands.teleport")
            withPermissions("dockyard.commands.fly")
            withPermissions("worldedit.*")
        }

        val moderatorGroup = PermissionManager.addGroup {
            withId("moderator")
            withPermissions("dockyard.commands.ban")
            withPermissions("dockyard.commands.kick")
            withPermissions("dockyard.commands.mute")
        }

        val adminGroup = PermissionManager.addGroup {
            withId("admin")
            withPermissions("group.builder")
            withPermissions("group.moderator")
        }

        assertEquals(true, adminGroup.hasPermission("dockyard.commands.gamemode.creative"))
        assertEquals(true, adminGroup.hasPermission("dockyard.commands.gamemode.survival"))

        assertEquals(true, adminGroup.hasPermission("dockyard.commands.ban"))
        assertEquals(true, adminGroup.hasPermission("dockyard.commands.kick"))
        assertEquals(true, adminGroup.hasPermission("dockyard.commands.mute"))

        assertEquals(false, builderGroup.hasPermission("dockyard.commands.ban"))
        assertEquals(false, builderGroup.hasPermission("dockyard.commands.kick"))
        assertEquals(false, builderGroup.hasPermission("dockyard.commands.mute"))

        assertEquals(false, moderatorGroup.hasPermission("dockyard.commands.gamemode.creative"))
        assertEquals(false, moderatorGroup.hasPermission("dockyard.commands.gamemode.survival"))
        assertEquals(false, moderatorGroup.hasPermission("worldedit.commands.paste"))

        PermissionManager.removeGroup(builderGroup)
        PermissionManager.removeGroup(adminGroup)
        PermissionManager.removeGroup(moderatorGroup)
    }

    @Test
    fun testCreation() {
        assertThrows<IllegalArgumentException> {
            PermissionManager.addGroup {
                withId("group.admin")
            }
        }

        assertThrows<IllegalArgumentException> {
            PermissionManager.addGroup {
                withId("admin")
            }
            PermissionManager.addGroup {
                withId("admin")
            }
        }
        PermissionManager.removeGroup("admin")

        assertThrows<IllegalArgumentException> {
            PermissionManager.addGroup {
                withId("admin")
                withPermissions("group.test")
            }
        }

        assertThrows<IllegalArgumentException> {
            PermissionManager.addGroup {
                withId("admin")
                withPermissions("group.")
            }
        }

        assertThrows<IllegalArgumentException> {
            PermissionManager.addGroup {
                withId("admin")
                withPermissions("group.admin")
            }
        }
        PermissionManager.removeGroup("admin")
    }
}