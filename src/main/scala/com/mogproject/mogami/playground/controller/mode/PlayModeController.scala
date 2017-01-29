package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami.core.MoveBuilderSfen
import com.mogproject.mogami.core.State.PromotionFlag
import com.mogproject.mogami.Game
import com.mogproject.mogami.playground.controller.{Configuration, Cursor, Language}
import com.mogproject.mogami.playground.view.Renderer

/**
  * Play mode
  */
case class PlayModeController(override val renderer: Renderer,
                              override val config: Configuration,
                              game: Game
                             ) extends ModeController(Playing, renderer, config) {

  override def canActivate(cursor: Cursor): Boolean = !cursor.isBox

  override def canSelect(cursor: Cursor): Boolean = cursor match {
    case Cursor(Some(sq), None, None) => game.currentState.board.get(sq).exists(game.currentState.turn == _.owner)
    case Cursor(None, Some(h), None) => h.owner == game.currentState.turn && game.currentState.hand.get(h).exists(_ > 0)
    case _ => false
  }

  override def initialize(): Unit = {
    super.initialize()
    renderAll()
  }

  override def terminate(): Unit = {
    super.terminate()
  }

  override def renderAll(): Unit = {
    super.renderAll()

    // draw status
    renderer.drawPieces(config.pieceRenderer, game.currentState)
    renderer.drawIndicators(game.currentState.turn, game.status)
    renderer.drawLastMove(game.lastMove)

    // record
    renderer.updateRecordContent(game, config.lang)

    // update control bar
    renderer.updateControlBar(game.moves.nonEmpty, game.moves.nonEmpty, forwardEnabled = false, stepForwardEnabled = false)

    // render URLs
    renderUrls(game)
  }

  /**
    * Move action in Play Mode
    *
    * @param selected from
    * @param invoked  to
    */
  override def invokeCursor(selected: Cursor, invoked: Cursor): Option[ModeController] = {
    val from = selected.moveFrom

    invoked match {
      case Cursor(Some(to), None, None) if game.currentState.canAttack(from, to) =>
        val nextGame: Option[Game] = game.currentState.getPromotionFlag(from, to) match {
          case Some(PromotionFlag.CannotPromote) => game.makeMove(MoveBuilderSfen(from, to, promote = false))
          case Some(PromotionFlag.CanPromote) =>
            val tempMv = MoveBuilderSfen(from, to, promote = false).toMove(game.currentState)
            renderer.drawLastMove(tempMv)
            game.makeMove(MoveBuilderSfen(from, to, renderer.askPromote(config.lang)))
          case Some(PromotionFlag.MustPromote) => game.makeMove(MoveBuilderSfen(from, to, promote = true))
          case None => None
        }
        nextGame.map(g => this.copy(game = g))
      case _ => None
    }
  }

  //
  // Actions
  //
  override def setMode(nextMode: Mode): Option[ModeController] = nextMode match {
    case Playing => None
    case Viewing => Some(ViewModeController(renderer, config, game, -1))
    case Editing if game.moves.isEmpty || renderer.askConfirm(config.lang) =>
      val st = game.currentState
      Some(EditModeController(renderer, config, st.turn, st.board, st.hand, st.getUnusedPtypeCount))
  }

  override def setLanguage(lang: Language): Option[ModeController] = Some(this.copy(config = config.copy(lang = lang)))

  override def setControl(controlType: Int): Option[ModeController] = {
    controlType match {
      case 0 if renderer.askConfirm(config.lang) => Some(this.copy(game = Game(game.initialState)))
      case 0 => None
      case 1 =>
        val mv = math.min(renderer.getSelectedIndex, game.moves.length) - 1
        Some(this.copy(game = game.copy(moves = game.moves.take(mv), givenHistory = Some(game.history.take(mv + 1)))))
      case _ =>
        throw new IllegalArgumentException(s"Unexpected control: mode=${mode} controlType=${controlType}")
    }
  }

  /**
    * Shift to View Mode
    *
    * @param index position
    * @return
    */
  override def setRecord(index: Int): Option[ModeController] = Some(ViewModeController(renderer, config, game, index))

}
