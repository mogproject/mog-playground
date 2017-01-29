package com.mogproject.mogami.playground.view

import com.mogproject.mogami.playground.controller.{Cursor, _}
import com.mogproject.mogami._
import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.core.Game.GameStatus.GameStatus
import com.mogproject.mogami.playground.view.piece.PieceRenderer
import com.mogproject.mogami.playground.api.Clipboard
import com.mogproject.mogami.playground.controller.mode.{Editing, Mode, Playing, Viewing}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, Element, MouseEvent, TouchEvent}
import org.scalajs.dom.html.{Canvas, Div}
import org.scalajs.dom.raw.HTMLSelectElement

import scalatags.JsDom.all._

/**
  * controls canvas rendering
  */
case class Renderer(elem: Element, layout: Layout) extends CursorManageable {

  // main canvas
  protected val canvas0: Canvas = createCanvas(0)
  protected val canvas1: Canvas = createCanvas(1)
  protected val canvas2: Canvas = createCanvas(2)
  protected val canvas3: Canvas = createCanvas(3)

  protected val layer0: CanvasRenderingContext2D = canvas0.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer1: CanvasRenderingContext2D = canvas1.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer2: CanvasRenderingContext2D = canvas2.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer3: CanvasRenderingContext2D = canvas3.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

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

  object EditTurn {
    private[this] val anchors: Map[Player, Element] = Map(
      BLACK -> a(cls := "btn btn-primary active", onclick := { () => Controller.setEditTurn(BLACK) }).render,
      WHITE -> a(cls := "btn btn-primary notActive", onclick := { () => Controller.setEditTurn(WHITE) }).render
    )

    val element: Div = div(cls := "form-group",
      label("Turn"),
      div(cls := "row",
        div(cls := "col-sm-8 col-md-8",
          div(cls := "input-group",
            div(id := "radioBtn", cls := "btn-group btn-group-justified",
              anchors(BLACK),
              anchors(WHITE)
            )
          )
        )
      )
    ).render

    def updateEditTurnLabel(lang: Language): Unit = lang match {
      case Japanese =>
        anchors(BLACK).innerHTML = "先手番"
        anchors(WHITE).innerHTML = "後手番"
      case English =>
        anchors(BLACK).innerHTML = "Black"
        anchors(WHITE).innerHTML = "White"
    }

    def updateEditTurnValue(newValue: Player): Unit = {
      anchors(newValue).classList.remove("notActive")
      anchors(newValue).classList.add("active")
      anchors(!newValue).classList.remove("active")
      anchors(!newValue).classList.add("notActive")
    }
  }

  object EditReset {
    private[this] val states = Seq(
      State.HIRATE, State.MATING_BLACK, State.MATING_WHITE,
      State.HANDICAP_LANCE, State.HANDICAP_BISHOP, State.HANDICAP_ROOK, State.HANDICAP_ROOK_LANCE,
      State.HANDICAP_2_PIECE, State.HANDICAP_3_PIECE, State.HANDICAP_4_PIECE, State.HANDICAP_5_PIECE,
      State.HANDICAP_6_PIECE, State.HANDICAP_8_PIECE, State.HANDICAP_10_PIECE,
      State.HANDICAP_THREE_PAWNS, State.HANDICAP_NAKED_KING
    )

    private[this] val labels: Map[Language, Seq[String]] = Map(
      Japanese -> Seq(
        "平手", "詰将棋 (先手)", "詰将棋 (後手)",
        "香落ち", "角落ち", "飛車落ち", "飛香落ち",
        "二枚落ち", "三枚落ち", "四枚落ち", "五枚落ち",
        "六枚落ち", "八枚落ち", "十枚落ち",
        "歩三兵", "裸玉"),
      English -> Seq("Even", "Mating (Black)", "Mating (White)",
        "Lance", "Bishop", "Rook", "Rook-Lance",
        "2-Piece", "3-Piece", "4-Piece", "5-Piece",
        "6-Piece", "8-Piece", "10-Piece",
        "Three Pawns", "Naked King")
    )

    private[this] val buttons = states.map(st => button(cls := "btn btn-default col-sm-3 col-sm-offset-1", onclick := { () => Controller.setEditInitialState(st) }, "").render)

    val element: Div = div(
      label("Reset"),
      div(cls := "row", buttons)
    ).render

    def updateLabel(lang: Language): Unit = buttons.zipWithIndex.foreach { case (b, i) => b.innerHTML = labels(lang)(i) }
  }

  private[this] val editSection = div(display := "none",
    EditTurn.element,
    EditReset.element
  ).render

  private[this] val footer: Div = div(cls := "row",
    div(cls := "col-md-10 col-md-offset-1",
      editSection,
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
      small(p(textAlign := "right", "Shogi Playground © 2017 ", a(href := "http://mogproject.com", "mogproject")))
    ).render)

    // register events
    if (hasTouchEvent) {
      setEventListener("touchstart", touchStart)
    } else {
      setEventListener("mousemove", mouseMove)
      setEventListener("mousedown", mouseDown)
    }

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

  def clearPiecesInBox(): Unit = {
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

  def showEditSection(): Unit = editSection.style.display = "block"

  def hideEditSection(): Unit = editSection.style.display = "none"

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

  def updateSnapshotUrl(url: String): Unit = snapshotInput.value = url

  def updateRecordUrl(url: String): Unit = recordInput.value = url

  def updateMode(mode: Mode): Unit = modeLabel.innerHTML = mode.label + span(cls := "caret").toString()

  def updateLang(lang: Language): Unit = langLabel.innerHTML = lang.label + span(cls := "caret").toString()

  def updateRecordContent(game: Game, lng: Language): Unit = {
    val f: Move => String = lng match {
      case Japanese => _.toKifString
      case English => _.toWesternNotationString
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

    recordSelector.innerHTML = ys.map { case (o, s) => option(o.map(n => s"${n}: ").getOrElse("") + s).toString() }.mkString
    updateRecordIndex(-1)
  }

  def updateRecordIndex(index: Int): Unit = {
    val maxValue = recordSelector.options.length - 1
    recordSelector.selectedIndex = (index < 0).fold(maxValue, math.min(index, maxValue))
  }

  def getMaxRecordIndex: Int = recordSelector.options.length - 1

  def getSelectedIndex: Int = recordSelector.selectedIndex

  def updateControlBar(stepBackwardEnabled: Boolean, backwardEnabled: Boolean, forwardEnabled: Boolean, stepForwardEnabled: Boolean): Unit = {
    controlInput0.disabled = !stepBackwardEnabled
    controlInput1.disabled = !backwardEnabled
    controlInput2.disabled = !forwardEnabled
    controlInput3.disabled = !stepForwardEnabled
  }
}
