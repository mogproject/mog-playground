package com.mogproject.mogami.playground.view

import com.mogproject.mogami.playground.controller.Cursor
import com.mogproject.mogami.{Hand, Piece, Player, Ptype, Square, State}
import com.mogproject.mogami.playground.view.piece.PieceRenderer
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, Element, MouseEvent}
import org.scalajs.dom.html.{Canvas, Div}

import scalatags.JsDom.all._

/**
  * controls canvas rendering
  */
case class Renderer(elem: Element, layout: Layout, pieceRenderer: PieceRenderer) {

  private[this] val header: Div = div(cls := "row",
    position := "relative",

    p(textAlign := "center", "Shogi Playground")
  ).render

  private[this] val canvas0: Canvas = createCanvas(0)
  private[this] val canvas1: Canvas = createCanvas(1)
  private[this] val canvas2: Canvas = createCanvas(2)
  private[this] val canvas3: Canvas = createCanvas(3)

  private[this] val layer0: CanvasRenderingContext2D = canvas0.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  private[this] val layer1: CanvasRenderingContext2D = canvas1.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  private[this] val layer2: CanvasRenderingContext2D = canvas2.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  private[this] val layer3: CanvasRenderingContext2D = canvas3.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  private[this] val canvasDiv: Div = div(cls := "row",
    position := "relative",
    height := layout.canvasHeight,

    div(cls := "col-md-12",
      canvas0, canvas1, canvas2, canvas3
    )
  ).render

  private[this] val snapshotInput = createInput()
  private[this] val recordInput = createInput()

  private[this] def createInput() = input(
    tpe := "text", cls := "form-control", aria.label := "...", readonly := "readonly"
  ).render

  private[this] def createInputGroup(labelString: String, inputElem: Element) = div(cls := "row",
    position := "relative",

    div(cls := "col-md-1"),
    div(cls := "col-md-10",
      label(labelString),
      div(cls := "input-group",
        inputElem,
        span(
          cls := "input-group-btn",
          button(cls := "btn btn-default", tpe := "button", "Copy!")
        )
      ),
      div(cls := "col-md-1")
    )
  ).render

  private[this] val footer: Div = div(
    createInputGroup("Snapshot URL", snapshotInput),
    createInputGroup("Record URL", recordInput)
  ).render

  initialize()

  private[this] def initialize(): Unit = {
    elem.appendChild(header)
    elem.appendChild(canvasDiv)
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

  def setEventListener(eventType: String, f: MouseEvent => Unit): Unit = canvasDiv.addEventListener(eventType, f, useCapture = false)

  def drawBoard(): Unit = {
    layout.board.draw(layer1)
    layout.handWhite.draw(layer1)
    layout.handBlack.draw(layer1)

    for (i <- 1 to 8) {
      val x = layout.board.left + layout.PIECE_WIDTH * i
      val y = layout.board.top + layout.PIECE_HEIGHT * i

      Line(x, layout.board.top, x, layout.board.bottom).draw(layer1)
      Line(layout.board.left, y, layout.board.right, y).draw(layer1)

      if (i % 3 == 0) {
        Circle(x, layout.board.top + layout.PIECE_HEIGHT * 3, 3).draw(layer1)
        Circle(x, layout.board.top + layout.PIECE_HEIGHT * 6, 3).draw(layer1)
      }
    }
  }

  def drawPieces(state: State): Unit = {
    clearPieces()
    state.board.foreach { case (sq, pc) => pieceRenderer.drawOnBoard(layer2, pc, sq) }
    state.hand.foreach { case (pc, n) => pieceRenderer.drawInHand(layer2, pc, n) }
  }

  def clearPieces(): Unit = {
    layout.board.clear(layer2)
    layout.handWhite.clear(layer2)
    layout.handBlack.clear(layer2)
  }

  def drawTurn(turn: Player): Unit = {
    (if (turn.isBlack) layout.indicatorWhite else layout.indicatorBlack).clear(layer2)
    (if (turn.isBlack) layout.indicatorBlack else layout.indicatorWhite).drawFill(layer2, layout.color.active)
  }

  def askPromote(): Boolean = {
    dom.window.confirm("Do you want to promote?")
  }

  def setDebug(text: String): Unit = {
    footer.innerHTML += br.toString + text
  }

  /**
    * Convert MouseEvent to Cursor
    *
    * @param evt mouse event
    * @return Cursor if the mouse position is inside the specific area
    */
  def getCursor(evt: MouseEvent): Option[Cursor] = {
    val rect = canvas2.getBoundingClientRect()
    val (x, y) = (evt.clientX - rect.left, evt.clientY - rect.top)

    (layout.board.isInside(x, y), layout.handBlack.isInside(x, y), layout.handWhite.isInside(x, y)) match {
      case (true, _, _) =>
        val file = 9 - ((x - layout.board.left) / layout.PIECE_WIDTH).toInt
        val rank = 1 + ((y - layout.board.top) / layout.PIECE_HEIGHT).toInt
        Some(Cursor(Left(Square(file, rank))))
      case (false, false, false) =>
        None
      case (false, isBlack, _) =>
        val offset = isBlack.fold(x - layout.handBlack.left, layout.handWhite.right - x)
        val i = (offset / layout.HAND_UNIT_WIDTH).toInt
        (i <= 6 && offset % layout.HAND_UNIT_WIDTH <= layout.PIECE_WIDTH).option {
          Cursor(Right(Hand(Piece(isBlack.fold(Player.BLACK, Player.WHITE), Ptype.inHand(i)))))
        }
    }
  }

  /**
    * Convert Cursor object to Rectangle.
    */
  private[this] def cursorToRect(cursor: Cursor): Rectangle = {
    val (x, y) = cursor match {
      case Cursor(Right(Hand(Player.BLACK, pt))) =>
        (layout.handBlack.left + (pt.sortId - 1) * layout.HAND_UNIT_WIDTH, layout.handBlack.top)
      case Cursor(Right(Hand(Player.WHITE, pt))) =>
        (layout.handWhite.right - (pt.sortId - 1) * layout.HAND_UNIT_WIDTH - layout.PIECE_WIDTH, layout.handWhite.top)
      case Cursor(Left(sq)) =>
        (layout.board.left + (9 - sq.file) * layout.PIECE_WIDTH, layout.board.top + (sq.rank - 1) * layout.PIECE_HEIGHT)
    }
    Rectangle(x, y, layout.PIECE_WIDTH, layout.PIECE_HEIGHT)
  }

  /**
    * Draw a highlighted cursor.
    */
  def drawCursor(cursor: Cursor): Unit = {
    cursorToRect(cursor).draw(layer3, layout.color.cursor, -2)
  }

  /**
    * Clear a cursor.
    */
  def clearCursor(cursor: Cursor): Unit = {
    cursorToRect(cursor).clear(layer3)
  }

  /**
    * Draw the selected area.
    */
  def drawSelectedArea(cursor: Cursor): Unit = cursorToRect(cursor).drawFill(layer0, layout.color.cursor, 2)

  /**
    * Draw the last move area.
    */
  def drawLastMoveArea(cs: Seq[Cursor]): Unit = cs.foreach(cursorToRect(_).drawFill(layer0, layout.color.light, 1))

  def clearLastMoveArea(cs: Seq[Cursor]): Unit = cs.foreach(cursorToRect(_).clear(layer0))

  /**
    * Clear a selected area.
    */
  def clearSelectedArea(cursor: Cursor): Unit = cursorToRect(cursor).clear(layer0)

  def updateSnapshotUrl(url: String): Unit = snapshotInput.value = url

  def updateRecordUrl(url: String): Unit = recordInput.value = url
}
