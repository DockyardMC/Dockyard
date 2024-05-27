
# 🏗️ DockyardMC 🚢

DockyardMC open-source, fast and lightweight Minecraft server software that's written from scratch in Kotlin without any code from Mojang. It is focused on making development easy, unlike PaperMC which still uses some really old bukkit APIs, Dockyard has very easy to use and modern API


⚠️ _**This project is currently undear heavy development and it is not even nearly production ready**_


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

#### Periodical Events

Run code periodically
```kotlin
Period.on<HourPeriod> {
    DockyardServer.broadcastMessage("<aqua>Reminder: <yellow>Stay hydrated and stretch once in a while!")
}
```

_there will be more later_


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