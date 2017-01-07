package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.core.MoveBuilderSfen
import com.mogproject.mogami.core.State.PromotionFlag
import com.mogproject.mogami.playground.view.piece.SimpleJapanesePieceRenderer
import com.mogproject.mogami.playground.view.{Layout, Renderer}
import com.mogproject.mogami.{Game, Hand, Piece, Square, State}
import org.scalajs.dom.{Element, MouseEvent}

import scala.annotation.tailrec
import scala.scalajs.js.URIUtils.{decodeURIComponent, encodeURIComponent}


/**
  * logic controller
  *
  * @param elem parent HTML element
  * @param args Arguments instance
  */
case class Controller(elem: Element, args: Arguments, baseUrl: String) {

  // variables
  private[this] var game: Game = Game()
  private[this] var rendererVal: Option[Renderer] = None
  private[this] var activeCursor: Option[Cursor] = None
  private[this] var selectedCursor: Option[Cursor] = None

  private[this] def renderer = rendererVal.get

  /**
    * Initialize the game and renderer
    */
  def initialize(): Unit = {
    // create game
    args.sfen.foreach { s =>
      val g = Game.parseSfenString(s)
      if (g.isDefined)
        game = g.get
      else
        println(s"Invalid parameter: sfen=${s}")
    }

    // create renderer
    val layout = Layout(args.canvasWidth, args.canvasHeight)
    val pieceRenderer = args.lang match {
      case Some("en") => SimpleJapanesePieceRenderer(layout) // todo
      case Some("ja") => SimpleJapanesePieceRenderer(layout)
      case _ => SimpleJapanesePieceRenderer(layout)
    }
    rendererVal = Some(Renderer(elem, layout, pieceRenderer))

    // draw board and pieces
    renderer.drawBoard()
    renderer.drawPieces(game.currentState)
    renderer.drawTurn(game.currentState.turn)
    updateUrls()

    if (game.moves.isEmpty) {
      // Play mode
    } else {
      // View mode
    }

    // register mouse event handlers
    renderer.setEventListener("mousemove", mouseMove)
    renderer.setEventListener("mousedown", mouseDown)

  }

  private[this] def updateUrls(): Unit = {
    // todo: add lang setting
    renderer.updateSnapshotUrl(baseUrl + "?sfen=" + encodeURIComponent(Game(game.currentState).toSfenString))
    renderer.updateRecordUrl(baseUrl + "?sfen=" + encodeURIComponent(game.toSfenString))
  }

  def mouseMove(evt: MouseEvent): Unit = {
    val ret = renderer.getCursor(evt)

    if (ret != activeCursor) {
      activeCursor.foreach(renderer.clearCursor)
      ret.foreach(c => if (!c.isHand || game.currentState.hand.get(c.moveFrom.right.get).exists(_ > 0)) renderer.drawCursor(c))
      activeCursor = ret
    }
  }

  def mouseDown(evt: MouseEvent): Unit = {
    val ret = renderer.getCursor(evt)

    if (selectedCursor.isDefined) {
      // move
      val selected = selectedCursor.get
      renderer.clearSelectedArea(selected)
      selectedCursor = None

      (selected, ret) match {
        case (Cursor(from), Some(Cursor(Left(to)))) if game.currentState.canAttack(from, to) =>
          val nextGame: Option[Game] = game.currentState.getPromotionFlag(from, to) match {
            case Some(PromotionFlag.CannotPromote) => game.makeMove(MoveBuilderSfen(from, to, promote=false))
            case Some(PromotionFlag.CanPromote) => game.makeMove(MoveBuilderSfen(from, to, renderer.askPromote()))
            case Some(PromotionFlag.MustPromote) => game.makeMove(MoveBuilderSfen(from, to, promote=true))
            case None => None
          }
          nextGame.foreach { g =>
            renderer.drawPieces(g.currentState)
            renderer.drawTurn(g.currentState.turn)
            updateUrls()
            game = g
          }
        case _ => // do nothing
      }
    } else {
      // select
      ret.foreach { cursor =>
        val canSelect = cursor match {
          case Cursor(Left(sq)) => game.currentState.board.get(sq).exists(game.currentState.turn == _.owner)
          case Cursor(Right(h)) => h.owner == game.currentState.turn && game.currentState.hand.get(h).exists(_ > 0)
        }
        if (canSelect) {
          selectedCursor = ret
          renderer.drawSelectedArea(cursor)
        }
      }
    }
  }

}