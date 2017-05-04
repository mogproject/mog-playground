package com.mogproject.mogami.playground.view.renderer

import com.mogproject.mogami.{BoardType, _}
import com.mogproject.mogami.playground.controller.{Configuration, Cursor}
import com.mogproject.mogami.playground.view.{Layout, TextRenderer}
import com.mogproject.mogami.playground.view.parts.board.MainBoard
import org.scalajs.dom
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
trait BoardRenderer {

  def layout: Layout

  private[this] var mainBoard = MainBoard(layout)

  private[this] var mainBoards = Seq(mainBoard)

  def boardRendererElement: Div = mainBoard.output

  def initializeBoardRenderer(): Unit = mainBoard.initialize()

  //
  // Actions
  //
  def drawAsImage(): Unit = {
    dom.window.document.body.style.backgroundColor = "black"

    val t = "Snapshot - Shogi Playground"
    val base64 = mainBoard.getImageBase64
    val elem = a(attr("download") := "snapshot.png", title := t, href := base64, img(alt := t, src := base64))
    dom.window.document.body.innerHTML = elem.toString
  }

  def drawBoard(): Unit = mainBoards.foreach(_.drawBoard())

  def drawIndexes(config: Configuration): Unit = mainBoards.foreach(_.drawIndexes(config))

  def drawIndicators(config: Configuration, turn: Player, status: GameStatus): Unit = mainBoards.foreach(_.drawIndicators(config, turn, status))

  def drawPlayerIcon(config: Configuration): Unit = mainBoards.foreach(_.drawPlayerIcon(config))

  def clearPlayerIcon(): Unit = mainBoards.foreach(_.clearPlayerIcon())

  def drawPlayerNames(config: Configuration, blackName: String, whiteName: String): Unit = mainBoards.foreach(_.drawPlayerNames(config, blackName, whiteName))

  def clearPlayerNames(): Unit = mainBoards.foreach(_.clearPlayerNames())

  def drawPieces(config: Configuration, state: State): Unit = mainBoards.foreach(_.drawPieces(config, state))

  def drawIllegalStatePieces(config: Configuration, state: State, move: Move): Unit = mainBoards.foreach(_.drawIllegalStatePieces(config, state, move))

  def clearPieces(): Unit = mainBoards.foreach(_.clearPieces())

  // for Edit Mode
  def expandCanvas(): Unit = mainBoards.foreach(_.expandCanvas())

  def contractCanvas(): Unit = mainBoards.foreach(_.contractCanvas())

  def drawEditingPieces(config: Configuration, board: BoardType, hand: HandType, box: Map[Ptype, Int]): Unit = mainBoards.foreach(_.drawEditingPieces(config, board, hand, box))

  def clearPiecesInBox(): Unit = mainBoards.foreach(_.clearPiecesInBox())

  def drawPieceBox(): Unit = mainBoards.foreach(_.drawPieceBox())

  def hidePieceBox(): Unit = mainBoards.foreach(_.hidePieceBox())

  // cursor
  def flashCursor(cursor: Cursor): Unit = mainBoards.foreach(_.flashCursor(cursor))

  def drawLastMove(config: Configuration, move: Option[Move]): Unit = mainBoards.foreach(_.drawLastMove(config, move))

  def clearLastMove(): Unit = mainBoards.foreach(_.clearLastMove())

  def clearActiveCursor(): Unit = mainBoards.foreach(_.clearActiveCursor())

  def drawSelectedArea(cursor: Cursor): Unit = mainBoards.foreach(_.drawSelectedArea(cursor))

  def clearSelectedArea(): Unit = mainBoards.foreach(_.clearSelectedArea())

  // effects
  def startMoveForwardEffect(): Unit = mainBoards.foreach(_.startMoveForwardEffect())

  def startMoveBackwardEffect(): Unit = mainBoards.foreach(_.startMoveBackwardEffect())

}
