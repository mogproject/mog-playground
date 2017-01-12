package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.core.MoveBuilderSfen
import com.mogproject.mogami.core.State.PromotionFlag
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.playground.view.Renderer
import com.mogproject.mogami.{Game, Move, State}
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

  // -1: Latest
  private[this] var currentMove: Int = -1

  private[this] var rendererVal: Option[Renderer] = None
  private[this] var activeCursor: Option[Cursor] = None
  private[this] var selectedCursor: Option[Cursor] = None

  private[this] def isLatestState: Boolean = currentMove < 0 || currentMove == game.moves.length

  private[this] def currentState: State = (currentMove < 0).fold(game.currentState, game.history(currentMove))

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

    // current move
    currentMove = math.min(game.moves.length, args.currentMove)

    // draw board and pieces
    renderer.drawBoard()
    updateCurrentState()
    updateUrls()
    renderer.setMode(config.mode)
    renderer.setLang(config.lang)
    renderer.setRecord(game, config.lang)
    renderer.selectRecord(currentMove)

    // register mouse event handlers
    if (renderer.hasTouchEvent) {
      renderer.setEventListener("touchstart", touchStart)
    } else {
      renderer.setEventListener("mousemove", mouseMove)
      renderer.setEventListener("mousedown", mouseDown)
    }

  }

  private[this] def updateCurrentState(): Unit = {
    // clear cursors
    renderer.clearCursor()
    clearSelection()

    // draw status
    renderer.drawPieces(config.pieceRenderer, currentState)
    renderer.drawIndicators(currentState.turn, isLatestState.fold(game.status, GameStatus.Playing))
    renderer.drawLastMove((currentMove < 0).fold(game.lastMove, (0 < currentMove).option(game.moves(currentMove - 1))))
  }

  private[this] def updateUrls(): Unit = {
    val configParams = config.toQueryParameters
    val moveParams = (0 <= currentMove && currentMove < game.moves.length).fold(List(s"move=${currentMove}"), List.empty)

    val snapshot = ("sfen=" + encodeURIComponent(Game(game.currentState).toSfenString)) +: configParams
    val record = (("sfen=" + encodeURIComponent(game.toSfenString)) +: configParams) ++ moveParams

    renderer.updateSnapshotUrl(s"${baseUrl}?${snapshot.mkString("&")}")
    renderer.updateRecordUrl(s"${baseUrl}?${record.mkString("&")}")
  }

  def mouseMove(evt: MouseEvent): Unit = currentMode match {
    case Playing =>
      val ret = renderer.getCursor(evt.clientX, evt.clientY)

      if (ret != activeCursor) {
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

  /**
    * Move action in the play mode
    *
    * @param selected from
    * @param moveTo   to
    */
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
          game = g
          updateCurrentState()
          renderer.setRecord(game, config.lang)
          renderer.selectRecord(-1)
          updateUrls()
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

  private[this] def clearSelection(): Unit = {
    selectedCursor.foreach(renderer.clearSelectedArea)
    selectedCursor = None
  }

  def touchStart(evt: TouchEvent): Unit = mouseDown(evt.changedTouches(0).clientX, evt.changedTouches(0).clientY)

  def setMode(mode: Mode): Unit = {
    def f() = {
      renderer.setMode(mode) // view
      config = config.copy(mode = mode) // config
      updateUrls() // urls
    }

    (currentMode, mode) match {
      case (Viewing, Viewing) => updateUrls()
      case (Playing, Viewing) => f()
      case (Viewing, Playing) =>
        if (isLatestState || renderer.askConfirm()) {
          if (!isLatestState) {
            game = game.copy(moves = game.moves.take(currentMove), givenHistory = Some(game.history.take(currentMove + 1)))
            renderer.setRecord(game, config.lang)
          }
          currentMove = -1
          f()
        }
      case (Playing | Viewing, Editing) =>
        if (game.moves.isEmpty || renderer.askConfirm()) {
          f()
        }
      case (Editing, Playing | Viewing) =>
      // check status
      case _ => // do nothing
    }
  }

  def setLanguage(lang: Language): Unit = {
    if (currentLang != lang) {
      // config
      config = config.copy(lang = lang)

      // view
      renderer.setLang(lang)
      renderer.drawPieces(config.pieceRenderer, game.currentState)
      renderer.setRecord(game, lang)

      // urls
      updateUrls()
    }
  }

  def setRecord(index: Int): Unit = {
    currentMove = math.min(game.moves.length, index)
    updateCurrentState()
    setMode(Viewing)
  }
}