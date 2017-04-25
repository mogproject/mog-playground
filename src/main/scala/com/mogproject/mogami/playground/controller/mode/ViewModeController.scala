package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami._
import com.mogproject.mogami.core.game.Game.BranchNo
import com.mogproject.mogami.playground.controller.{Configuration, Cursor}
import com.mogproject.mogami.playground.view.Renderer

/**
  * View mode
  */
case class ViewModeController(renderer: Renderer,
                              config: Configuration,
                              game: Game,
                              displayBranchNo: BranchNo,
                              displayPosition: Int
                             ) extends GameController {
  val mode: Mode = Viewing

  /**
    * Initialization
    */
  override def initialize(): Unit = {
    super.initialize()
    renderer.setCommentReadOnly()
  }

  override def terminate(): Unit = {
    super.terminate()
    renderer.resetCommentReadOnly()
  }

  override def copy(config: Configuration,
                    game: Game,
                    displayBranchNo: BranchNo,
                    displayPosition: Int): GameController =
    ViewModeController(renderer, config, game, displayBranchNo, displayPosition)

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

  override def invokeHoldEvent(invoked: Cursor): Option[ModeController] = {
    if (invoked.isBoard) {
      setControl(2) // move next
    } else {
      None
    }
  }
}
