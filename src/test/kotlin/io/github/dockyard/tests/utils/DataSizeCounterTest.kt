package io.github.dockyard.tests.utils

import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.utils.DataSizeCounter
import kotlin.test.Test
import kotlin.test.assertEquals

class DataSizeCounterTest {

    @Test
    fun test() {
        val counter = DataSizeCounter()

        counter.add(1000, DataSizeCounter.Type.BYTE)

        assertEquals(0.9765625.truncate(2).toDouble(), counter.getSize(DataSizeCounter.Type.KILOBYTE))
        assertEquals(0.00095367431.truncate(2).toDouble(), counter.getSize(DataSizeCounter.Type.MEGABYTE))
        assertEquals(9.31322575e-7.truncate(2).toDouble(), counter.getSize(DataSizeCounter.Type.GIGABYTE))
        assertEquals(9.094947e-10.truncate(2).toDouble(), counter.getSize(DataSizeCounter.Type.TERABYTE))

        counter.reset()

        assertEquals(0.0, counter.getSize(DataSizeCounter.Type.BYTE))
        assertEquals(0.0, counter.getSize(DataSizeCounter.Type.KILOBYTE))
        assertEquals(0.0, counter.getSize(DataSizeCounter.Type.MEGABYTE))
        assertEquals(0.0, counter.getSize(DataSizeCounter.Type.GIGABYTE))
        assertEquals(0.0, counter.getSize(DataSizeCounter.Type.TERABYTE))

        counter.remove(1000, DataSizeCounter.Type.BYTE)

        assertEquals(-0.9765625.truncate(2).toDouble(), counter.getSize(DataSizeCounter.Type.KILOBYTE))
        assertEquals(-0.00095367431.truncate(2).toDouble(), counter.getSize(DataSizeCounter.Type.MEGABYTE))
        assertEquals(-9.31322575e-7.truncate(2).toDouble(), counter.getSize(DataSizeCounter.Type.GIGABYTE))
        assertEquals(-9.094947e-10.truncate(2).toDouble(), counter.getSize(DataSizeCounter.Type.TERABYTE))
    }
}