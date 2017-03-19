package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami.Game
import com.mogproject.mogami.playground.controller.{Configuration, Cursor}
import com.mogproject.mogami.playground.view.Renderer

/**
  * View mode
  */
case class ViewModeController(renderer: Renderer,
                              config: Configuration,
                              game: Game,
                              displayPosition: Int
                             ) extends GameController {
  val mode: Mode = Viewing

  override def copy(config: Configuration, game: Game, displayPosition: Int): GameController =
    ViewModeController(renderer, config, game, displayPosition)

  override def canActivate(cursor: Cursor): Boolean = cursor.isPlayer

  override def canInvokeWithoutSelection(cursor: Cursor): Boolean = true

  override def invokeCursor(selected: Cursor, invoked: Cursor): Option[ModeController] = {
    if (invoked.isBoard) {
      setControl(2) // move next
    } else if (invoked.isPlayer) {
      renderer.showGameInfoModal(config, game.gameInfo)
      None
    } else {
      None
    }
  }
}
