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
    case Cursor(Some(board), None, None, None) => invokeCursorHelper(board)
    case Cursor(_, _, _, Some(_)) => renderer.showGameInfoModal(config, game.gameInfo); None
    case _ => None
  }

  override def invokeHoldEvent(invoked: Cursor): Option[ModeController] = invoked match {
    case Cursor(Some(board), None, None, None) => invokeCursorHelper(board)
    case _ => None
  }

  private[this] def invokeCursorHelper(board: Square): Option[ModeController] = {
    if (board.file <= 5) {
      // move next
      if (displayPosition < renderer.getMaxRecordIndex) {
        renderer.startMoveForwardEffect()
        setControl(2)
      } else {
        None
      }
    } else {
      // move backward
      if (0 < renderer.getRecordIndex(displayPosition)) {
        renderer.startMoveBackwardEffect()
        setControl(1)
      } else {
        None
      }
    }
  }
}
