package io.github.dockyardmc.utils

import org.jctools.queues.MessagePassingQueue
import org.jctools.queues.MpmcUnboundedXaddArrayQueue
import java.lang.ref.Cleaner
import java.lang.ref.SoftReference
import java.util.function.UnaryOperator

class ObjectPool<T>(val supplier: () -> T, val sanitizer: UnaryOperator<T> = UnaryOperator.identity<T>()) {
    companion object {
        const val QUEUE_SIZE = 32_768
        val CLEANER = Cleaner.create()
    }

    val size: Int get() = pool.size()
    private val pool: MessagePassingQueue<SoftReference<T>> = MpmcUnboundedXaddArrayQueue(QUEUE_SIZE)

    fun get(): T {
        var ref: SoftReference<T>?
        while (pool.poll().also { ref = it } != null) {
            val result = ref?.get()
            if (result != null) {
                return result
            }
        }
        return supplier.invoke()
    }

    fun add(obj: T) {
        this.pool.offer(SoftReference<T>(sanitizer.apply(obj)))
    }

    fun clear() {
        this.pool.clear()
    }

    fun register(ref: Any, obj: T) {
        CLEANER.register(ref, BufferedCleaner<T>(this, obj))
    }

    class BufferedCleaner<T>(val pool: ObjectPool<T>, val obj: T) : Runnable {
        override fun run() {
            this.pool.add(obj)
        }
    }
}