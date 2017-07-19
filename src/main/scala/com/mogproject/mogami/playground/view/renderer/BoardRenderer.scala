package com.mogproject.mogami.playground.view.renderer

import com.mogproject.mogami.{BoardType, _}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.playground.controller.{Configuration, Cursor}
import com.mogproject.mogami.playground.view.bootstrap.Tooltip
import com.mogproject.mogami.playground.view.parts.board.MainBoard
import com.mogproject.mogami.playground.view.renderer.BoardRenderer.{DoubleBoard, FlipEnabled}
import com.mogproject.mogami.playground.view.renderer.piece.PieceRenderer
import com.mogproject.mogami.playground.view.section.{ControlSection, SideBarLeft, SideBarRight}
import org.scalajs.dom
import org.scalajs.dom.html.Div

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
trait BoardRenderer {

  protected def sideBarLeft: SideBarLeft

  private[this] var mainBoards: Seq[MainBoard] = Seq.empty

  protected def controlSection: ControlSection

  def getPieceRenderer: PieceRenderer = mainBoards.head.pieceRenderer

  lazy val mainArea: Div = div().render

  lazy val mainPane: Div = div(
    cls := "main-area-wrapper",
    mainArea
  ).render

  def widenMainPane(): Unit = {
    mainPane.style.width = 100.pct
  }

  def recenterMainPane(): Unit = {
    mainPane.style.width = s"calc(100% - ${sideBarLeft.currentWidth + SideBarRight.currentWidth}px)"
  }

  /**
    * Initialize or reload main boards
    *
    * `controlSection` should be updated.
    *
    * @param config configuration
    */
  def initializeBoardRenderer(config: Configuration): Unit = {
    mainBoards = if (config.flip == DoubleBoard) {
      Seq(false, true).map(f => MainBoard(config.canvasWidth, f, config.pieceLang, config.recordLang))
    } else {
      Seq(MainBoard(config.canvasWidth, config.flip == FlipEnabled, config.pieceLang, config.recordLang))
    }

    val node = (config.isMobile, config.isLandscape, config.flip == DoubleBoard) match {
      case (true, true, isDoubleBoard) => createMobileLandscapeMain(config.canvasWidth, isDoubleBoard.fold(2, 1))
      case (true, false, _) => createMobilePortraitMain(config.canvasWidth)
      case (_, _, isDoubleBoard) => createPCPortraitMain(config.canvasWidth, isDoubleBoard.fold(2, 1))
    }
    mainArea.innerHTML = ""
    mainArea.appendChild(div(cls := "container-fluid", node).render)

    mainBoards.foreach(_.initialize())
  }

  private[this] def createPCPortraitMain(canvasWidth: Int, numBoards: Int): TypedTag[Div] = div(cls := "main-area main-area-pc",
    width := (canvasWidth + 70) * numBoards - 50, // +20 for 1 board, +90 for 2 boards
    if (numBoards == 2) {
      div(cls := "row",
        div(cls := "col-xs-6", mainBoards.head.canvasContainer), div(cls := "col-xs-6", mainBoards(1).canvasContainer)
      )
    } else {
      div(
        mainBoards.head.canvasContainer
      )
    },
    div(
      controlSection.outputControlBar,
      controlSection.outputComment
    )
  )

  private[this] def createMobilePortraitMain(canvasWidth: Int): TypedTag[Div] = div(cls := "main-area main-area-mobile-portrait",
    width := canvasWidth,
    mainBoards.head.canvasContainer,
    div(
      controlSection.outputControlBar,
      controlSection.outputComment
    )
  )

  private[this] def createMobileLandscapeMain(canvasWidth: Int, numBoards: Int): TypedTag[Div] = div(cls := "main-area",
    width := canvasWidth * 2 + 60,
    div(cls := "row",
      div(cls := "col-xs-6", mainBoards.head.canvasContainer),
      div(cls := "col-xs-6", (numBoards == 2).fold(mainBoards(1).canvasContainer,  controlSection.outputComment))
    ),
    div(cls := "row", controlSection.outputControlBar)
  )

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

  def startMoveAction(move: Option[Move]): Unit = mainBoards.foreach(_.startMoveAnimation(move))

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