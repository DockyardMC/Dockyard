package io.github.dockyard.tests.datacomponents

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.data.DataComponentPatch
import io.github.dockyardmc.data.components.BannerPatternsComponent
import io.github.dockyardmc.data.components.CustomNameComponent
import io.github.dockyardmc.data.components.ItemNameComponent
import io.github.dockyardmc.data.components.RepairCostComponent
import io.github.dockyardmc.scroll.extensions.toComponent
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DataComponentPatchTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testComponentPatch() {
        val patch = DataComponentPatch(Int2ObjectArrayMap(), false, true)
        patch.set(RepairCostComponent(10))
        patch.remove(CustomNameComponent::class)

        assertTrue(patch.has(RepairCostComponent::class))
        assertEquals(10, patch.get<RepairCostComponent>()?.cost)

        assertFalse(patch.has(CustomNameComponent::class))
        assertNull(patch.get<CustomNameComponent>())

        assertFalse(patch.has(BannerPatternsComponent::class))
        assertNull(patch.get<BannerPatternsComponent>())
    }

    @Test
    fun testPatchedGet() {
        val prototype = DataComponentPatch(Int2ObjectArrayMap(), false, true)
        prototype.set(ItemNameComponent("Hello"))
        prototype.set(RepairCostComponent(69))
        prototype.set(CustomNameComponent("World"))

        val patch = DataComponentPatch(Int2ObjectArrayMap(), false, true)
        patch.set(RepairCostComponent(1))
        patch.remove(CustomNameComponent::class)

        // override
        assertTrue(patch.has(prototype, RepairCostComponent::class))
        assertEquals(1, patch.get<RepairCostComponent>(prototype, RepairCostComponent::class)?.cost)

        // inherit
        assertTrue(patch.has(prototype, RepairCostComponent::class))
        assertEquals("Hello".toComponent().stripStyling(), patch.get<ItemNameComponent>(prototype, ItemNameComponent::class)?.itemName?.stripStyling())

        // delete
        assertFalse(patch.has(prototype, CustomNameComponent::class))
        assertNull(patch[prototype, CustomNameComponent::class])

        // non existent
        assertFalse(patch.has(prototype, BannerPatternsComponent::class))
        assertNull(patch[prototype, BannerPatternsComponent::class])
    }
}