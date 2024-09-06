package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import io.github.dockyardmc.entities.ai.AIGoal
import io.github.dockyardmc.entities.ai.AIManager
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.utils.MathUtils

class TestZombie(location: Location): Entity(location) {
    override var type: EntityType = EntityTypes.ZOMBIE
    override var health: Bindable<Float> = Bindable(20f)
    override var inventorySize: Int = 0

    var target: Entity? = null

    val brain = AIManager(this)

    init {
        brain.addGoal(ZombieLookAroundAIGoal(this, 1))
        brain.addGoal(ZombieGroanAiGoal(this, 1))
    }
}

class ZombieLookAroundAIGoal(override var entity: Entity, override var priority: Int): AIGoal() {

    val zombie = entity as TestZombie
    val chancePerTick = 50

    var lookingAroundTime: Int = 0

    override fun startCondition(): Boolean {
        return MathUtils.randomInt(chancePerTick, 100) == chancePerTick
                && zombie.target == null
    }

    override fun start() {
        lookingAroundTime = 10
        zombie.teleport(zombie.location.clone().apply { pitch = 0f; yaw += MathUtils.randomFloat(-90f, 90f) })
    }

    override fun end() {
        cooldown = MathUtils.randomInt(3, 5) * 20
    }

    override fun endCondition(): Boolean = lookingAroundTime <= 0

    override fun tick() {
        lookingAroundTime--
    }
}

class ZombieGroanAiGoal(override var entity: Entity, override var priority: Int): AIGoal() {

    val zombie = entity as TestZombie
    val chancePerTick = 50

    override fun startCondition(): Boolean {
        return MathUtils.randomInt(chancePerTick, 100) == chancePerTick
                && zombie.target == null
    }

    override fun start() {
        zombie.playSoundToViewers(Sound("minecraft:entity.zombie.ambient", pitch = MathUtils.randomFloat(0.8f, 1.2f)))
    }

    override fun end() {
        cooldown = MathUtils.randomInt(1, 2) * 20
    }

    override fun endCondition(): Boolean = true

    override fun tick() {}
}