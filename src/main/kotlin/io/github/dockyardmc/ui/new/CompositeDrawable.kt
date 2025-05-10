package io.github.dockyardmc.ui.new

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableMap
import cz.lukynka.bindables.BindablePool
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.debug

abstract class CompositeDrawable(val parent: CompositeDrawable? = null) : Disposable {

    protected val bindablePool = BindablePool()
    private val items: BindableMap<Int, DrawableItem> = bindablePool.provideBindableMap()
    private val bindableRefs: MutableMap<Bindable<*>, (Bindable.ValueChangedEvent<*>) -> Unit> = mutableMapOf()
    private val children: MutableMap<CompositeDrawable, Int> = mutableMapOf()

    abstract fun buildComponent()

    init {
        items.mapUpdated {
            parent?.renderChildren()
        }
    }

    protected fun onRenderInternal() {
        buildComponent()
        renderChildren()
        bindableRefs.forEach { (bindable, _) -> bindable.triggerUpdate() }
    }

    protected fun renderChildren() {
        children.forEach { (composite, _) -> this.renderChild(composite) }
    }

    fun withSlot(slot: Int, item: DrawableItem) {
        items[slot] = item
    }

    fun withSlot(slot: Int, builder: DrawableItem.Builder.() -> Unit) {
        val instance = DrawableItem.Builder()
        builder.invoke(instance)
        items[slot] = instance.build()
    }

    fun withComposite(slot: Int, compositeDrawable: CompositeDrawable) {
        debug("added composite ${compositeDrawable::class.simpleName} with slot $slot (this -> ${this::class.simpleName}", true)
        children[compositeDrawable] = slot
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> withBindable(bindable: Bindable<T>, update: (Bindable.ValueChangedEvent<T>) -> Unit) {
        bindableRefs[bindable] = update as (Bindable.ValueChangedEvent<*>) -> Unit
    }

    fun renderChild(child: CompositeDrawable) {
        debug("${this::class.simpleName} -> rendering child ${child::class.simpleName}", true)
        val offsetIndex = children[child] ?: throw IllegalStateException("${child::class.simpleName} is not child of ${this::class.simpleName}")

        child.onRenderInternal()
        child.items.values.forEach { (slot, item) ->
            this.items[slot + offsetIndex] = item
        }
    }

    fun getSlots(): Map<Int, DrawableItem> = items.values.toMap()
    fun getChildren(): Map<CompositeDrawable, Int> = children.toMap()
}