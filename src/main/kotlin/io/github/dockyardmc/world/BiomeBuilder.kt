package io.github.dockyardmc.world

import io.github.dockyardmc.extentions.fromRGBInt
import io.github.dockyardmc.extentions.fromRGBIntOrNull
import io.github.dockyardmc.extentions.getPackedInt
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.registries.*
import io.github.dockyardmc.scroll.CustomColor

fun customBiome(identifier: String, builder: BiomeBuilder.() -> Unit): Biome {
    val instance = BiomeBuilder(identifier)
    builder.invoke(instance)
    return instance.toBiome()
}

class BiomeBuilder(val identifier: String) {
    private val defaultBiome = Biomes.PLAINS

    var rainChance: Float = 1f
    var hasRain: Boolean = true
    var temperature: Float = 1f
    var temperatureModifier: BiomeTemperatureModifier = BiomeTemperatureModifier.NONE
    var fogColor: CustomColor? = CustomColor.fromRGBIntOrNull(defaultBiome.effects.fogColor)
    var foliageColor: CustomColor? = CustomColor.fromRGBIntOrNull(defaultBiome.effects.foliageColor)
    var grassColor: CustomColor? = CustomColor.fromRGBIntOrNull(defaultBiome.effects.grassColor)
    var grassColorModifier: BiomeGrassColorModifier = BiomeGrassColorModifier.NONE
    var moodSound: MoodSound? = defaultBiome.effects.moodSound
    var music: List<Biome.WeightedBackgroundMusic>? = defaultBiome.effects.music
    var ambientAdditions: AmbientAdditions? = defaultBiome.effects.ambientAdditions
    var ambientLoop: String? = defaultBiome.effects.ambientLoop
    var particles: BiomeParticles? = defaultBiome.effects.particle
    var skyColor: CustomColor = CustomColor.fromRGBInt(defaultBiome.effects.skyColor)
    var waterColor: CustomColor = CustomColor.fromRGBInt(defaultBiome.effects.waterColor)
    var waterFogColor: CustomColor = CustomColor.fromRGBInt(defaultBiome.effects.waterFogColor)

    fun toBiome(): Biome {
        return Biome(
            identifier = identifier, downfall = rainChance,
            effects = Effects(
                fogColor = fogColor?.getPackedInt(),
                foliageColor = foliageColor?.getPackedInt(),
                grassColor = grassColor?.getPackedInt(),
                grassColorModifier = grassColorModifier.serializableValue,
                moodSound = moodSound,
                music = music,
                ambientAdditions = ambientAdditions,
                ambientLoop = ambientLoop,
                particle = particles,
                skyColor = skyColor.getPackedInt(),
                waterColor = waterColor.getPackedInt(),
                waterFogColor = waterFogColor.getPackedInt(),
            ),
            hasRain = hasRain,
            temperature = temperature,
            temperatureModifier = temperatureModifier.serializableValue
        )
    }

    fun withParticles(particle: Particle, probability: Float) {
        withParticles(BiomeParticles(ParticleOptions(particle.identifier), probability))
    }

    fun withParticles(particles: BiomeParticles) {
        this.particles = particles
    }

    fun withWaterFogColor(hex: String) {
        withWaterFogColor(CustomColor.fromHex(hex))
    }

    fun withWaterFogColor(color: CustomColor) {
        this.waterFogColor = color
    }

    fun withWaterColor(hex: String) {
        withWaterColor(CustomColor.fromHex(hex))
    }

    fun withWaterColor(color: CustomColor) {
        this.waterColor = color
    }

    fun withSkyColor(hex: String) {
        withSkyColor(CustomColor.fromHex(hex))
    }

    fun withSkyColor(color: CustomColor) {
        this.skyColor = color
    }

    fun withAmbientLoop(sound: String) {
        this.ambientLoop = sound
    }

    fun withAmbientAdditions(sound: String, tickChance: Double) {
        withAmbientAdditions(AmbientAdditions(sound, tickChance))
    }

    fun withAmbientAdditions(ambientAdditions: AmbientAdditions) {
        this.ambientAdditions = ambientAdditions
    }

    fun withMusic(music: List<Biome.WeightedBackgroundMusic>) {
        this.music = music
    }

    fun withMoodSound(sound: String, blockSearchExtent: Int, positionOffset: Double, delay: Int) {
        withMoodSound(MoodSound(blockSearchExtent, positionOffset, sound, delay))
    }

    fun withMoodSound(moodSound: MoodSound) {
        this.moodSound = moodSound
    }

    fun withFogColor(hex: String) {
        withFogColor(CustomColor.fromHex(hex))
    }

    fun withFogColor(color: CustomColor) {
        this.fogColor = color
    }

    fun withFoliageColor(hex: String) {
        withFoliageColor(CustomColor.fromHex(hex))
    }

    fun withFoliageColor(color: CustomColor) {
        this.foliageColor = color
    }

    fun withGrassColor(hex: String) {
        withGrassColor(CustomColor.fromHex(hex))
    }

    fun withGrassColor(color: CustomColor) {
        this.grassColor = color
    }

    fun withGrassColorModifier(modifier: BiomeGrassColorModifier) {
        this.grassColorModifier = modifier
    }

    fun withRainChance(rainChance: Float) {
        this.rainChance = rainChance
    }

    fun hasRain(hasRain: Boolean) {
        this.hasRain = hasRain
    }

    fun withTemperature(temperature: Float) {
        this.temperature = temperature
    }

    fun withTemperatureModifier(modifier: BiomeTemperatureModifier) {
        this.temperatureModifier = modifier
    }
}

enum class BiomeTemperatureModifier(val serializableValue: String?) {
    NONE(null),
    FROZEN("frozen")
}

enum class BiomeGrassColorModifier(val serializableValue: String?) {
    NONE(null),
    DARK_FOREST("dark_forest"),
    SWAMP("swamp")
}