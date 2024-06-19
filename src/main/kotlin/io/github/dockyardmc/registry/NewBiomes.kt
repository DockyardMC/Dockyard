package io.github.dockyardmc.registry

object NewBiomes {

    var map = mutableMapOf<String, NewBiome>(
        "minecraft:badlands" to NewBiome(
            0.0f,
            Effects(
                fog_color = 12638463,
                foliage_color = 10387789,
                grass_color = 9470285,
                mood_sound = MoodSound(block_search_extent = 8, offset = 2.0, sound = "minecraft:ambient.cave", tick_delay = 6000),
                music = Music(max_delay = 24000, min_delay = 12000, replace_current_music = false, sound = "minecraft:music.overworld.badlands"),
                sky_color = 7254527,
                water_color = 4159204,
                water_fog_color = 329011
            ),
            has_precipitation = false,
            temperature = 2.0f
        ),


    )
}


data class MoodSound(
    val block_search_extent: Int,
    val offset: Double,
    val sound: String,
    val tick_delay: Int
)

data class Music(
    val max_delay: Int,
    val min_delay: Int,
    val replace_current_music: Boolean,
    val sound: String
)

data class Effects(
    val fog_color: Int,
    val foliage_color: Int,
    val grass_color: Int,
    val mood_sound: MoodSound,
    val music: Music,
    val sky_color: Int,
    val water_color: Int,
    val water_fog_color: Int
)

data class NewBiome(
    val downfall: Float,
    val effects: Effects,
    val has_precipitation: Boolean,
    val temperature: Float
)