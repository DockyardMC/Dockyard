package io.github.dockyardmc.bindables

class BindableMutableList<T>(vararg list: T) {

    private var innerList: MutableList<T> = mutableListOf()
    private var addListeners = mutableListOf<BindableListItemAddListener<T>>()
    private var removeListener = mutableListOf<BindableListItemRemoveListener<T>>()
    private var changeListener = mutableListOf<BindableListItemChangeListener<T>>()
    private var updateListener = mutableListOf<BindableListUpdateListener<T>>()

    init {
        list.forEach { innerList.add(it) }
    }

    val values: List<T>
        get() = innerList.toList()

    fun add(item: T) {
        innerList.add(item)
        addListeners.forEach { it.unit.invoke(BindableListItemAddEvent<T>(item)) }
        updateListener.forEach { it.unit.invoke(BindableListUpdateEvent<T>(item)) }
    }

    fun remove(item: T) {
        innerList.remove(item)
        updateListener.forEach { it.unit.invoke(BindableListUpdateEvent<T>(item)) }
        removeListener.forEach { it.unit.invoke(BindableListItemRemovedEvent<T>(item)) }
    }

    fun setIndex(index: Int, item: T) {
        innerList[index] = item
        updateListener.forEach { it.unit.invoke(BindableListUpdateEvent<T>(item)) }
        changeListener.forEach { it.unit.invoke(BindableListItemChangeEvent<T>(item)) }
    }

    class BindableListUpdateEvent<T>(val item: T)
    class BindableListItemChangeEvent<T>(val item: T)
    class BindableListItemAddEvent<T>(val item: T)
    class BindableListItemRemovedEvent<T>(val item: T)

    fun itemAdded(function: (event: BindableListItemAddEvent<T>) -> Unit) {
        addListeners.add(BindableListItemAddListener(function))
    }

    fun itemRemoved(function: (event: BindableListItemRemovedEvent<T>) -> Unit) {
        removeListener.add(BindableListItemRemoveListener(function))
    }

    fun itemChanged(function: (event: BindableListItemChangeEvent<T>) -> Unit) {
        changeListener.add(BindableListItemChangeListener(function))
    }

    fun listUpdated(function: (event: BindableListUpdateEvent<T>) -> Unit) {
        updateListener.add(BindableListUpdateListener(function))
    }



    class BindableListItemAddListener<T>(val unit: (list: BindableListItemAddEvent<T>) -> Unit)
    class BindableListItemRemoveListener<T>(val unit: (list: BindableListItemRemovedEvent<T>) -> Unit)
    class BindableListItemChangeListener<T>(val unit: (list: BindableListItemChangeEvent<T>) -> Unit)
    class BindableListUpdateListener<T>(val unit: (list: BindableListUpdateEvent<T>) -> Unit)
}