package com.mogproject.mogami.playground.view

import com.mogproject.mogami.playground.controller.{Cursor, _}
import com.mogproject.mogami._
import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.core.Game.GameStatus.GameStatus
import com.mogproject.mogami.playground.view.piece.PieceRenderer
import com.mogproject.mogami.playground.api.Clipboard
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, Element}
import org.scalajs.dom.html.{Canvas, Div}
import org.scalajs.dom.raw.HTMLSelectElement

import scalatags.JsDom.all._

/**
  * controls canvas rendering
  */
case class Renderer(elem: Element, layout: Layout) {
  // variables
  private[this] var lastMoveArea: Set[Cursor] = Set.empty
  private[this] var lastCursor: Option[Cursor] = None

  // constants
  private[this] val boxPtypes: Seq[Ptype] = Ptype.KING +: Ptype.inHand

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
  private[this] val recordSelector: HTMLSelectElement = select(
    cls := "form-control thin-select",
    onchange := (() => Controller.setRecord(recordSelector.selectedIndex))
  ).render

  private[this] val modeLabel = a(href := "#", cls := "dropdown-toggle", data.toggle := "dropdown", role := "button", aria.haspopup := true, aria.expanded := false).render

  private[this] val langLabel = a(href := "#", cls := "dropdown-toggle", data.toggle := "dropdown", role := "button", aria.haspopup := true, aria.expanded := false).render

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
                li(a(href := "#", "Japanese", onclick := (() => Controller.setLanguage(Japanese)))),
                li(a(href := "#", "English", onclick := (() => Controller.setLanguage(English))))
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

  private[this] def createInputGroup(labelString: String, inputElem: Element, target: String) = div(
    label(labelString),
    div(cls := "input-group",
      inputElem,
      span(
        cls := "input-group-btn",
        button(cls := "btn btn-default", data("clipboard-target") := s"#${target}", tpe := "button", "Copy!")
      )
    )
  ).render

  private[this] def createControlInput(controlType: Int, glyph: String) = button(cls := "btn btn-default",
    onclick := { () => Controller.setControl(controlType) },
    span(cls := s"glyphicon glyphicon-${glyph}", aria.hidden := true)
  ).render

  private[this] val controlInput0 = createControlInput(0, "step-backward")
  private[this] val controlInput1 = createControlInput(1, "backward")
  private[this] val controlInput2 = createControlInput(2, "forward")
  private[this] val controlInput3 = createControlInput(3, "step-forward")

  private[this] val controlSection = div(
    div(
      label("Control"),
      div(cls := "btn-group btn-group-justified", role := "group", aria.label := "...",
        div(cls := "btn-group", role := "group", controlInput0),
        div(cls := "btn-group", role := "group", controlInput1),
        div(cls := "btn-group", role := "group", controlInput2),
        div(cls := "btn-group", role := "group", controlInput3)
      )
    ),
    br(),
    createInputGroup("Snapshot URL", snapshotInput, "snapshot"),
    br(),
    createInputGroup("Record URL", recordInput, "record")
  ).render

  private[this] val footer: Div = div(cls := "row",
    div(cls := "col-md-10 col-md-offset-1",
      controlSection
    )
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
      ),
      hr(),
      small(p(textAlign := "right", "Shogi Playground © 2017 mogproject"))
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

  def drawIndexes(lang: Language): Unit = {
    val ctx = layer3
    val rankIndex = lang match {
      case Japanese => "一二三四五六七八九"
      case English => "abcdefghi"
    }

    ctx.fillStyle = layout.color.fg

    // clear
    layout.fileIndex.clear(ctx)
    layout.rankIndex.clear(ctx)

    // file
    ctx.font = layout.font.index
    for (i <- 0 until 9) {
      val x = layout.board.left + layout.PIECE_WIDTH * (8 - i) + 10
      val y = layout.board.top - 2
      ctx.fillText("１２３４５６７８９".charAt(i).toString, x, y)
    }

    //rank
    for (i <- 0 until 9) {
      val x = layout.board.right + (lang == Japanese).fold(1, 3)
      val y = layout.board.top + layout.PIECE_HEIGHT * i + 24
      ctx.fillText(rankIndex.charAt(i).toString, x, y)
    }
  }

  def drawPieces(pieceRenderer: PieceRenderer, state: State): Unit = {
    clearPieces()
    state.board.foreach { case (sq, pc) => pieceRenderer.drawOnBoard(layer2, pc, sq) }
    state.hand.foreach { case (pc, n) => pieceRenderer.drawInHand(layer2, pc, n) }
  }

  def clearPieces(): Unit = {
    layout.board.clear(layer2)
    layout.handWhite.clear(layer2)
    layout.handBlack.clear(layer2)
  }

  def drawEditingPieces(pieceRenderer: PieceRenderer, board: BoardType, hand: HandType, box: Map[Ptype, Int]): Unit = {
    clearPieces()
    clearPiecesInBox()
    board.foreach { case (sq, pc) => pieceRenderer.drawOnBoard(layer2, pc, sq) }
    hand.foreach { case (pc, n) => pieceRenderer.drawInHand(layer2, pc, n) }
    box.filter(_._2 > 0).foreach { case (pt, _) => pieceRenderer.drawInBox(layer2, pt) }
  }

  def clearPiecesInBox(): Unit ={
    layout.pieceBox.clear(layer2)
  }

  def drawIndicators(turn: Player, status: GameStatus): Unit = {
    status match {
      case GameStatus.Playing =>
        (if (turn.isBlack) layout.indicatorWhite else layout.indicatorBlack).clear(layer2)
        (if (turn.isBlack) layout.indicatorBlack else layout.indicatorWhite).drawFill(layer2, layout.color.active)
      case GameStatus.Mated =>
        (if (turn.isBlack) layout.indicatorWhite else layout.indicatorBlack).drawFill(layer2, layout.color.win)
        (if (turn.isBlack) layout.indicatorBlack else layout.indicatorWhite).drawFill(layer2, layout.color.lose)
      case GameStatus.PerpetualCheck | GameStatus.Uchifuzume =>
        (if (turn.isBlack) layout.indicatorWhite else layout.indicatorBlack).drawFill(layer2, layout.color.lose)
        (if (turn.isBlack) layout.indicatorBlack else layout.indicatorWhite).drawFill(layer2, layout.color.win)
      case GameStatus.Drawn =>
        layout.indicatorWhite.drawFill(layer2, layout.color.draw)
        layout.indicatorBlack.drawFill(layer2, layout.color.draw)
    }
  }

  def drawPieceBox(): Unit = {
    layout.pieceBox.draw(layer1, layout.color.pieceBox, 3)
  }

  def hidePieceBox(): Unit = {
    layout.pieceBox.clear(layer1, -3)
    clearPiecesInBox()
  }

  def showControlSection(): Unit = controlSection.style.display = "block"

  def hideControlSection(): Unit = controlSection.style.display = "none"

  def askPromote(lang: Language): Boolean = dom.window.confirm(lang match {
    case Japanese => "成りますか?"
    case English => "Do you want to promote?"
  })

  def askConfirm(lang: Language): Boolean = dom.window.confirm(lang match {
    case Japanese => "棋譜の情報が失われますが、よろしいですか?"
    case English => "The record will be discarded. Are you sure?"
  })

  def alertEditedState(msg: String, lang: Language): Unit = dom.window.alert(lang match {
    case Japanese => s"不正な局面です。\n(${msg})"
    case English => s"Invalid state.\n(${msg})"
  })

  /**
    * Convert MouseEvent to Cursor
    *
    * @return Cursor if the mouse position is inside the specific area
    */
  def getCursor(clientX: Double, clientY: Double): Option[Cursor] = {
    val rect = canvas2.getBoundingClientRect()
    val (x, y) = (clientX - rect.left, clientY - rect.top)

    (layout.board.isInside(x, y), layout.handBlack.isInside(x, y), layout.handWhite.isInside(x, y), layout.pieceBox.isInside(x, y)) match {
      case (true, _, _, _) =>
        val file = 9 - ((x - layout.board.left) / layout.PIECE_WIDTH).toInt
        val rank = 1 + ((y - layout.board.top) / layout.PIECE_HEIGHT).toInt
        Some(Cursor(Square(file, rank)))
      case (false, false, false, false) =>
        None
      case (false, false, false, true) =>
        val offset = x - layout.pieceBox.left
        val i = (offset / layout.PIECE_BOX_UNIT_WIDTH).toInt
        (i <= 7 && offset % layout.PIECE_BOX_UNIT_WIDTH <= layout.PIECE_WIDTH).option(Cursor(boxPtypes(i)))
      case (false, isBlack, _, _) =>
        val offset = isBlack.fold(x - layout.handBlack.left, layout.handWhite.right - x)
        val i = (offset / layout.HAND_UNIT_WIDTH).toInt
        (i <= 6 && offset % layout.HAND_UNIT_WIDTH <= layout.PIECE_WIDTH).option {
          Cursor(Piece(isBlack.fold(Player.BLACK, Player.WHITE), Ptype.inHand(i)))
        }
    }
  }

  /**
    * Convert Cursor object to Rectangle.
    */
  private[this] def cursorToRect(cursor: Cursor): Rectangle = {
    val (x, y) = cursor match {
      case Cursor(None, Some(Hand(Player.BLACK, pt)), None) =>
        (layout.handBlack.left + (pt.sortId - 1) * layout.HAND_UNIT_WIDTH, layout.handBlack.top)
      case Cursor(None, Some(Hand(Player.WHITE, pt)), None) =>
        (layout.handWhite.right - (pt.sortId - 1) * layout.HAND_UNIT_WIDTH - layout.PIECE_WIDTH, layout.handWhite.top)
      case Cursor(Some(sq), None, None) =>
        (layout.board.left + (9 - sq.file) * layout.PIECE_WIDTH, layout.board.top + (sq.rank - 1) * layout.PIECE_HEIGHT)
      case Cursor(None, None, Some(pt)) =>
        (layout.pieceBox.left + pt.sortId * layout.PIECE_BOX_UNIT_WIDTH, layout.pieceBox.top)
      case _ => (0, 0) // never happens
    }
    Rectangle(x, y, layout.PIECE_WIDTH, layout.PIECE_HEIGHT)
  }

  /**
    * Draw a highlighted cursor.
    */
  def drawCursor(cursor: Cursor): Unit = {
    clearCursor()
    cursorToRect(cursor).draw(layer3, layout.color.cursor, -2)
    lastCursor = Some(cursor)
  }

  /**
    * Clear a cursor.
    */
  def clearCursor(): Unit = {
    lastCursor.foreach(cursorToRect(_).clear(layer3))
    lastCursor = None
  }

  /**
    * Draw the selected area.
    */
  def drawSelectedArea(cursor: Cursor): Unit = cursorToRect(cursor).drawFill(layer0, layout.color.cursor, 2)

  /**
    * Clear a selected area.
    */
  def clearSelectedArea(cursor: Cursor): Unit = cursorToRect(cursor).clear(layer0)

  /**
    * Draw the last move area.
    */
  def drawLastMove(move: Option[Move]): Unit = {
    val newArea: Set[Cursor] = move match {
      case None => Set.empty
      case Some(mv) =>
        val fr = mv.from match {
          case None => Cursor(mv.player, mv.oldPtype)
          case Some(sq) => Cursor(sq)
        }
        Set(fr, Cursor(mv.to))
    }

    (lastMoveArea -- newArea).foreach(cursorToRect(_).clear(layer0))
    (newArea -- lastMoveArea).foreach(cursorToRect(_).drawFill(layer0, layout.color.light, 1))
    lastMoveArea = newArea
  }

  def clearLastMove(): Unit = drawLastMove(None)

  def updateSnapshotUrl(url: String): Unit = snapshotInput.value = url

  def updateRecordUrl(url: String): Unit = recordInput.value = url

  def setMode(mode: Mode): Unit = modeLabel.innerHTML = mode.label + span(cls := "caret").toString()

  def setLang(lang: Language): Unit = langLabel.innerHTML = lang.label + span(cls := "caret").toString()

  def setRecord(game: Game, lng: Language): Unit = {
    val f: Move => String = lng match {
      case Japanese => _.toKifString
      case English => _.toSfenString
    }

    val xs = game.moves.zipWithIndex.map { case (m, i) =>
      (Some(i + 1), game.history(i).turn.toSymbolString + f(m))
    }
    val additional = (game.status, lng) match {
      case (GameStatus.Mated, Japanese) => List((None, "詰み"))
      case (GameStatus.Drawn, Japanese) => List((None, "千日手"))
      case (GameStatus.PerpetualCheck, Japanese) => List((None, "連続王手の千日手"))
      case (GameStatus.Uchifuzume, Japanese) => List((None, "打ち歩詰め"))
      case (GameStatus.Mated, English) => List((None, "Mated"))
      case (GameStatus.Drawn, English) => List((None, "Drawn"))
      case (GameStatus.PerpetualCheck, English) => List((None, "Perpetual Check"))
      case (GameStatus.Uchifuzume, English) => List((None, "Uchifuzume"))
      case (GameStatus.Playing, _) => List()
    }
    val ys = ((Some(0), "-") +: xs) ++ additional

    val oldIndex = recordSelector.selectedIndex
    recordSelector.innerHTML = ys.map { case (o, s) => option(o.map(n => s"${n}: ").getOrElse("") + s).toString() }.mkString
    selectRecord(oldIndex)
  }

  def selectRecord(index: Int): Unit = {
    val maxValue = recordSelector.options.length - 1
    recordSelector.selectedIndex = (index < 0).fold(maxValue, math.min(index, maxValue))
  }

  def getSelectedIndex: Int = recordSelector.selectedIndex

  def updateControlBar(): Unit = {
    val maxValue = recordSelector.options.length - 1
    val selected = getSelectedIndex

    controlInput0.disabled = selected == 0
    controlInput1.disabled = selected == 0
    controlInput2.disabled = selected == maxValue
    controlInput3.disabled = selected == maxValue
  }
}
