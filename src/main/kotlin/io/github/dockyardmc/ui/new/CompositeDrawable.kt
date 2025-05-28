package io.github.dockyardmc.ui.new

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableMap
import cz.lukynka.bindables.BindablePool
import io.github.dockyardmc.profiler.profiler
import io.github.dockyardmc.utils.Disposable

abstract class CompositeDrawable(var parent: CompositeDrawable? = null) : Disposable {

    protected val bindablePool = BindablePool()
    private val items: BindableMap<Int, DrawableItemStack> = bindablePool.provideBindableMap()
    private val bindableRefs: MutableMap<Bindable<*>, (Bindable.ValueChangedEvent<*>) -> Unit> = mutableMapOf()
    private val children: MutableMap<CompositeDrawable, Int> = mutableMapOf()
    private var initialized: Boolean = false
    private var isRendering: Boolean = false

    abstract fun buildComponent()

    init {
        items.mapUpdated {
            if (initialized && !isRendering) {
                parent?.renderChildren()
            }
        }
    }

    protected open fun onRenderInternal() {
        profiler("Render ${this::class.simpleName}", true) {
            if (!initialized) {
                buildComponent()
                bindableRefs.forEach { (bindable, _) ->
                    bindable.triggerUpdate()
                }
            }

            renderChildren()
            initialized = true
        }
    }

    protected fun renderChildren() {
        children.forEach { (composite, _) -> this.renderChild(composite) }
    }

    fun withSlot(slot: Int, item: DrawableItemStack) {
        items[slot] = item
    }

    fun withSlot(x: Int, y: Int, item: DrawableItemStack) {
        val index = getSlotIndexFromVector2(x, y)
        withSlot(index, item)
    }

    fun withSlot(slot: Int, builder: DrawableItemStack.Builder.() -> Unit) {
        val instance = DrawableItemStack.Builder()
        builder.invoke(instance)
        items[slot] = instance.build()
    }

    fun withSlot(x: Int, y: Int, builder: DrawableItemStack.Builder.() -> Unit) {
        val index = getSlotIndexFromVector2(x, y)
        val instance = DrawableItemStack.Builder()
        builder.invoke(instance)
        items[index] = instance.build()
    }

    fun withComposite(slot: Int, compositeDrawable: CompositeDrawable) {
        if (children[compositeDrawable] == slot) {
            return
        }

        compositeDrawable.parent = this
        children[compositeDrawable] = slot
    }

    fun withComposite(x: Int, y: Int, compositeDrawable: CompositeDrawable) {
        val slot = getSlotIndexFromVector2(x, y)
        if (children[compositeDrawable] == slot) {
            return
        }

        compositeDrawable.parent = this
        children[compositeDrawable] = slot
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> withBindable(bindable: Bindable<T>, update: (Bindable.ValueChangedEvent<T>) -> Unit) {
        if (bindableRefs.containsKey(bindable)) return
        bindableRefs[bindable] = update as (Bindable.ValueChangedEvent<*>) -> Unit
        bindable.valueChanged(update)
    }

    fun renderChild(child: CompositeDrawable) {
        val offsetIndex = children[child] ?: throw IllegalStateException("${child::class.simpleName} is not child of ${this::class.simpleName}")

        isRendering = true
        try {
            child.onRenderInternal()
            child.items.values.forEach { (slot, item) ->
                this.items[slot + offsetIndex] = item
            }
        } finally {
            isRendering = false
        }
    }

    fun getSlots(): Map<Int, DrawableItemStack> = items.values.toMap()
    fun getChildren(): Map<CompositeDrawable, Int> = children.toMap()
}