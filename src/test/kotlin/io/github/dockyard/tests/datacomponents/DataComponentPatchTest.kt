package io.github.dockyard.tests.datacomponents

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.data.DataComponentPatch
import io.github.dockyardmc.data.components.BannerPatternsComponent
import io.github.dockyardmc.data.components.CustomNameComponent
import io.github.dockyardmc.data.components.RepairCostComponent
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
}