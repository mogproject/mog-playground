package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
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
  }

  override def terminate(): Unit = {
    super.terminate()
  }

  override def copy(config: Configuration,
                    game: Game,
                    displayBranchNo: BranchNo,
                    displayPosition: Int): GameController =
    ViewModeController(renderer, config, game, displayBranchNo, displayPosition)

  override def canActivate(cursor: Cursor): Boolean = cursor.isPlayer

  override def canInvokeWithoutSelection(cursor: Cursor): Boolean = true

  override def invokeCursor(selected: Cursor, invoked: Cursor): Option[ModeController] = invoked match {
    case Cursor(Some(board), None, None, None) =>
      // move next or backward
      setControl((board.file <= 5).fold(2, 1))
    case Cursor(_, _, _, Some(_)) =>
      // player
      renderer.showGameInfoModal(config, game.gameInfo)
      None
    case _ => None
  }

  override def invokeHoldEvent(invoked: Cursor): Option[ModeController] = {
    if (invoked.isBoard) {
      setControl(2) // move next
    } else {
      None
    }
  }
}
