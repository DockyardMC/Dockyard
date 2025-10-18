package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableDispatcher
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.events.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType

class Interaction(location: Location) : Entity(location) {

    override var type: EntityType = EntityTypes.INTERACTION
    override val health: Bindable<Float> = bindablePool.provideBindable(0f)
    override var inventorySize: Int = 0

    private val eventPool = EventPool(Events, "Interaction Listeners")

    val width: Bindable<Float> = bindablePool.provideBindable(1f)
    val height: Bindable<Float> = bindablePool.provideBindable(1f)
    val responsive: Bindable<Boolean> = bindablePool.provideBindable(true)

    val rightClickDispatcher: BindableDispatcher<Player> = bindablePool.provideBindableDispatcher()
    val leftClickDispatcher: BindableDispatcher<Player> = bindablePool.provideBindableDispatcher()
    val middleClickDispatcher: BindableDispatcher<Player> = bindablePool.provideBindableDispatcher()
    val generalInteractionDispatcher: BindableDispatcher<Player> = bindablePool.provideBindableDispatcher()

    init {
        eventPool.on<PlayerInteractWithEntityEvent> { event ->
            if (event.entity != this) return@on
            rightClickDispatcher.dispatch(event.player)
            generalInteractionDispatcher.dispatch(event.player)
        }

        eventPool.on<PlayerDamageEntityEvent> { event ->
            if (event.entity != this) return@on
            leftClickDispatcher.dispatch(event.player)
            generalInteractionDispatcher.dispatch(event.player)
        }

        eventPool.on<PlayerPickItemFromEntityEvent> { event ->
            if (event.entity != this) return@on
            middleClickDispatcher.dispatch(event.player)
            generalInteractionDispatcher.dispatch(event.player)
        }

        width.valueChanged { event ->
            metadata[Metadata.Interaction.WIDTH] = event.newValue
        }

        height.valueChanged { event ->
            metadata[Metadata.Interaction.HEIGHT] = event.newValue
        }

        responsive.valueChanged { event ->
            metadata[Metadata.Interaction.RESPONSIVE] = event.newValue
        }
    }

    override fun dispose() {
        eventPool.dispose()
        super.dispose()
    }
}