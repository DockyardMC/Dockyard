package io.github.dockyardmc.bindables

class BindableMap<T, V>(map: Map<T, V>) {

    constructor(vararg list: Pair<T, V>): this(list.toMap())

    private var innerMap: MutableMap<T, V> = mutableMapOf()
    private var addListeners = mutableListOf<BindableMapItemAddListener<T, V>>()
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

    fun add(key: T, value: V) {
        innerMap[key] = value
        addListeners.forEach { it.unit.invoke(BindableMapItemAddEvent<Pair<T, V>>(key to value)) }
        updateListener.forEach { it.unit.invoke() }
    }

    fun addIfNotPresent(key: T, value: V) {
        if(!values.containsKey(key)) add(key, value)
    }

    fun removeIfPresent(item: T) {
        if(values.contains(item)) remove(item)
    }

    fun remove(key: T) {
        val item = innerMap[key] ?: return
        innerMap.remove(key)
        removeListener.forEach { it.unit.invoke(BindableMapItemRemovedEvent<Pair<T, V>>(key to item)) }
        updateListener.forEach { it.unit.invoke() }
    }

    operator fun set(key: T, value: V) {
        innerMap[key] = value
        changeListener.forEach { it.unit.invoke(BindableMapItemChangeEvent<Pair<T, V>>(key to value)) }
        updateListener.forEach { it.unit.invoke() }
    }

    operator fun contains(target: T): Boolean = values.contains(target)

    class BindableMapUpdateEvent<T>(val item: T?)
    class BindableMapItemChangeEvent<T>(val item: T)
    class BindableMapItemAddEvent<T>(val item: T)
    class BindableMapItemRemovedEvent<T>(val item: T)

    fun itemAdded(function: (event: BindableMapItemAddEvent<Pair<T, V>>) -> Unit) {
        addListeners.add(BindableMapItemAddListener(function))
    }

    fun itemRemoved(function: (event: BindableMapItemRemovedEvent<Pair<T, V>>) -> Unit) {
        removeListener.add(BindableMapItemRemoveListener(function))
    }

    fun itemChanged(function: (event: BindableMapItemChangeEvent<Pair<T, V>>) -> Unit) {
        changeListener.add(BindableMapItemChangeListener(function))
    }

    fun listUpdated(function: () -> Unit) {
        updateListener.add(BindableMapUpdateListener(function))
    }

    fun triggerUpdate() {
        updateListener.forEach { it.unit.invoke() }
    }

    class BindableMapItemAddListener<T, V>(val unit: (list: BindableMapItemAddEvent<Pair<T, V>>) -> Unit)
    class BindableMapItemRemoveListener<T, V>(val unit: (list: BindableMapItemRemovedEvent<Pair<T, V>>) -> Unit)
    class BindableMapItemChangeListener<T, V>(val unit: (list: BindableMapItemChangeEvent<Pair<T, V>>) -> Unit)
    class BindableMapUpdateListener<T, V>(val unit: () -> Unit)
}

