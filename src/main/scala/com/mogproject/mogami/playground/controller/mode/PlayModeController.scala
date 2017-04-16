package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami._
import com.mogproject.mogami.core.State.PromotionFlag
import com.mogproject.mogami.core.move.Resign
import com.mogproject.mogami.{Game, Square}
import com.mogproject.mogami.playground.controller.{Configuration, Controller, Cursor}
import com.mogproject.mogami.playground.view.Renderer
import com.mogproject.mogami.util.Implicits._

/**
  * Play mode
  */
case class PlayModeController(renderer: Renderer,
                              config: Configuration,
                              game: Game,
                              displayPosition: Int
                             ) extends GameController with CursorAdjustable {
  val mode: Mode = Playing

  override def copy(config: Configuration, game: Game, displayPosition: Int): GameController =
    PlayModeController(renderer, config, game, displayPosition)

  /**
    * Initialization
    */
  override def initialize(): Unit = {
    super.initialize()
    renderer.showActionSection()
  }

  override def terminate(): Unit = {
    super.terminate()
    renderer.hideActionSection()
  }

  override def renderAll(): Unit = {
    super.renderAll()

    renderer.updateActionSection(config.messageLang, game.finalAction.isEmpty || displayPosition <= game.moves.length)
  }

  override def canActivate(cursor: Cursor): Boolean = !cursor.isBox

  override def canSelect(cursor: Cursor): Boolean = config.flip.when[Cursor](!_)(cursor) match {
    case Cursor(Some(sq), None, None, None) => selectedState.board.get(sq).exists(selectedState.turn == _.owner)
    case Cursor(None, Some(h), None, None) => h.owner == selectedState.turn && selectedState.hand.get(h).exists(_ > 0)
    case _ => false
  }

  /**
    * Move action in Play Mode
    *
    * @param selected from
    * @param invoked  to
    */
  override def invokeCursor(selected: Cursor, invoked: Cursor): Option[ModeController] = {
    if (invoked.isPlayer) {
      renderer.showGameInfoModal(config, game.gameInfo)
      None
    } else {
      val from = config.flip.when[Cursor](!_)(selected).moveFrom

      def f(to: Square, promote: Boolean) = {
        getTruncatedGame.makeMove(MoveBuilderSfen(from, to, promote)).map(g => this.copy(game = g, displayPosition = displayPosition + 1))
      }

      config.flip.when[Cursor](!_)(invoked) match {
        case Cursor(Some(to), None, None, None) if selectedState.canAttack(from, to) =>
          selectedState.getPromotionFlag(from, to) match {
            case Some(PromotionFlag.CannotPromote) => f(to, promote = false)
            case Some(PromotionFlag.CanPromote) =>
              for {
                s <- from.left.toOption
                p <- selectedState.board.get(s)
              } yield {
                renderer.askPromote(config, p,
                  () => Controller.update(f(to, promote = false)),
                  () => Controller.update(f(to, promote = true))
                )
              }
              None
            case Some(PromotionFlag.MustPromote) => f(to, promote = true)
            case None => None
          }
        case _ => None
      }
    }
  }

  def processMouseUp(selected: Cursor, released: Cursor): Option[Cursor] = {
    (selected.isBoard, selected.isHand, released.isBoard) match {
      case (true, false, true) =>
        for {
          from <- selected.toSquare(config.flip)
          to <- released.toSquare(config.flip)
          p <- selectedState.board.get(from)
          sq <- adjustMovement(p, from, to)
        } yield Cursor(sq, config.flip)
      case (false, true, true) => Some(released) // no adjustment for hand pieces
      case _ => None
    }
  }

  // Action Section
  def setResign(): Option[ModeController] = {
    Some(this.copy(
      game = getTruncatedGame.copy(finalAction = Some(Resign())),
      displayPosition = displayPosition + 1))
  }

}
