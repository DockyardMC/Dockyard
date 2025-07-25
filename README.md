[<img src="https://github.com/user-attachments/assets/5c488d9e-30b0-4025-9b32-b3e474fc4eb1">](https://github.com/DockyardMC/Dockyard)

---
[![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmvn.devos.one%2Freleases%2Fio%2Fgithub%2Fdockyardmc%2Fdockyard%2Fmaven-metadata.xml&style=for-the-badge&logo=maven&logoColor=%23FFFFFF&label=Latest%20Version&color=%23afff87)](https://mvn.devos.one/#/releases/io/github/dockyardmc/dockyard)
[![Mc Version](https://img.shields.io/badge/Minecraft_Version-1.21.6-Minecraft?style=for-the-badge&color=%23ff9969)](https://kotlinlang.org/)
[![Language](https://img.shields.io/badge/Language-Kotlin-Kotlin?style=for-the-badge&color=%23ff6969)](https://kotlinlang.org/)

[![wakatime](https://wakatime.com/badge/github/DockyardMC/Dockyard.svg?style=for-the-badge)](https://wakatime.com/badge/github/DockyardMC/Dockyard)
[![Discord](https://img.shields.io/discord/1242845647892123650?label=Discord%20Server&color=%237289DA&style=for-the-badge&logo=discord&logoColor=%23FFFFFF)](https://discord.gg/SA9nmfMkdc)
[![Static Badge](https://img.shields.io/badge/Donate-Ko--Fi-pink?style=for-the-badge&logo=ko-fi&logoColor=%23FFFFFF&color=%23ff70c8)](https://ko-fi.com/LukynkaCZE)

DockyardMC is an open-source, fast and lightweight Minecraft server protocol implementation that's written from scratch in Kotlin without any code from Mojang. It is focused on making developing and prototyping easy, simple and intuitive while having full control over every aspect of the server.

**This project currently under development, missing parts some users might rely on**

## Quick Start

You can read how to set up and use dockyard [here](https://dockyard.lukynka.cloud/wiki/quick-start)

## Features

- Modern, simple to use API which makes developing and prototyping easy and fast
- Lightweight and without all the overhead the vanilla server has
- Ability to take full control over every aspect of the server
- Fully multithreaded worlds
- Built-in [Spark](https://github.com/lucko/spark) profiler

## API Examples

### Events

```kotlin
Events.on<PlayerJoinEvent> { event ->
    if(banList.contains(event.player.uuid)) {
        event.player.kick("<red>You are banned!")
        event.cancel()
        return@on
    }
  
    broadcastMessage("<lime>→ <yellow>${event.player} has joined the server.")
}
```

Modifying events (including PacketReceived and PacketSent)
```kotlin
Events.on<PacketReceivedEvent> { event ->
    if(event.packet is ServerboundPlayerChatMessagePacket) {
      event.packet.message = "ha get overwritten >:3"
    }
}
```
---

### Commands API
You can create commands quickly and easily with the DockyardMC command API

```kotlin
Commands.add("/explode") {
    addArgument("player", PlayerArgument())
    withPermission("player.admin")
    withDescription("explodes a player")
    execute { context ->
        val executingPlayer = context.getPlayerOrThrow()
        val player = getArgument<Player>("player")
    
        player.spawnParticle(player.location, Particles.EXPLOSION_EMITTER, Vector3f(1f), amount = 5)
        player.playSound(Sounds.ENTITY_GENERIC_EXPLODE, volume = 2f, pitch = randomFloat(0.6f, 1.3f))
    
        player.sendMessage("<yellow>You got <b>totally exploded <yellow>by <red>$executingPlayer")
        executingPlayer.sendMessage("<yellow>You <b>totally exploded <yellow>player <red>$player")
    }
}
```

---

### Built-in APIs for bossbar, sidebar, dialogs, advancements and others

#### Sidebar API
```kotlin
val sidebar = sidebar {
    withTitle("<yellow><bold>My Cool Server")
    withSpacer()
    withPlayerLine { player -> "Welcome, <aqua>$player" }
    withPlayerLine { player -> "World: <yellow>${player.world.name}" }
    withPlayerLine { player -> "Ping: <pink>${player.ping}" }
    withSpacer()
    withStaticLine("<yellow>www.mycoolserver.uwu")
}

Events.on<PlayerJoinEvent> { event ->
    sidebar.addViewer(event.player)
}
```
Changing any lines, title etc. will automatically send update to the viewers

#### Bossbar API
```kotlin
val bossbar = Bossbar("<yellow>The server has uptime is: <orange>$serverUptime<yellow>!", 1f, BossbarColor.YELLOW, BossbarNotches.SIX)

Events.on<PlayerJoinEvent> { event ->
    bossbar.addViewer(event.player)
}
```
Again, changing any properties of the bossbar will automatically send updates to the viewers 


#### Dialog API

```kotlin
val poll = createNoticeDialog("dockyard:poll") {
    title = "Important poll !!!!"
    canCloseWithEsc = false

    addBooleanInput("plays_league") {
        label = "Do you play league of legends?"
    }

    addTextInput("reason") {
        label = "If so, why?"
    }

    withButton("Submit") {
        // No need to manually listen on `DialogCustomClickActionEvent`
        onClick { event ->
            val playsLeague = event.payload.getBoolean("plays_league")
            val reason = event.payload.getString("reason")

            if (playsLeague) {
                event.player.kick("Stinky league player")
                broadcastMessage("<red>${event.player} plays league and tried to justify it with reason: $reason!!!1 !")
            }
        }
    }
}
player.openDialog(dialog)
```
The dialog object gets automatically added to the registry

#### Advancement API

```kotlin
// here's how to make an advancement
val root = advancement("dockyard/root") {
    withTitle("<blue>Dockyard")
    withDescription("On github!!!\n<gold>DockyardMC/Dockyard")
    withIcon(Items.LAPIS_LAZULI)
    withBackground(Blocks.CHERRY_LEAVES)
    withPosition(0f, 0f)
}

advancement("dockyard/child") {
    // set the parent of the advancement like this
    withParent(root)

    withTitle("Child")
    withIcon(Items.STICK.toItemStack()
        .withComponent(EnchantmentGlintOverrideItemComponent(true)))

    withPosition(1f, 0f)
}

Events.on<PlayerJoinEvent> { event ->
    root.addAll(event.player)
}
```

Changing any properties will automatically send updates to the viewers

You can also send an advancement toast like this:
```kotlin
player.showToast("Hello there", Items.FEATHER.toItemStack())
```

---

### Entity Metadata Layers

Layering entity metadata per player allows for client-side changes to entities for purposes like client-side glowing and client-side invisibility. 

Note that this behaviour is not just sending one packet, but its whole system that overlays the player specific metadata layer over the entity's actual metadata   

Here are few examples:
```kotlin
// pre-made functions to set client-side glowing and invisibility
entity.setGlowingFor(player, true)
entity.setInvisibleFor(player, false)
```

```kotlin
// get the metadata layer of player or create new one if it doesn't exist
val playerMetadataLayer = warden.metadataLayers[player] ?: mutableMapOf<EntityMetadataType, EntityMetadata>()

// create new EntityMetadata with index and type pose
val pose = EntityMetadata(EntityMetadataType.POSE, EntityMetaValue.POSE, EntityPose.ROARING)

// add the pose to the list
playerMetadataLayer[EntityMetadataType.POSE] = pose
warden.metadataLayers[player] = playerMetadataLayer

// specified player will now see the warden roaring
```

## Running

Dockyard is mainly designed as library that can be imported via maven. If you want to run dockyard you will need to embed it into your own kotlin app.

## Contributing

Contributions are always welcome! Please always check branches to see if the feature you are contributing is not already existing feature that someone else is working on

(plus you get cool fancy orange contributor role on the discord!!!)

## Related Libraries / Projects

- **[Scroll](https://github.com/DockyardMC/Scroll/)** - Minecraft component library made for DockyardMC
- **[adventure-nbt](https://github.com/KyoriPowered/adventure/tree/main/4/nbt)** - Minecraft NBT library
- **[kotlin-bindables](https://github.com/LukynkaCZE/kotlin-bindables)** - Bindable system inspired by [osu!framework](https://github.com/ppy/osu-framework/)
- **[Pathetic](https://github.com/Metaphoriker/pathetic)** - A powerful, optimized and easy-to-use Java A* Pathfinding Library for 3D environments.
- **[Spark](https://github.com/lucko/spark)** - A performance profiler for Minecraft clients, servers, and proxies
- **[PrettyLog](https://github.com/LukynkaCZE/PrettyLog/)** - Fancy logging library

## Authors

- [LukynkaCZE](https://www.github.com/LukynkaCZE)
- [p1k0chu](https://github.com/p1k0chu)
- [AsoDesu](https://www.github.com/AsoDesu)

## Additional thanks to

- [KevDev](https://github.com/TrasherMC)
- [BluSpring](https://github.com/BluSpring)
- [Asoji](https://github.com/asoji)
- [MattW](https://github.com/mworzala) / [Minestom](https://github.com/Minestom/Minestom)
- All the contributors
- Twitch chat who watches me code this! <3

---

If you want to support me and this project, consider [**buying me a coffee**](https://ko-fi.com/lukynkacze) <3
