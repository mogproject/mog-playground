package com.mogproject.mogami.playground.view.renderer

import com.mogproject.mogami.{BoardType, _}
import com.mogproject.mogami.playground.controller.{Configuration, Cursor}
import com.mogproject.mogami.playground.view.parts.board.MainBoard
import com.mogproject.mogami.playground.view.renderer.BoardRenderer.{DoubleBoard, FlipEnabled}
import com.mogproject.mogami.playground.view.renderer.piece.PieceRenderer
import org.scalajs.dom
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
trait BoardRenderer {

  private[this] var mainBoards: Seq[MainBoard] = Seq.empty

  lazy val boardRendererElement: Div = div().render

  def getPieceRenderer: PieceRenderer = mainBoards.head.pieceRenderer

  /**
    * Initialize or reload main boards
    *
    * @param config configuration
    */
  def initializeBoardRenderer(config: Configuration): Unit = {
    mainBoards = if (config.flip == DoubleBoard) {
      Seq(false, true).map(f => MainBoard(config.canvasWidth, f, config.pieceLang, config.recordLang))
    } else {
      Seq(MainBoard(config.canvasWidth, config.flip == FlipEnabled, config.pieceLang, config.recordLang))
    }

    boardRendererElement.innerHTML = ""
    boardRendererElement.appendChild(mainBoards.head.canvasContainer)
    mainBoards.foreach(_.initialize())
  }

  //
  // Actions
  //
  def drawAsImage(): Unit = {
    dom.window.document.body.style.backgroundColor = "black"

    val t = "Snapshot - Shogi Playground"
    val base64 = mainBoards.head.getImageBase64
    val elem = a(attr("download") := "snapshot.png", title := t, href := base64, img(alt := t, src := base64))
    dom.window.document.body.innerHTML = elem.toString
  }

  def drawBoard(): Unit = mainBoards.foreach(_.drawBoard())

  def drawIndexes(): Unit = mainBoards.foreach(_.drawIndexes())

  def drawIndicators(turn: Player, status: GameStatus): Unit = mainBoards.foreach(_.drawIndicators(turn, status))

  def drawPlayerIcon(): Unit = mainBoards.foreach(_.drawPlayerIcon())

  def clearPlayerIcon(): Unit = mainBoards.foreach(_.clearPlayerIcon())

  def drawPlayerNames(blackName: String, whiteName: String): Unit = mainBoards.foreach(_.drawPlayerNames(blackName, whiteName))

  def clearPlayerNames(): Unit = mainBoards.foreach(_.clearPlayerNames())

  def drawPieces(state: State): Unit = mainBoards.foreach(_.drawPieces(state))

  def drawIllegalStatePieces(state: State, move: Move): Unit = mainBoards.foreach(_.drawIllegalStatePieces(state, move))

  def clearPieces(): Unit = mainBoards.foreach(_.clearPieces())

  // for Edit Mode
  def expandCanvas(): Unit = mainBoards.foreach(_.expandCanvas())

  def contractCanvas(): Unit = mainBoards.foreach(_.contractCanvas())

  def drawEditingPieces(board: BoardType, hand: HandType, box: Map[Ptype, Int]): Unit = mainBoards.foreach(_.drawEditingPieces(board, hand, box))

  def clearPiecesInBox(): Unit = mainBoards.foreach(_.clearPiecesInBox())

  def drawPieceBox(): Unit = mainBoards.foreach(_.drawPieceBox())

  def hidePieceBox(): Unit = mainBoards.foreach(_.hidePieceBox())

  // cursor
  def flashCursor(cursor: Cursor): Unit = mainBoards.foreach(_.flashCursor(cursor))

  def drawLastMove(move: Option[Move]): Unit = mainBoards.foreach(_.drawLastMove(move))

  def clearLastMove(): Unit = mainBoards.foreach(_.clearLastMove())

  def clearActiveCursor(): Unit = mainBoards.foreach(_.clearActiveCursor())

  def drawSelectedArea(cursor: Cursor): Unit = mainBoards.foreach(_.drawSelectedArea(cursor))

  def clearSelectedArea(): Unit = mainBoards.foreach(_.clearSelectedArea())

  // effects
  def startMoveForwardEffect(): Unit = mainBoards.foreach(_.startMoveForwardEffect())

  def startMoveBackwardEffect(): Unit = mainBoards.foreach(_.startMoveBackwardEffect())
}

object BoardRenderer {

  sealed trait FlipType {
    def unary_! : FlipType = this match {
      case FlipEnabled => FlipDisabled
      case FlipDisabled => FlipEnabled
      case DoubleBoard => DoubleBoard
    }
  }

  case object FlipDisabled extends FlipType

  case object FlipEnabled extends FlipType

  case object DoubleBoard extends FlipType

}