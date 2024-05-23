package io.github.dockyardmc.player

import io.github.dockyardmc.scroll.Component
import java.util.UUID

class PlayerInfoUpdate(
    val uuid: UUID,
    val action: PlayerInfoUpdateAction
)

class AddPlayerInfoUpdateAction(val profileProperty: PlayerUpdateProfileProperty): PlayerInfoUpdateAction()

//TODO too hard, not doing now lol
class InitializeChatInfoUpdateAction(): PlayerInfoUpdateAction()

class UpdateGamemodeInfoUpdateAction(val gameMode: GameMode): PlayerInfoUpdateAction()

class UpdateListedInfoUpdateAction(val listed: Boolean): PlayerInfoUpdateAction()

class UpdateLatencyInfoUpdateAction(val latency: Int): PlayerInfoUpdateAction()

class UpdateDisplayNameInfoUpdateAction(val hasDisplayName: Boolean, val displayName: Component): PlayerInfoUpdateAction()

open class PlayerInfoUpdateAction()