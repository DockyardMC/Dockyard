package io.github.dockyard.tests.permissions

import cz.lukynka.prettylog.log
import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.player.permissions.PermissionManager
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerPermissionTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
        PlayerTestUtil.getOrCreateFakePlayer()
    }

    @Test
    fun testPermissions() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.permissions.clear()

        player.permissions.add("dockyard.commands.gamemode.*")
        player.permissions.add("dockyard.commands.teleport")
        player.permissions.add("dockyard.commands.time")

        assertEquals(true, player.hasPermission("dockyard.commands.gamemode.creative"))
        assertEquals(true, player.hasPermission("dockyard.commands.teleport"))
        assertEquals(true, player.hasPermission("dockyard.commands.time"))
        assertEquals(false, player.hasPermission("dockyard.commands.scheduler"))

        player.permissions.clear()

        assertEquals(false, player.hasPermission("dockyard.commands.gamemode.creative"))
        assertEquals(false, player.hasPermission("dockyard.commands.teleport"))
        assertEquals(false, player.hasPermission("dockyard.commands.time"))
        assertEquals(false, player.hasPermission("dockyard.commands.scheduler"))
    }

    @Test
    fun testPermissionGroups() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.permissions.clear()

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

        player.permissions.add("group.builder")

        assertEquals(true, player.hasPermission("worldedit.commands.paste"))
        assertEquals(true, player.hasPermission("dockyard.commands.gamemode.adventure"))
        assertEquals(true, player.hasPermission("dockyard.commands.gamemode.creative"))

        assertEquals(false, player.hasPermission("dockyard.commands.ban"))
        assertEquals(false, player.hasPermission("dockyard.commands.kick"))
        assertEquals(false, player.hasPermission("dockyard.commands.mute"))

        player.permissions.add("group.moderator")

        assertEquals(true, player.hasPermission("worldedit.commands.paste"))
        assertEquals(true, player.hasPermission("dockyard.commands.gamemode.adventure"))
        assertEquals(true, player.hasPermission("dockyard.commands.gamemode.creative"))

        assertEquals(true, player.hasPermission("dockyard.commands.ban"))
        assertEquals(true, player.hasPermission("dockyard.commands.kick"))
        assertEquals(true, player.hasPermission("dockyard.commands.mute"))

        player.permissions.remove("group.moderator")
        player.permissions.remove("group.builder")

        assertEquals(false, player.hasPermission("worldedit.commands.paste"))
        assertEquals(false, player.hasPermission("dockyard.commands.gamemode.adventure"))
        assertEquals(false, player.hasPermission("dockyard.commands.gamemode.creative"))

        assertEquals(false, player.hasPermission("dockyard.commands.ban"))
        assertEquals(false, player.hasPermission("dockyard.commands.kick"))
        assertEquals(false, player.hasPermission("dockyard.commands.mute"))

        player.permissions.add("group.admin")

        assertEquals(true, player.hasPermission("worldedit.commands.paste"))
        assertEquals(true, player.hasPermission("dockyard.commands.gamemode.adventure"))
        assertEquals(true, player.hasPermission("dockyard.commands.gamemode.creative"))

        assertEquals(true, player.hasPermission("dockyard.commands.ban"))
        assertEquals(true, player.hasPermission("dockyard.commands.kick"))
        assertEquals(true, player.hasPermission("dockyard.commands.mute"))

        player.permissions.clear()
        player.permissions.add("*")

        assertEquals(true, player.hasPermission("worldedit.commands.paste"))
        assertEquals(true, player.hasPermission("dockyard.commands.gamemode.adventure"))
        assertEquals(true, player.hasPermission("dockyard.commands.gamemode.creative"))

        assertEquals(true, player.hasPermission("dockyard.commands.ban"))
        assertEquals(true, player.hasPermission("dockyard.commands.kick"))
        assertEquals(true, player.hasPermission("dockyard.commands.mute"))
    }
}