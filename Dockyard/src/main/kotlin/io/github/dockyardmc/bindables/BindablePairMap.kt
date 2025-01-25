package io.github.dockyardmc.bindables

data class PairKey<T>(val first: T, val second: T)

class BindablePairMap<T, V>(map: Map<PairKey<T>, V>) {

    constructor() : this(mutableMapOf())

    private var innerMap: MutableMap<PairKey<T>, V> = mutableMapOf()
    private var removeListener = mutableListOf<BindablePairMapItemRemoveListener<T, V>>()
    private var changeListener = mutableListOf<BindablePairMapItemChangeListener<T, V>>()
    private var updateListener = mutableListOf<BindablePairMapUpdateListener<T, V>>()

    init {
        map.forEach(innerMap::put)
    }

    // Custom operator for 2D-like key access
    operator fun set(first: T, second: T, value: V) {
        innerMap[PairKey(first, second)] = value
        changeListener.forEach { it.unit.invoke(BindablePairMapItemSetEvent(first, second, value)) }
        updateListener.forEach { it.unit.invoke() }
    }

    val values: Map<PairKey<T>, V>
        get() = innerMap.toMap()

    val size: Int
        get() = innerMap.size

    fun addIfNotPresent(key: PairKey<T>, value: V) {
        if (!values.containsKey(key)) set(key.first, key.second, value)
    }

    fun removeIfPresent(key: PairKey<T>) {
        if (values.contains(key)) remove(key)
    }

    fun remove(key: PairKey<T>) {
        val item = innerMap[key] ?: return
        innerMap.remove(key)
        removeListener.forEach { it.unit.invoke(BindablePairMapItemRemovedEvent(key.first, key.second, item)) }
        updateListener.forEach { it.unit.invoke() }
    }

    operator fun contains(target: PairKey<T>): Boolean = values.contains(target)

    class BindablePairMapItemSetEvent<T, V>(val first: T, val second: T, val value: V)
    class BindablePairMapItemRemovedEvent<T, V>(val first: T, val second: T, val value: V)

    fun itemRemoved(function: (event: BindablePairMapItemRemovedEvent<T, V>) -> Unit) {
        removeListener.add(BindablePairMapItemRemoveListener(function))
    }

    fun itemSet(function: (event: BindablePairMapItemSetEvent<T, V>) -> Unit) {
        changeListener.add(BindablePairMapItemChangeListener(function))
    }

    fun mapUpdated(function: () -> Unit) {
        updateListener.add(BindablePairMapUpdateListener(function))
    }

    fun setSilently(key: PairKey<T>, value: V) {
        innerMap[key] = value
    }

    fun removeSilently(key: PairKey<T>) {
        innerMap.remove(key)
    }

    fun triggerUpdate() {
        updateListener.forEach { it.unit.invoke() }
    }

    operator fun get(key: PairKey<T>): V? = innerMap[key]

    fun clear(silent: Boolean = false) {
        val map = innerMap.toMutableMap()
        map.forEach {
            if (silent) {
                innerMap.remove(it.key)
            } else {
                remove(it.key)
            }
        }
        updateListener.forEach { it.unit.invoke() }
    }

    class BindablePairMapItemRemoveListener<T, V>(val unit: (list: BindablePairMapItemRemovedEvent<T, V>) -> Unit)
    class BindablePairMapItemChangeListener<T, V>(val unit: (list: BindablePairMapItemSetEvent<T, V>) -> Unit)
    class BindablePairMapUpdateListener<T, V>(val unit: () -> Unit)
}
