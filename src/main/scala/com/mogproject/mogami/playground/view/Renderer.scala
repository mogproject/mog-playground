package com.mogproject.mogami.playground.view

import com.mogproject.mogami.playground.controller._
import com.mogproject.mogami._
import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.playground.view.piece.PieceRenderer
import com.mogproject.mogami.playground.api.Clipboard
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, Element, MouseEvent, TouchEvent}
import org.scalajs.dom.html.{Canvas, Div}

import scala.scalajs.js
import scalatags.JsDom.all._

/**
  * controls canvas rendering
  */
case class Renderer(elem: Element, layout: Layout, pieceRenderer: PieceRenderer) {
  // main canvas
  private[this] val canvas0: Canvas = createCanvas(0)
  private[this] val canvas1: Canvas = createCanvas(1)
  private[this] val canvas2: Canvas = createCanvas(2)
  private[this] val canvas3: Canvas = createCanvas(3)

  private[this] val layer0: CanvasRenderingContext2D = canvas0.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  private[this] val layer1: CanvasRenderingContext2D = canvas1.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  private[this] val layer2: CanvasRenderingContext2D = canvas2.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  private[this] val layer3: CanvasRenderingContext2D = canvas3.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  private[this] val canvasContainer: Div = div(cls := "col-md-6",
    padding := 0,
    height := layout.canvasHeight,
    canvas0,
    canvas1,
    canvas2,
    canvas3
  ).render

  // forms
  private[this] val recordSelector = select(cls := "form-control thin-select",
    option("-"),
    option("+7776FU"),
    option("-3334FU")
  ).render

  private[this] val modeLabel = a(href := "#", cls := "dropdown-toggle", data.toggle := "dropdown", role := "button", aria.haspopup := true, aria.expanded := false,
    "Play",
    span(cls := "caret")
  ).render

  private[this] val langLabel = a(href := "#", cls := "dropdown-toggle", data.toggle := "dropdown", role := "button", aria.haspopup := true, aria.expanded := false,
    "JP",
    span(cls := "caret")
  ).render

  private[this] val navigator = tag("nav")(cls := "navbar navbar-default navbar-fixed-top",
    div(cls := "container",
      div(cls := "row")(
        div(cls := "navbar-header col-md-10 col-md-offset-1",
          ul(cls := "nav navbar-nav",
            li(cls := "dropdown",
              widthA := "60px",
              modeLabel,
              ul(cls := "dropdown-menu",
                li(cls := "dropdown-header", "Mode"),
                li(a(href := "#", "Play", onclick := (() => Controller.setMode(Playing)))),
                li(a(href := "#", "View", onclick := (() => Controller.setMode(Viewing)))),
                li(a(href := "#", "Edit", onclick := (() => Controller.setMode(Editing))))
              )
            ),
            li(cls := "navbar-form",
              div(cls := "form-group",
                recordSelector
              )
            ),
            li(cls := "dropdown pull-right",
              textAlign := "right",
              langLabel,
              ul(cls := "dropdown-menu",
                li(cls := "dropdown-header", "Language"),
                li(a(href := "#", "Japanese", onclick := (() => Controller.setLanauge(Japanese)))),
                li(a(href := "#", "English", onclick := (() => Controller.setLanauge(English))))
              )
            )
          )
        )
      )
    )
  )

  private[this] val snapshotInput = createInput("snapshot")
  private[this] val recordInput = createInput("record")

  private[this] def createInput(ident: String) = input(
    tpe := "text", id := ident, cls := "form-control", aria.label := "...", readonly := "readonly"
  ).render

  private[this] def createInputGroup(labelString: String, inputElem: Element, target: String) = div(cls := "row",
    position := "relative",

    div(cls := "col-md-1"),
    div(cls := "col-md-10",
      label(labelString),
      div(cls := "input-group",
        inputElem,
        span(
          cls := "input-group-btn",
          button(cls := "btn btn-default", data("clipboard-target") := s"#${target}", tpe := "button", "Copy!")
        )
      ),
      div(cls := "col-md-1")
    )
  ).render

  private[this] val footer: Div = div(
    createInputGroup("Snapshot URL", snapshotInput, "snapshot"),
    createInputGroup("Record URL", recordInput, "record")
  ).render

  initialize()

  private[this] def initialize(): Unit = {
    elem.appendChild(div(cls := "container",
      div(cls := "row navbar",
        div(cls := "col-md-12", navigator)
      ),
      div(cls := "row",
        canvasContainer,
        div(cls := "col-md-6", footer)
      )
    ).render)

    // initialize clipboard.js
    val cp = new Clipboard(".btn")

    // todo: show tooptip @see http://stackoverflow.com/questions/37381640/tooltips-highlight-animation-with-clipboard-js-click/37395225
  }

  private[this] def createCanvas(zIndexVal: Int): Canvas = {
    canvas(
      widthA := layout.canvasWidth,
      heightA := layout.canvasHeight,
      marginLeft := "auto",
      marginRight := "auto",
      left := 0,
      right := 0,
      top := 0,
      zIndex := zIndexVal
    ).render
  }

  def hasTouchEvent: Boolean = dom.window.hasOwnProperty("ontouchstart")

  def setEventListener[A](eventType: String, f: A => Unit): Unit = canvasContainer.addEventListener(eventType, f, useCapture = false)

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

  def drawIndicators(game: Game): Unit = {
    game.status match {
      case GameStatus.Playing =>
        (if (game.turn.isBlack) layout.indicatorWhite else layout.indicatorBlack).clear(layer2)
        (if (game.turn.isBlack) layout.indicatorBlack else layout.indicatorWhite).drawFill(layer2, layout.color.active)
      case GameStatus.Mated =>
        (if (game.turn.isBlack) layout.indicatorWhite else layout.indicatorBlack).drawFill(layer2, layout.color.win)
        (if (game.turn.isBlack) layout.indicatorBlack else layout.indicatorWhite).drawFill(layer2, layout.color.lose)
      case GameStatus.Illegal =>
        (if (game.turn.isBlack) layout.indicatorWhite else layout.indicatorBlack).drawFill(layer2, layout.color.lose)
        (if (game.turn.isBlack) layout.indicatorBlack else layout.indicatorWhite).drawFill(layer2, layout.color.win)
      case GameStatus.Drawn =>
        layout.indicatorWhite.drawFill(layer2, layout.color.draw)
        layout.indicatorBlack.drawFill(layer2, layout.color.draw)
    }
  }

  def askPromote(): Boolean = {
    dom.window.confirm("Do you want to promote?")
  }

  /**
    * Convert MouseEvent to Cursor
    *
    * @return Cursor if the mouse position is inside the specific area
    */
  def getCursor(clientX: Double, clientY: Double): Option[Cursor] = {
    val rect = canvas2.getBoundingClientRect()
    val (x, y) = (clientX - rect.left, clientY - rect.top)

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

  def setMode(mode: Mode): Unit = {
    modeLabel.innerHTML = mode.label + span(cls := "caret").toString()
  }

  def setLang(lang: Language): Unit = {
    langLabel.innerHTML = lang.label + span(cls := "caret").toString()
  }
}
