package io.github.dockyardmc.attributes

import cz.lukynka.bindables.BindablePool
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Attributes
import io.github.dockyardmc.registry.registries.Attribute
import io.github.dockyardmc.utils.Disposable

class PlayerAttributes(val player: Player): Disposable {

    private val bindablePool = BindablePool()
    private val attributeMap: MutableMap<Attribute, AttributeInstance> = mutableMapOf()

    operator fun get(attribute: Attribute): AttributeInstance {
        if(!attributeMap.containsKey(attribute)) {
            attributeMap[attribute] = AttributeInstance(player, attribute, bindablePool)
        }
        return attributeMap[attribute]!!
    }

    init {
        setDefault(Attributes.ATTACK_DAMAGE, 1.0)
        setDefault(Attributes.MOVEMENT_SPEED, 0.10000000149011612)
        setDefault(Attributes.BLOCK_INTERACTION_RANGE, 4.5)
        setDefault(Attributes.ENTITY_INTERACTION_RANGE, 3.0)
    }

    private fun setDefault(attribute: Attribute, default: Double) {
        val bindable = get(attribute).base
        bindable.value = default
        bindable.defaultValue = default
    }

    override fun dispose() {
        bindablePool.dispose()
        attributeMap.clear()
    }
}