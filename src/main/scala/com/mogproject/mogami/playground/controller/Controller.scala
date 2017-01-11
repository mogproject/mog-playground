package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.core.MoveBuilderSfen
import com.mogproject.mogami.core.State.PromotionFlag
import com.mogproject.mogami.playground.view.Renderer
import com.mogproject.mogami.Game
import org.scalajs.dom.{Element, MouseEvent, TouchEvent}

import scala.scalajs.js.URIUtils.encodeURIComponent


/**
  * logic controller
  */
object Controller {

  // variables
  private[this] var baseUrl: String = ""
  private[this] var config: Configuration = Configuration()
  private[this] var game: Game = Game()
  private[this] var currentMove: Int = 0
  private[this] var rendererVal: Option[Renderer] = None
  private[this] var activeCursor: Option[Cursor] = None
  private[this] var selectedCursor: Option[Cursor] = None

  private[this] def currentMode: Mode = config.mode

  private[this] def currentLang: Language = config.lang

  private[this] def renderer = rendererVal.get

  /**
    * Initialize the game and renderer
    *
    * @param elem parent HTML element
    * @param args Arguments instance
    */
  def initialize(elem: Element, args: Arguments, baseUrl: String): Unit = {
    this.baseUrl = baseUrl

    // create game
    game = args.game

    // create renderer
    rendererVal = Some(Renderer(elem, args.config.layout))

    // update mode
    config = (args.config.mode, game.moves.nonEmpty) match {
      case (Playing, true) => args.config.copy(mode = Viewing)
      case _ => args.config
    }

    // draw board and pieces
    renderer.drawBoard()
    renderer.setMode(config.mode)
    renderer.setLang(config.lang)
    renderer.drawPieces(config.pieceRenderer, game.currentState)
    renderer.drawIndicators(game)
    updateLastMove()
    updateUrls()

    // register mouse event handlers
    if (renderer.hasTouchEvent) {
      renderer.setEventListener("touchstart", touchStart)
    } else {
      renderer.setEventListener("mousemove", mouseMove)
      renderer.setEventListener("mousedown", mouseDown)
    }

  }

  private[this] def updateUrls(): Unit = {
    val snapshot = encodeURIComponent(Game(game.currentState).toSfenString)
    val record = encodeURIComponent(game.toSfenString)

    renderer.updateSnapshotUrl(s"${baseUrl}?sfen=${snapshot}&${config.toQueryString}")
    renderer.updateRecordUrl(s"${baseUrl}?sfen=${record}&${config.toQueryString}")
  }

  private[this] def updateLastMove(): Unit = lastMoveToCursors().foreach(renderer.drawLastMoveArea)

  private[this] def clearLastMove(): Unit = lastMoveToCursors().foreach(renderer.clearLastMoveArea)

  private[this] def lastMoveToCursors(): Option[Seq[Cursor]] = game.lastMove.map { m =>
    val fr = m.from match {
      case None => Cursor(m.player, m.oldPtype)
      case Some(sq) => Cursor(sq)
    }
    List(fr, Cursor(m.to))
  }

  def mouseMove(evt: MouseEvent): Unit = currentMode match {
    case Playing =>
      val ret = renderer.getCursor(evt.clientX, evt.clientY)

      if (ret != activeCursor) {
        activeCursor.foreach(renderer.clearCursor)
        ret.foreach(c => if (!c.isHand || game.currentState.hand.get(c.moveFrom.right.get).exists(_ > 0)) renderer.drawCursor(c))
        activeCursor = ret
      }
    case _ =>
  }

  def mouseDown(evt: MouseEvent): Unit = mouseDown(evt.clientX, evt.clientY)

  private[this] def mouseDown(x: Double, y: Double): Unit = currentMode match {
    case Playing =>
      (selectedCursor, renderer.getCursor(x, y)) match {
        case (Some(selected), Some(moveTo)) => moveAction(selected, moveTo)
        case (None, Some(selected)) => selectAction(selected)
        case _ => // do nothing
      }
    case _ =>
  }

  private[this] def moveAction(selected: Cursor, moveTo: Cursor): Unit = {
    renderer.clearSelectedArea(selected)
    selectedCursor = None

    (selected, moveTo) match {
      case (Cursor(from), Cursor(Left(to))) if game.currentState.canAttack(from, to) =>
        val nextGame: Option[Game] = game.currentState.getPromotionFlag(from, to) match {
          case Some(PromotionFlag.CannotPromote) => game.makeMove(MoveBuilderSfen(from, to, promote = false))
          case Some(PromotionFlag.CanPromote) => game.makeMove(MoveBuilderSfen(from, to, renderer.askPromote()))
          case Some(PromotionFlag.MustPromote) => game.makeMove(MoveBuilderSfen(from, to, promote = true))
          case None => None
        }
        nextGame.foreach { g =>
          renderer.drawPieces(config.pieceRenderer, g.currentState)
          renderer.drawIndicators(g)
          clearLastMove()
          game = g
          updateUrls()
          updateLastMove()
        }
      case _ => // do nothing
    }
  }

  private[this] def selectAction(selected: Cursor): Unit = {
    val canSelect = selected match {
      case Cursor(Left(sq)) => game.currentState.board.get(sq).exists(game.currentState.turn == _.owner)
      case Cursor(Right(h)) => h.owner == game.currentState.turn && game.currentState.hand.get(h).exists(_ > 0)
    }
    if (canSelect) {
      selectedCursor = Some(selected)
      renderer.drawSelectedArea(selected)
    }
  }

  def touchStart(evt: TouchEvent): Unit = mouseDown(evt.changedTouches(0).clientX, evt.changedTouches(0).clientY)

  def setMode(mode: Mode): Unit = {
    if (currentMode != mode) {
      // config
      config = config.copy(mode = mode)

      // view
      renderer.setMode(mode)

      // urls
      updateUrls()
    }
  }

  def setLanguage(lang: Language): Unit = {
    if (currentLang != lang) {
      // config
      config = config.copy(lang = lang)

      // view
      renderer.setLang(lang)
      renderer.drawPieces(config.pieceRenderer, game.currentState)

      // urls
      updateUrls()
    }
  }
}