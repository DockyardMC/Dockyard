package io.github.dockyardmc.bindables

class BindableMap<T, V>(map: Map<T, V>) {

    constructor(vararg list: Pair<T, V>): this(list.toMap())

    private var innerMap: MutableMap<T, V> = mutableMapOf()
    private var removeListener = mutableListOf<BindableMapItemRemoveListener<T, V>>()
    private var changeListener = mutableListOf<BindableMapItemChangeListener<T, V>>()
    private var updateListener = mutableListOf<BindableMapUpdateListener<T, V>>()

    init {
        map.forEach(innerMap::put)
    }

    val values: Map<T, V>
        get() = innerMap.toMap()

    val size: Int
        get() = innerMap.size

    fun addIfNotPresent(key: T, value: V) {
        if(!values.containsKey(key)) set(key, value)
    }

    fun removeIfPresent(item: T) {
        if(values.contains(item)) remove(item)
    }

    fun remove(key: T) {
        val item = innerMap[key] ?: return
        innerMap.remove(key)
        removeListener.forEach { it.unit.invoke(BindableMapItemRemovedEvent<T, V>(key, item)) }
        updateListener.forEach { it.unit.invoke() }
    }

    operator fun set(key: T, value: V) {
        innerMap[key] = value
        changeListener.forEach { it.unit.invoke(BindableMapItemSetEvent<T, V>(key, value)) }
        updateListener.forEach { it.unit.invoke() }
    }

    operator fun contains(target: T): Boolean = values.contains(target)

    class BindableMapItemSetEvent<T, V>(val key: T, val value: V)
    class BindableMapItemRemovedEvent<T, V>(val key: T, val value: V)

    fun itemRemoved(function: (event: BindableMapItemRemovedEvent<T, V>) -> Unit) {
        removeListener.add(BindableMapItemRemoveListener(function))
    }

    fun itemSet(function: (event: BindableMapItemSetEvent<T, V>) -> Unit) {
        changeListener.add(BindableMapItemChangeListener(function))
    }

    fun mapUpdated(function: () -> Unit) {
        updateListener.add(BindableMapUpdateListener(function))
    }

    fun setSilently(key: T, value: V) {
        innerMap[key] = value
    }

    fun removeSilently(key: T) {
        innerMap.remove(key)
    }

    fun triggerUpdate() {
        updateListener.forEach { it.unit.invoke() }
    }

    operator fun get(slot: T): V? = innerMap[slot]

    fun clear(silent: Boolean = false) {
        val map = innerMap.toMutableMap()
        map.forEach {
            if(silent) {
                innerMap.remove(it.key)
            } else {
                remove(it.key)
            }
        }
        updateListener.forEach { it.unit.invoke() }
    }

    class BindableMapItemRemoveListener<T, V>(val unit: (list: BindableMapItemRemovedEvent<T, V>) -> Unit)
    class BindableMapItemChangeListener<T, V>(val unit: (list: BindableMapItemSetEvent<T, V>) -> Unit)
    class BindableMapUpdateListener<T, V>(val unit: () -> Unit)
}