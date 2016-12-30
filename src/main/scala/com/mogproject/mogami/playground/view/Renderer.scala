package com.mogproject.mogami.playground.view

import com.mogproject.mogami.{Player, State}
import com.mogproject.mogami.playground.view.piece.PieceRenderer
import org.scalajs.dom.{CanvasRenderingContext2D, Element}
import org.scalajs.dom.html._

import scalatags.JsDom.all._

/**
  * controls canvas rendering
  */
case class Renderer(elem: Element, layout: Layout, pieceRenderer: PieceRenderer) {

  private[this] val header: Div = div(
    position := "relative",
    p(textAlign := "center", "Shogi Playground")
  ).render

  private[this] val footer: Div = div(
    position := "relative"
  ).render

  private[this] val canvas0: Canvas = createCanvas(0)
  private[this] val canvas1: Canvas = createCanvas(1)
  private[this] val canvas2: Canvas = createCanvas(2)
  private[this] val canvas3: Canvas = createCanvas(3)

  private[this] val layer0: CanvasRenderingContext2D = canvas0.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  private[this] val layer1: CanvasRenderingContext2D = canvas1.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  private[this] val layer2: CanvasRenderingContext2D = canvas2.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  private[this] val layer3: CanvasRenderingContext2D = canvas3.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  initialize()

  private[this] def initialize(): Unit = {
    elem.appendChild(header)
    elem.appendChild(div(position := "relative", canvas0, canvas1, canvas2, canvas3).render)
    elem.appendChild(footer)
  }

  private[this] def createCanvas(zIndexVal: Int): Canvas = {
    canvas(
      widthA := layout.canvasWidth,
      heightA := layout.canvasHeight,
      position := "absolute",
      marginLeft := "auto",
      marginRight := "auto",
      left := 0,
      right := 0,
      top := 0,
      zIndex := zIndexVal
    ).render
  }

  def drawBoard(): Unit = {
    layout.board.draw(layer0)
    layout.handWhite.draw(layer0)
    layout.handBlack.draw(layer0)

    for (i <- 1 to 8) {
      val x = layout.board.left + layout.PIECE_WIDTH * i
      val y = layout.board.top + layout.PIECE_HEIGHT * i

      Line(x, layout.board.top, x, layout.board.bottom).draw(layer0)
      Line(layout.board.left, y, layout.board.right, y).draw(layer0)

      if (i % 3 == 0) {
        Circle(x, layout.board.top + layout.PIECE_HEIGHT * 3, 3).draw(layer2)
        Circle(x, layout.board.top + layout.PIECE_HEIGHT * 6, 3).draw(layer2)
      }
    }
  }

  def drawPieces(state: State): Unit = {
    drawTurn(state.turn)
    state.board.foreach { case (sq, pc) => pieceRenderer.drawOnBoard(layer3, pc, sq) }
    state.hand.foreach { case (pc, n) => pieceRenderer.drawInHand(layer3, pc, n)}
  }

  def drawTurn(turn: Player): Unit = {
    (if (turn.isBlack) layout.indicatorWhite else layout.indicatorBlack).clear(layer2)
    (if (turn.isBlack) layout.indicatorBlack else layout.indicatorWhite).drawFill(layer2, layout.color.active)
  }
}
