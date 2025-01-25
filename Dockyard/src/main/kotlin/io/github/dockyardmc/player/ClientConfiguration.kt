package io.github.dockyardmc.player

class ClientConfiguration(
    var locale: String,
    var viewDistance: Int,
    var chatMode: Int,
    var chatColors: Boolean,
    var displayedSkinParts: Byte,
    var mainHandSide: PlayerHand,
    var enableTextFiltering: Boolean,
    var allowServerListing: Boolean,
    var particleSettings: ClientParticleSettings
)