package io.github.dockyardmc.player

import java.util.UUID

class PlayerInfoUpdate(
    val uuid: UUID,
    val action: PlayerInfoUpdateAction
)

interface PlayerInfoUpdateAction {
    val bitMask: Int
}

class AddPlayerInfoUpdateAction(

    val profileProperty: ProfilePropertyMap,
    override val bitMask: Int = 0x01

): PlayerInfoUpdateAction

class UpdateGamemodeInfoUpdateAction(

    val gameMode: GameMode,
    override val bitMask: Int = 0x04

): PlayerInfoUpdateAction

class SetListedInfoUpdateAction(

    val listed: Boolean,
    override val bitMask: Int = 0x08

): PlayerInfoUpdateAction

class UpdateLatencyInfoUpdateAction(

    val ping: Int,
    override val bitMask: Int = 0x10

): PlayerInfoUpdateAction

class SetDisplayNameInfoUpdateAction(

    val hasDisplayName: Boolean,
    val displayName: String?,
    override val bitMask: Int = 0x20

): PlayerInfoUpdateAction


//class UpdateGamemodeInfoUpdateAction(val gameMode: GameMode): PlayerInfoUpdateAction()
//
//class UpdateListedInfoUpdateAction(val listed: Boolean): PlayerInfoUpdateAction()
//
//class UpdateLatencyInfoUpdateAction(val latency: Int): PlayerInfoUpdateAction()
//
//class UpdateDisplayNameInfoUpdateAction(val hasDisplayName: Boolean, val displayName: Component): PlayerInfoUpdateAction()
//
