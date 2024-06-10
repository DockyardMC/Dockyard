package io.github.dockyardmc.player

import io.github.dockyardmc.scroll.Component
import java.util.UUID

class PlayerInfoUpdate(
    val uuid: UUID,
    val action: PlayerInfoUpdateAction
)

class AddPlayerInfoUpdateAction(val profileProperty: ProfilePropertyMap): PlayerInfoUpdateAction()

//TODO too hard, not doing now lol
@Deprecated("look im never gonna add this, dont use this. Its here just cause all others are there")
class InitializeChatInfoUpdateAction(): PlayerInfoUpdateAction()

class UpdateGamemodeInfoUpdateAction(val gameMode: GameMode): PlayerInfoUpdateAction()

class UpdateListedInfoUpdateAction(val listed: Boolean): PlayerInfoUpdateAction()

class UpdateLatencyInfoUpdateAction(val latency: Int): PlayerInfoUpdateAction()

class UpdateDisplayNameInfoUpdateAction(val hasDisplayName: Boolean, val displayName: Component): PlayerInfoUpdateAction()

open class PlayerInfoUpdateAction()