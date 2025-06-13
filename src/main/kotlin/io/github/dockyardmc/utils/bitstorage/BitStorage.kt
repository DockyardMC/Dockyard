package io.github.dockyardmc.utils.bitstorage

abstract class BitStorage(val size: Int) {

    abstract val data: LongArray
    abstract val bits: Int

    abstract fun getAndSet(index: Int, value: Int): Int

    abstract operator fun get(index: Int): Int

    abstract operator fun set(index: Int, value: Int)

    abstract fun unpack(output: IntArray)

    abstract fun copy(): BitStorage

    protected fun checkIndex(index: Int) {
        require(index in 0 until size) { "Index must be between 0 and $size (is $index)" }
    }
}