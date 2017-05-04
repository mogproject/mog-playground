package com.mogproject.mogami.playground.view.parts.board

import com.mogproject.mogami.{BoardType, _}
import com.mogproject.mogami.util.MapUtil
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.playground.controller.{Configuration, English, Japanese}
import com.mogproject.mogami.playground.view._
import com.mogproject.mogami.playground.view.effect.MoveNextEffect
import org.scalajs.dom.{CanvasRenderingContext2D, UIEvent}
import org.scalajs.dom.html.{Canvas, Div}

import scalatags.JsDom.all._

/**
  *
  */
case class MainBoard(layout: Layout) extends CursorManageable with MoveNextEffect {

  // main canvas
  protected val canvas0: Canvas = createCanvas(0)
  protected val canvas1: Canvas = createCanvas(1)
  protected val canvas2: Canvas = createCanvas(2)
  protected val canvas3: Canvas = createCanvas(3)
  protected val canvas4: Canvas = createCanvas(4)
  protected val canvas5: Canvas = createCanvas(5)
  protected val canvases: List[Canvas] = List(canvas0, canvas1, canvas2, canvas3, canvas4, canvas5)

  protected val layer0: CanvasRenderingContext2D = canvas0.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer1: CanvasRenderingContext2D = canvas1.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer2: CanvasRenderingContext2D = canvas2.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer3: CanvasRenderingContext2D = canvas3.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer4: CanvasRenderingContext2D = canvas4.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  protected val layer5: CanvasRenderingContext2D = canvas5.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  // elements
  private[this] lazy val canvasContainer: Div = div(
    padding := 0,
    height := layout.canvasHeightCompact,
    canvases
  ).render

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

  private[this] def setEventListener[A](eventType: String, f: A => Unit): Unit = canvasContainer.addEventListener(eventType, f, useCapture = false)

  def initialize(): Unit = {

    // register events to the canvas
    if (hasTouchEvent) {
      setEventListener("touchstart", touchStart)
      setEventListener("touchend", touchEnd)
      setEventListener("touchcancel", { _: UIEvent => clearHoldEvent() })
    } else {
      setEventListener("mousemove", mouseMove)
      setEventListener("mousedown", mouseDown)
      setEventListener("mouseup", mouseUp)
      setEventListener("mouseout", { _: UIEvent => clearHoldEvent() })
    }

  }

  def output: Div = canvasContainer

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
        Circle(x, layout.board.top + layout.PIECE_HEIGHT * 3, layout.DOT_SIZE).draw(layer1)
        Circle(x, layout.board.top + layout.PIECE_HEIGHT * 6, layout.DOT_SIZE).draw(layer1)
      }
    }
  }

  def drawPlayerIcon(config: Configuration): Unit = {
    val (b, w) = config.flip.fold((layout.color.white, layout.color.fg), (layout.color.fg, layout.color.white))
    val ctx = layer2

    // clear
    clearPlayerIcon()

    // draw
    List((w, layout.playerIconWhite, true), (b, layout.playerIconBlack, false)).foreach { case (c, r, rot) =>
      TextRenderer(ctx, "☗", layout.font.playerIcon, c, r.left, r.top, r.width, r.height)
        .alignCenter.alignMiddle.withRotate(rot).withStroke(layout.color.fg, layout.strokeSize).render()
    }
  }

  def clearPlayerIcon(): Unit = {
    val ctx = layer2
    layout.playerIconWhite.clear(ctx)
    layout.playerIconBlack.clear(ctx)
  }

  def drawPlayerNames(config: Configuration, blackName: String, whiteName: String): Unit = {
    drawPlayerIcon(config)

    val ctx = layer0

    // clear
    clearPlayerNames()

    // draw
    List(
      (config.flip.fold(whiteName, blackName), layout.playerNameBlack, false),
      (config.flip.fold(blackName, whiteName), layout.playerNameWhite, true)).foreach { case (t, r, rot) =>
      TextRenderer(ctx, t, layout.font.playerName, layout.color.fg, r.left, r.top, r.width, r.height)
        .withTrim.alignLeft.alignMiddle.withRotate(rot).render()
    }
  }

  def clearPlayerNames(): Unit = {
    val ctx = layer0
    layout.playerNameWhite.clear(ctx, -1)
    layout.playerNameBlack.clear(ctx, -1)
  }

  def drawIndexes(config: Configuration): Unit = {
    val ctx = layer3
    val fileIndex = "１２３４５６７８９"
    val rankIndex = config.recordLang match {
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

  private[this] def drawPieces(config: Configuration, board: BoardType, hand: HandType): Unit = {
    val pr = config.pieceRenderer

    clearPieces()
    board.foreach { case (sq, pc) => pr.drawOnBoard(layer2, config.flip.when[Piece](!_)(pc), config.flip.when[Square](!_)(sq)) }
    hand.withFilter(_._2 > 0).foreach { case (pc, n) => pr.drawInHand(layer2, config.flip.when[Hand](!_)(pc), n) }
  }

  def drawPieces(config: Configuration, state: State): Unit = drawPieces(config, state.board, state.hand)

  /**
    * Draw illegal state
    */
  def drawIllegalStatePieces(config: Configuration, state: State, move: Move): Unit = {
    val releaseBoard: BoardType => BoardType = move.from.when(sq => b => b - sq)
    val releaseHand: HandType => HandType = move.isDrop.when(MapUtil.decrementMap(_, Hand(move.newPiece)))
    val obtainHand: HandType => HandType = move.capturedPiece.when(p => h => MapUtil.incrementMap(h, Hand(!p.demoted)))

    val board = releaseBoard(state.board) + (move.to -> move.newPiece)
    val hand = (releaseHand andThen obtainHand) (state.hand)
    drawPieces(config, board, hand)
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
    board.foreach { case (sq, pc) => pr.drawOnBoard(layer2, config.flip.when[Piece](!_)(pc), config.flip.when[Square](!_)(sq)) }
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

    def drawWinLose(turnWins: Boolean): Unit = {
      val winner = (t.isBlack ^ turnWins).fold(layout.indicatorWhite, layout.indicatorBlack)
      val loser = (t.isBlack ^ turnWins).fold(layout.indicatorBlack, layout.indicatorWhite)
      winner.drawFill(layer0, layout.color.win)
      f(winner, "WIN", t.isBlack ^ turnWins)
      loser.drawFill(layer0, layout.color.lose)
      f(loser, "LOSE", t.isWhite ^ turnWins)
    }

    status match {
      case GameStatus.Playing =>
        (if (t.isBlack) layout.indicatorWhite else layout.indicatorBlack).clear(layer0, -1)
        val r = if (t.isBlack) layout.indicatorBlack else layout.indicatorWhite
        r.drawFill(layer0, layout.color.active)
        f(r, "TURN", t.isWhite)
      case GameStatus.Mated | GameStatus.Resigned | GameStatus.TimedUp | GameStatus.IllegallyMoved => drawWinLose(false)
      case GameStatus.PerpetualCheck | GameStatus.Uchifuzume => drawWinLose(true)
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

  def getImageBase64: String = {
    val c = canvases.head

    // create a hidden canvas
    val hiddenCanvas: Canvas = canvas(
      widthA := c.width,
      heightA := c.height
    ).render

    val hiddenContext: CanvasRenderingContext2D = hiddenCanvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    // set background as white
    hiddenContext.fillStyle = "#ffffff"
    hiddenContext.fillRect(0, 0, c.width, c.height)

    // copy layers
    canvases.foreach(src => hiddenContext.drawImage(src, 0, 0))

    // export image
    hiddenCanvas.toDataURL("image/png")
  }

}
