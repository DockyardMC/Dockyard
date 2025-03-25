package io.github.dockyard.tests.maths

import io.github.dockyardmc.maths.Percentage
import kotlin.test.Test
import kotlin.test.assertEquals

class PercentageTest {

    @Test
    fun testPercentage() {
        val percentage = Percentage(69.0)
        assertEquals(69.0, percentage.percentage)

        percentage.max = 50.0
        assertEquals(50.0, percentage.percentage)

        percentage.max = null
        percentage.min = 80.0
        assertEquals(80.0, percentage.percentage)

        percentage.min = null
        assertEquals(0.69, percentage.getNormalized())

        assertEquals(34.5, percentage.getValueOf(50.0))
        assertEquals(0.0, percentage.getValueOf(0.0))
        assertEquals(69.0, percentage.getValueOf(100.0))
    }
}