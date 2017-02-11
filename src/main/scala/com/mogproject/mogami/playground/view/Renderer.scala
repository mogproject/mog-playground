package com.mogproject.mogami.playground.view

import com.mogproject.mogami.playground.controller._
import com.mogproject.mogami._
import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.core.Game.GameStatus.GameStatus
import com.mogproject.mogami.playground.view.piece.PieceRenderer
import com.mogproject.mogami.playground.api.Clipboard
import com.mogproject.mogami.playground.api.Clipboard.Event
import com.mogproject.mogami.playground.controller.mode.{Editing, Mode, Playing, Viewing}
import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import com.mogproject.mogami.playground.view.modal._
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, Element}
import org.scalajs.dom.html.{Canvas, Div}
import org.scalajs.dom.raw.HTMLSelectElement
import org.scalajs.jquery.jQuery

import scalatags.JsDom.all._

/**
  * controls canvas rendering
  */
case class Renderer(elem: Element, layout: Layout) extends CursorManageable with TextRenderer {

  // main canvas
  protected val canvas0: Canvas = createCanvas(0)
  protected val canvas1: Canvas = createCanvas(1)
  protected val canvas2: Canvas = createCanvas(2)
  protected val canvas3: Canvas = createCanvas(3)
  protected val canvases: List[Canvas] = List(canvas0, canvas1, canvas2, canvas3)

  protected val layer0: CanvasRenderingContext2D = canvas0.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer1: CanvasRenderingContext2D = canvas1.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer2: CanvasRenderingContext2D = canvas2.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer3: CanvasRenderingContext2D = canvas3.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  private[this] val canvasContainer: Div = div(cls := "col-md-6",
    padding := 0,
    height := layout.canvasHeightCompact,
    canvas0,
    canvas1,
    canvas2,
    canvas3
  ).render

  // forms
  private[this] val recordSelector: HTMLSelectElement = select(
    cls := "form-control rect-select",
    onchange := (() => Controller.setRecord(recordSelector.selectedIndex))
  ).render

  private[this] val langLabel = a(href := "#", cls := "dropdown-toggle", data.toggle := "dropdown", role := "button", aria.haspopup := true, aria.expanded := false).render

  private[this] val navigator = tag("nav")(cls := "navbar navbar-default navbar-fixed-top",
    div(cls := "container",
      div(cls := "row")(
        div(cls := "navbar-header col-md-10 col-md-offset-1",
          ul(cls := "nav navbar-nav",
            li(ModeChanger.element),
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
        button(cls := "btn btn-default", data("clipboard-target") := s"#${target}", tpe := "button",
          data("toggle") := "tooltip", data("trigger") := "manual", data("placement") := "bottom",
          "Copy!"
        )
      )
    )
  ).render

  private[this] def setTooltip(elem: Element, message: String): Unit = {
    jQuery(elem).attr("data-original-title", message).asInstanceOf[BootstrapJQuery].tooltip("show")
  }

  private[this] def hideTooltip(elem: Element): Unit = {
    val f = () => jQuery(elem).asInstanceOf[BootstrapJQuery].tooltip("hide").attr("data-original-title", "")
    dom.window.setTimeout(f, 1000)
  }

  private[this] def createControlInput(controlType: Int, glyph: String) = button(cls := "btn btn-default btn-control",
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
      div(cls := "btn-toolbar", role := "toolbar",
        div(cls := "btn-group", role := "group", aria.label := "...",
          div(cls := "btn-group", role := "group", controlInput0),
          div(cls := "btn-group", role := "group", controlInput1),
          div(cls := "btn-group", role := "group", recordSelector),
          div(cls := "btn-group", role := "group", controlInput2),
          div(cls := "btn-group", role := "group", controlInput3)
        )
      )
    ),
    br(),
    createInputGroup("Snapshot URL", snapshotInput, "snapshot"),
    br(),
    createInputGroup("Record URL", recordInput, "record")
  ).render

  object ModeChanger {
    private[this] val anchors: Map[Mode, Element] = Map(
      Playing -> a(cls := "btn btn-primary thin-btn active", onclick := { () => Controller.setMode(Playing) }, "Play").render,
      Viewing -> a(cls := "btn btn-primary thin-btn notActive", onclick := { () => Controller.setMode(Viewing) }, "View").render,
      Editing -> a(cls := "btn btn-primary thin-btn notActive", onclick := { () => Controller.setMode(Editing) }, "Edit").render
    )

    val element: Div = div(cls := "input-group",
      div(id := "radioBtn", cls := "btn-group",
        anchors(Playing),
        anchors(Viewing),
        anchors(Editing)
      )
    ).render

    def updateModeChangerValue(newValue: Mode): Unit = {
      anchors.foreach { case (mode, elem) =>
        if (mode == newValue) {
          elem.classList.remove("notActive")
          elem.classList.add("active")
        } else {
          elem.classList.remove("active")
          elem.classList.add("notActive")
        }
      }
    }
  }

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

    private[this] val buttons = states.map(st =>
      button(
        tpe := "button",
        cls := "btn btn-default btn-block",
        onclick := { () => Controller.setEditInitialState(st) }, ""
      ).render)

    val element: Div = div(
      label("Reset"),
      div(cls := "row", buttons.map(b => div(cls := "col-md-4 col-xs-6", b)))
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
    cp.on("success", (e: Event) => {
      setTooltip(e.trigger, "Copied!")
      hideTooltip(e.trigger)
    })
    cp.on("error", (e: Event) => {
      setTooltip(e.trigger, "Failed!")
      hideTooltip(e.trigger)
    })

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

  def expandCanvas(): Unit = {
    canvasContainer.style.height = layout.canvasHeight + "px"
    canvases.foreach(_.height = layout.canvasHeight)
  }

  def contractCanvas(): Unit = {
    canvasContainer.style.height = layout.canvasHeightCompact + "px"
    canvases.foreach(_.height = layout.canvasHeightCompact)
  }

  def drawBoard(): Unit = {
    layout.board.draw(layer1)
    layout.handWhite.draw(layer1)
    layout.handBlack.draw(layer1)
    layout.playerBlack.draw(layer1)
    layout.playerWhite.draw(layer1)

    drawPlayerIcon()

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

  def drawPlayerIcon(): Unit = {
    val ctx = layer0
    List(("☖", layout.playerIconWhite, true), ("☗", layout.playerIconBlack, false)).foreach { case (t, r, rot) =>
      drawTextCenter(ctx, t, r.left, r.top, r.width, r.height, layout.font.playerIcon, layout.color.fg, rot)
    }
  }

  def clearPlayerIcon(): Unit = {
    val ctx = layer0
    List(layout.playerIconWhite, layout.playerIconBlack).foreach(_.clear(ctx))
  }

  def drawPlayerNames(lang: Language): Unit = {
    val ctx = layer0
    val (b, w, font) = lang match {
      case Japanese => ("先手", "後手", layout.font.playerNameJapanese)
      case English => ("Black", "White", layout.font.playerNameEnglish)
    }

    // clear
    layout.playerNameWhite.clear(ctx)
    layout.playerNameBlack.clear(ctx)

    // draw
    List((b, layout.playerNameBlack, false), (w, layout.playerNameWhite, true)).foreach { case (t, r, rot) =>
      drawTextCenter(ctx, t, r.left, r.top, r.width, r.height, font, layout.color.fg, rot)
    }
  }

  def drawIndexes(lang: Language): Unit = {
    val ctx = layer3
    val rankIndex = lang match {
      case Japanese => "一二三四五六七八九"
      case English => "abcdefghi"
    }

    // clear
    layout.fileIndex.clear(ctx)
    layout.rankIndex.clear(ctx)

    // file
    for (i <- 0 until 9) {
      val text = "１２３４５６７８９".charAt(i).toString
      val left = layout.fileIndex.left + layout.PIECE_WIDTH * (8 - i)
      val top = layout.fileIndex.top
      drawTextCenter(ctx, text, left, top, layout.PIECE_WIDTH, layout.fileIndex.height, layout.font.numberIndex, layout.color.fg, rotated = false)
    }

    //rank
    for (i <- 0 until 9) {
      val text = rankIndex.charAt(i).toString
      val left = layout.rankIndex.left
      val top = layout.rankIndex.top + layout.PIECE_HEIGHT * i
      drawTextCenter(ctx, text, left, top, layout.rankIndex.width, layout.PIECE_HEIGHT, layout.font.numberIndex, layout.color.fg, rotated = false)
    }
  }

  def drawPieces(pieceRenderer: PieceRenderer, state: State): Unit = {
    clearPieces()
    state.board.foreach { case (sq, pc) => pieceRenderer.drawOnBoard(layer2, pc, sq) }
    state.hand.withFilter(_._2 > 0).foreach { case (pc, n) => pieceRenderer.drawInHand(layer2, pc, n) }
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
    hand.withFilter(_._2 > 0).foreach { case (pc, n) => pieceRenderer.drawInHand(layer2, pc, n) }
    box.withFilter(_._2 > 0).foreach { case (pt, n) => pieceRenderer.drawInBox(layer2, pt, n) }
  }

  def clearPiecesInBox(): Unit = {
    layout.pieceBox.clear(layer2)
  }

  def drawIndicators(turn: Player, status: GameStatus): Unit = {
    def f(rect: Rectangle, text: String, rotated: Boolean): Unit = {
      drawTextCenter(layer0, text, rect.left, rect.top, rect.width, rect.height, layout.font.indicator, layout.color.white, rotated)
    }

    status match {
      case GameStatus.Playing =>
        (if (turn.isBlack) layout.indicatorWhite else layout.indicatorBlack).clear(layer0)
        val r = if (turn.isBlack) layout.indicatorBlack else layout.indicatorWhite
        r.drawFill(layer0, layout.color.active)
        f(r, "TURN", turn.isWhite)
      case GameStatus.Mated =>
        val winner = turn.isBlack.fold(layout.indicatorWhite, layout.indicatorBlack)
        val loser = turn.isBlack.fold(layout.indicatorBlack, layout.indicatorWhite)
        winner.drawFill(layer0, layout.color.win)
        f(winner, "WIN", turn.isBlack)
        loser.drawFill(layer0, layout.color.lose)
        f(loser, "LOSE", turn.isWhite)
      case GameStatus.PerpetualCheck | GameStatus.Uchifuzume =>
        val winner = turn.isBlack.fold(layout.indicatorBlack, layout.indicatorWhite)
        val loser = turn.isBlack.fold(layout.indicatorWhite, layout.indicatorBlack)
        winner.drawFill(layer0, layout.color.win)
        f(winner, "WIN", turn.isWhite)
        loser.drawFill(layer0, layout.color.lose)
        f(loser, "LOSE", turn.isBlack)
      case GameStatus.Drawn =>
        layout.indicatorWhite.drawFill(layer0, layout.color.draw)
        layout.indicatorBlack.drawFill(layer0, layout.color.draw)
        f(layout.indicatorBlack, "DRAW", rotated = false)
        f(layout.indicatorWhite, "DRAW", rotated = true)
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

  def askPromote(pieceRenderer: PieceRenderer, lang: Language, piece: Piece, callbackUnpromote: () => Unit, callbackPromote: () => Unit): Unit = {
    PromotionDialog(lang, piece, pieceRenderer, callbackUnpromote, callbackPromote).show()
  }

  def askConfirm(lang: Language, callback: () => Unit): Unit = {
    val s = lang match {
      case Japanese => p("棋譜の情報が失われますが、よろしいですか?")
      case English => p("The record will be discarded. Are you sure?")
    }
    YesNoDialog(lang, s, callback).show()
  }

  def alertEditedState(msg: String, lang: Language): Unit = {
    val s = lang match {
      case Japanese => p("不正な局面です。", br, s"(${msg})")
      case English => p("Invalid state.", br, s"(${msg})")
    }
    AlertDialog(lang, s).show()
  }

  def updateSnapshotUrl(url: String): Unit = snapshotInput.value = url

  def updateRecordUrl(url: String): Unit = recordInput.value = url

  def updateMode(mode: Mode): Unit = ModeChanger.updateModeChangerValue(mode)

  def updateLang(lang: Language): Unit = langLabel.innerHTML = lang.label + span(cls := "caret").toString()

  def updateRecordContent(game: Game, lng: Language): Unit = {
    val f: Move => String = lng match {
      case Japanese => _.toKifString
      case English => _.toWesternNotationString
    }

    val xs = game.moves.zipWithIndex.map { case (m, i) =>
      s"${i + 1}: ${game.history(i).turn.toSymbolString + f(m)}"
    }.toList
    val prefix = lng match {
      case Japanese => "初期局面"
      case English => "Start"
    }
    val suffix = (game.status, lng) match {
      case (GameStatus.Mated, Japanese) => List("詰み")
      case (GameStatus.Drawn, Japanese) => List("千日手")
      case (GameStatus.PerpetualCheck, Japanese) => List("連続王手の千日手")
      case (GameStatus.Uchifuzume, Japanese) => List("打ち歩詰め")
      case (GameStatus.Mated, English) => List("Mated")
      case (GameStatus.Drawn, English) => List("Drawn")
      case (GameStatus.PerpetualCheck, English) => List("Perpetual Check")
      case (GameStatus.Uchifuzume, English) => List("Uchifuzume")
      case (GameStatus.Playing, _) => List()
    }
    val ys = prefix :: xs ++ suffix

    recordSelector.innerHTML = ys.map(s => option(s)).mkString
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
