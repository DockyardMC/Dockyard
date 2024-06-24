package io.github.dockyardmc.bindables

class Bindable<T>(initialValue: T) {

    private var bindableValue = initialValue
    private var changeListeners = mutableListOf<ValueChangeListener<T>>()

    var value: T
        get() = bindableValue
        set(value) {
            changeListeners.forEach { it.unit.invoke(ValueChangedEvent<T>(bindableValue, value)) }
            bindableValue = value
        }

    fun setSilently(value: T) {
        bindableValue = value
    }

    fun valueChanged(function: (event: ValueChangedEvent<T>) -> Unit) {
        changeListeners.add(ValueChangeListener<T>(function))
    }

    class ValueChangeListener<T>(
        val unit: (list: ValueChangedEvent<T>) -> Unit
    )

    class ValueChangedEvent<T>(
        val oldValue: T,
        val newValue: T
    )
}

fun Bindable<Boolean>.toggle() {
    this.value = !this.value
}