package com.mogproject.mogami.playground.view

import com.mogproject.mogami.playground.controller._
import com.mogproject.mogami._
import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.core.Game.GameStatus.GameStatus
import com.mogproject.mogami.playground.view.piece.PieceRenderer
import com.mogproject.mogami.playground.api.Clipboard
import com.mogproject.mogami.playground.api.Clipboard.Event
import com.mogproject.mogami.playground.controller.mode.Mode
import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import com.mogproject.mogami.playground.view.modal._
import com.mogproject.mogami.playground.view.parts._
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
case class Renderer(elem: Element, layout: Layout) extends CursorManageable with EventManageable {

  // main canvas
  protected val canvas0: Canvas = createCanvas(0)
  protected val canvas1: Canvas = createCanvas(1)
  protected val canvas2: Canvas = createCanvas(2)
  protected val canvas3: Canvas = createCanvas(3)
  protected val canvas4: Canvas = createCanvas(4)
  protected val canvases: List[Canvas] = List(canvas0, canvas1, canvas2, canvas3, canvas4)

  protected val layer0: CanvasRenderingContext2D = canvas0.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer1: CanvasRenderingContext2D = canvas1.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer2: CanvasRenderingContext2D = canvas2.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer3: CanvasRenderingContext2D = canvas3.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer4: CanvasRenderingContext2D = canvas4.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  // elements
  val saveImageButton = SaveImageButton(canvases)

  private[this] val canvasContainer: Div = div(cls := "col-md-6",
    padding := 0,
    height := layout.canvasHeightCompact,
    canvases
  ).render

  // forms
  private[this] val recordSelector: HTMLSelectElement = select(
    cls := "form-control rect-select",
    onchange := (() => Controller.setRecord(recordSelector.selectedIndex))
  ).render

  private[this] val navigator = tag("nav")(cls := "navbar navbar-default navbar-fixed-top",
    div(cls := "container",
      div(cls := "row")(
        div(cls := "navbar-header col-md-10 col-md-offset-1",
          ul(cls := "nav navbar-nav",
            li(ModeSelector.output),
            LanguageSelector.output,
            FlipButton.output
          )
        )
      )
    )
  )

  private[this] val snapshotInput = createInput("snapshot")
  private[this] val recordInput = createInput("record")
  private[this] val sfenInput = createInput("sfen")

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

  private[this] def createControlInput(glyph: String) = button(cls := "btn btn-default btn-control",
    span(cls := s"glyphicon glyphicon-${glyph}", aria.hidden := true)
  ).render

  private[this] val controlInput0 = createControlInput("step-backward")
  private[this] val controlInput1 = createControlInput("backward")
  private[this] val controlInput2 = createControlInput("forward")
  private[this] val controlInput3 = createControlInput("step-forward")

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
    createInputGroup("Record URL", recordInput, "record"),
    br(),
    saveImageButton.output,
    br(),
    createInputGroup("Snapshot SFEN String", sfenInput, "sfen")
  ).render

  private[this] val editSection = div(display := "none",
    EditTurn.output,
    EditReset.output
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
        navigator
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

    setClickEvent(controlInput0, () => Controller.setControl(0))
    setClickEvent(controlInput1, () => Controller.setControl(1))
    setClickEvent(controlInput2, () => Controller.setControl(2))
    setClickEvent(controlInput3, () => Controller.setControl(3))

    ModeSelector.initialize()
    FlipButton.initialize()
    LanguageSelector.initialize()
    EditTurn.initialize()
    EditReset.initialize()
    saveImageButton.initialize()

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
      heightA := layout.canvasHeightCompact,
      marginLeft := "auto",
      marginRight := "auto",
      left := 0,
      right := 0,
      top := 0,
      zIndex := zIndexVal
    ).render
  }

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

  def drawPlayerIcon(config: Configuration): Unit = {
    val (b, w) = config.flip.fold(("☖", "☗"), ("☗", "☖"))
    val ctx = layer0

    // clear
    clearPlayerIcon()

    // draw
    List((w, layout.playerIconWhite, true), (b, layout.playerIconBlack, false)).foreach { case (t, r, rot) =>
      TextRenderer(ctx, t, layout.font.playerIcon, layout.color.fg, r.left, r.top, r.width, r.height)
        .alignCenter.alignMiddle.withRotate(rot).render()
    }
  }

  def clearPlayerIcon(): Unit = {
    val ctx = layer0
    layout.playerIconWhite.clear(ctx)
    layout.playerIconBlack.clear(ctx)
  }

  def drawPlayerNames(config: Configuration): Unit = {
    drawPlayerIcon(config)

    val ctx = layer0
    val (b, w, font) = config.lang match {
      case Japanese => ("先手", "後手", layout.font.playerNameJapanese)
      case English => ("Black", "White", layout.font.playerNameEnglish)
    }

    // clear
    clearPlayerNames()

    // draw
    List(
      (config.flip.fold(w, b), layout.playerNameBlack, false),
      (config.flip.fold(b, w), layout.playerNameWhite, true)).foreach { case (t, r, rot) =>
      TextRenderer(ctx, t, font, layout.color.fg, r.left, r.top, r.width, r.height)
        .alignCenter.alignMiddle.withRotate(rot).render()
    }
  }

  def clearPlayerNames(): Unit = {
    val ctx = layer0
    layout.playerNameWhite.clear(ctx)
    layout.playerNameBlack.clear(ctx)
  }

  def drawIndexes(config: Configuration): Unit = {
    val ctx = layer3
    val fileIndex = "１２３４５６７８９"
    val rankIndex = config.lang match {
      case Japanese => "一二三四五六七八九"
      case English => "abcdefghi"
    }

    // clear
    layout.fileIndex.clear(ctx)
    layout.rankIndex.clear(ctx)

    // file
    for (i <- 0 until 9) {
      val text = fileIndex.charAt(config.flip.fold(8 - i, i)).toString
      val left = layout.fileIndex.left + layout.PIECE_WIDTH * (8 - i)
      val top = layout.fileIndex.top

      TextRenderer(ctx, text, layout.font.numberIndex, layout.color.fg, left, top, layout.PIECE_WIDTH, layout.fileIndex.height)
        .alignCenter.alignMiddle.render()
    }

    //rank
    for (i <- 0 until 9) {
      val text = rankIndex.charAt(config.flip.fold(8 - i, i)).toString
      val left = layout.rankIndex.left
      val top = layout.rankIndex.top + layout.PIECE_HEIGHT * i
      TextRenderer(ctx, text, layout.font.numberIndex, layout.color.fg, left, top, layout.rankIndex.width, layout.PIECE_HEIGHT)
        .alignCenter.alignMiddle.render()
    }
  }

  def drawPieces(config: Configuration, state: State): Unit = {
    val pr = config.pieceRenderer

    clearPieces()
    state.board.foreach { case (sq, pc) => pr.drawOnBoard(layer2, config.flip.when[Piece](!_)(pc), config.flip.when(flipSquare)(sq)) }
    state.hand.withFilter(_._2 > 0).foreach { case (pc, n) => pr.drawInHand(layer2, config.flip.when[Hand](!_)(pc), n) }
  }

  def clearPieces(): Unit = {
    layout.board.clear(layer2)
    layout.handWhite.clear(layer2, -4)
    layout.handBlack.clear(layer2, -4)
  }

  def drawEditingPieces(config: Configuration, board: BoardType, hand: HandType, box: Map[Ptype, Int]): Unit = {
    val pr = config.pieceRenderer

    clearPieces()
    clearPiecesInBox()
    board.foreach { case (sq, pc) => pr.drawOnBoard(layer2, config.flip.when[Piece](!_)(pc), config.flip.when(flipSquare)(sq)) }
    hand.withFilter(_._2 > 0).foreach { case (pc, n) => pr.drawInHand(layer2, config.flip.when[Hand](!_)(pc), n) }
    box.withFilter(_._2 > 0).foreach { case (pt, n) => pr.drawInBox(layer2, pt, n) }
  }

  def clearPiecesInBox(): Unit = {
    layout.pieceBox.clear(layer2, -4)
  }

  def drawIndicators(config: Configuration, turn: Player, status: GameStatus): Unit = {
    val t = config.flip.fold(!turn, turn) // flip turn

    def f(r: Rectangle, text: String, rotated: Boolean): Unit = {
      TextRenderer(layer0, text, layout.font.indicator, layout.color.white, r.left, r.top, r.width, r.height)
        .alignCenter.alignMiddle.withRotate(rotated).render()
    }

    status match {
      case GameStatus.Playing =>
        (if (t.isBlack) layout.indicatorWhite else layout.indicatorBlack).clear(layer0, -1)
        val r = if (t.isBlack) layout.indicatorBlack else layout.indicatorWhite
        r.drawFill(layer0, layout.color.active)
        f(r, "TURN", t.isWhite)
      case GameStatus.Mated =>
        val winner = t.isBlack.fold(layout.indicatorWhite, layout.indicatorBlack)
        val loser = t.isBlack.fold(layout.indicatorBlack, layout.indicatorWhite)
        winner.drawFill(layer0, layout.color.win)
        f(winner, "WIN", t.isBlack)
        loser.drawFill(layer0, layout.color.lose)
        f(loser, "LOSE", t.isWhite)
      case GameStatus.PerpetualCheck | GameStatus.Uchifuzume =>
        val winner = t.isBlack.fold(layout.indicatorBlack, layout.indicatorWhite)
        val loser = t.isBlack.fold(layout.indicatorWhite, layout.indicatorBlack)
        winner.drawFill(layer0, layout.color.win)
        f(winner, "WIN", t.isWhite)
        loser.drawFill(layer0, layout.color.lose)
        f(loser, "LOSE", t.isBlack)
      case GameStatus.Drawn =>
        layout.indicatorWhite.drawFill(layer0, layout.color.draw)
        layout.indicatorBlack.drawFill(layer0, layout.color.draw)
        f(layout.indicatorBlack, "DRAW", rotated = false)
        f(layout.indicatorWhite, "DRAW", rotated = true)
    }
  }

  def drawPieceBox(): Unit = {
    val r = layout.pieceBox
    r.draw(layer1, layout.color.pieceBox, 3)
    TextRenderer(layer1, "UNUSED PIECES", layout.font.pieceBoxLabel, layout.color.fg, r.left, r.top - layout.MARGIN_BLOCK, r.width, layout.MARGIN_BLOCK * 3 / 2)
      .alignCenter.alignMiddle.render()
  }

  def hidePieceBox(): Unit = {
    layout.pieceBox.clear(layer1, -3)
    clearPiecesInBox()
  }

  def showEditSection(): Unit = editSection.style.display = "block"

  def hideEditSection(): Unit = editSection.style.display = "none"

  def showControlSection(): Unit = controlSection.style.display = "block"

  def hideControlSection(): Unit = controlSection.style.display = "none"

  def askPromote(config: Configuration, piece: Piece, callbackUnpromote: () => Unit, callbackPromote: () => Unit): Unit = {
    PromotionDialog(config, piece, callbackUnpromote, callbackPromote).show()
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

  def updateSfenString(sfen: String): Unit = sfenInput.value = sfen

  def updateMode(mode: Mode): Unit = ModeSelector.updateValue(mode)

  def updateLang(config: Configuration): Unit = LanguageSelector.updateValue(config.lang)

  def updateFlip(config: Configuration): Unit = FlipButton.updateValue(config.flip)

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

  def getRecordIndex(index: Int): Int = (index < 0).fold(getMaxRecordIndex, math.min(index, getMaxRecordIndex))

  def updateRecordIndex(index: Int): Unit = recordSelector.selectedIndex = getRecordIndex(index)

  def getMaxRecordIndex: Int = recordSelector.options.length - 1

  def getSelectedIndex: Int = recordSelector.selectedIndex

  def updateControlBar(stepBackwardEnabled: Boolean, backwardEnabled: Boolean, forwardEnabled: Boolean, stepForwardEnabled: Boolean): Unit = {
    controlInput0.disabled = !stepBackwardEnabled
    controlInput1.disabled = !backwardEnabled
    controlInput2.disabled = !forwardEnabled
    controlInput3.disabled = !stepForwardEnabled
  }

  def updateEditResetLabel(lang: Language): Unit = EditReset.updateLabel(lang)

  def updateEditTurnLabel(lang: Language): Unit = EditTurn.updateLabel(lang)

  def updateEditTurnValue(newValue: Player): Unit = EditTurn.updateValue(newValue)
}
