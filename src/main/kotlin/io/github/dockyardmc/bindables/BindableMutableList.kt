package io.github.dockyardmc.bindables

class BindableMutableList<T> {

    private var innerList: MutableList<T> = mutableListOf()
    private var addListeners = mutableListOf<BindableListItemAddListener<T>>()
    private var removeListener = mutableListOf<BindableListItemRemoveListener<T>>()
    private var changeListener = mutableListOf<BindableListItemChangeListener<T>>()

    val values: List<T>
        get() = innerList.toList()

    fun add(item: T) {
        innerList.add(item)
    }

    fun remove(item: T) {
        innerList.remove(item)
    }

    fun setIndex(index: Int, item: T) {
        innerList[index] = item
    }

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

    class BindableListItemAddListener<T>(val unit: (list: BindableListItemAddEvent<T>) -> Unit)
    class BindableListItemRemoveListener<T>(val unit: (list: BindableListItemRemovedEvent<T>) -> Unit)
    class BindableListItemChangeListener<T>(val unit: (list: BindableListItemChangeEvent<T>) -> Unit)
}