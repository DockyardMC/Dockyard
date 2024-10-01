[![wakatime](https://wakatime.com/badge/github/DockyardMC/Dockyard.svg)](https://wakatime.com/badge/github/DockyardMC/Dockyard)
[![Discord](https://img.shields.io/discord/1242845647892123650?label=Discord%20Server&color=%237289DA)](https://discord.gg/SA9nmfMkdc)

# 🌊 DockyardMC 🚢

DockyardMC open-source, fast and lightweight Minecraft server protocol implementation that's written from scratch in Kotlin without any code from Mojang. It is focused on making development easy, unlike PaperMC which still uses some really old bukkit APIs, Dockyard has very easy to use and modern API

> [!WARNING]  
> This project is currently under heavy development, and it is NOT production ready.

## Quick Start

You can read how to setup and use dockyard here: https://dockyard.lukynka.cloud/wiki/quick-start

## Features

- Easy to use, modern and extensible API
- Ability to take full control over every aspect of the server
- Lightweight
- uhhh there will be more stuff here later

## API Examples

#### Events

```kotlin
Events.on<PlayerConnectEvent> {
    DockyardServer.broadcastMessage("<lime>→ <yellow>${it.player} has joined the server.")
}
```

Modifying events (including PacketReceived and PacketSent)
```kotlin
Events.on<PacketReceivedEvent> {
    if(it.packet is ServerboundPlayerChatMessagePacket) {
        it.packet.message = "ha get overwritten >:3"
    }
}
```
Canceling Events

```kotlin
Events.on<PlayerMoveEvent> {
    // No moving for aso >:3
    if(it.player.username == "AsoDesu_") {
        it.cancelled = true
    }
}
```
---

#### Commands API
You can create commands quickly and easily with the DockyardMC command API

```kotlin
Commands.add("/explode") {
    addArgument("player", PlayerArgument())
    withPermission("player.admin")
    withDescription("executes stuff")
    execute {
        val executingPlayer = it.getPlayerOrThrow()
        val player = getArgument<Player>("player")
    
        player.spawnParticle(player.location, Particles.EXPLOSION_EMITTER, Vector3f(1f), amount = 5)
        player.playSound("minecraft:entity.generic.explode", volume = 2f, pitch = MathUtils.randomFloat(0.6f, 1.3f))
    
        player.sendMessage("<yellow>You got <rainbow><b>totally exploded <yellow>by <red>$executingPlayer")
        executingPlayer.sendMessage("<yellow>You <rainbow><b>totally exploded <yellow>player <red>$player")
    }
}
```

---

#### Periodical Events

Run code periodically
```kotlin
Period.on<HourPeriod> {
    DockyardServer.broadcastMessage("<aqua>Reminder: <yellow>Stay hydrated and stretch once in a while!")
}
```

---

_there will be more later_

---

## Run Locally

- Clone the repository `git clone https://github.com/DockyardMC/Dockyard/`
- Go to the project directory `cd Dockyard`
- Open in IntelliJ and run task `Dockyard Server`

## Contributing

Contributions are always welcome! Please always check branches to see if the feature you are contributing is not already existing feature that someone else is working on

## Related Libraries / Projects

- **Scroll**, Minecraft component library made for DockyardMC
    - https://github.com/DockyardMC/Scroll/

- **PrettyLog**, fancy logging library
    - https://github.com/LukynkaCZE/PrettyLog/
## Authors

- [@LukynkaCZE](https://www.github.com/LukynkaCZE)
- [@AsoDesu](https://www.github.com/AsoDesu)

## Additional thanks to

- [@KevDev](https://github.com/TrasherMC)
- [@BluSpring](https://github.com/BluSpring)
- [@Asoji](https://github.com/asoji)
- All the contributors
- Twitch chat who watches me code this! <3

If you want to support me and this project, consider [buying me a coffee](https://ko-fi.com/lukynkacze)
