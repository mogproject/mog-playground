package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.core.MoveBuilderSfen
import com.mogproject.mogami.core.State.PromotionFlag
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.playground.view.Renderer
import com.mogproject.mogami._
import com.mogproject.mogami.util.MapUtil
import org.scalajs.dom.{Element, MouseEvent, TouchEvent}

import scala.scalajs.js.URIUtils.encodeURIComponent
import scala.util.{Failure, Success, Try}


/**
  * logic controller
  */
object Controller {

  // variables
  private[this] var baseUrl: String = ""
  private[this] var config: Configuration = Configuration()
  private[this] var game: Game = Game()
  private[this] var currentMode: Mode = Playing

  // saved state for Edit Mode
  private[this] var editingTurn: Player = Player.BLACK
  private[this] var editingBoard: BoardType = Map.empty
  private[this] var editingHand: HandType = Map.empty
  private[this] var editingBox: Map[Ptype, Int] = Map.empty

  // -1: Latest
  private[this] var currentMove: Int = -1

  private[this] var rendererVal: Option[Renderer] = None
  private[this] var activeCursor: Option[Cursor] = None
  private[this] var selectedCursor: Option[Cursor] = None

  private[this] def isLatestState: Boolean = currentMove < 0 || currentMove == game.moves.length

  private[this] def currentState: State = (currentMove < 0).fold(game.currentState, game.history(currentMove))

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
    this.config = args.config

    // create game
    game = args.game

    // create renderer
    rendererVal = Some(Renderer(elem, args.config.layout))

    // update mode
    currentMode = game.moves.nonEmpty.fold(Viewing, Playing)

    // current move
    currentMove = math.min(game.moves.length, args.currentMove)

    // draw board and pieces
    renderer.drawBoard()
    renderer.drawIndexes(config.lang)
    updateUrls()
    renderer.setMode(currentMode)
    renderer.setLang(config.lang)
    renderer.setRecord(game, config.lang)
    renderer.selectRecord(currentMove)
    updateCurrentState()

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

    // update control bar
    renderer.updateControlBar()
  }

  private[this] def updateUrls(): Unit = {
    val configParams = config.toQueryParameters
    val moveParams = (0 <= currentMove && currentMove < game.moves.length).fold(List(s"move=${currentMove}"), List.empty)

    val snapshot = ("sfen=" + encodeURIComponent(Game(currentState).toSfenString)) +: configParams
    val record = (("sfen=" + encodeURIComponent(game.toSfenString)) +: configParams) ++ moveParams

    renderer.updateSnapshotUrl(s"${baseUrl}?${snapshot.mkString("&")}")
    renderer.updateRecordUrl(s"${baseUrl}?${record.mkString("&")}")
  }

  def mouseMove(evt: MouseEvent): Unit = currentMode match {
    case Playing =>
      val ret = renderer.getCursor(evt.clientX, evt.clientY)

      if (ret != activeCursor) {
        ret match {
          case Some(c@Cursor(Some(_), None, None)) => renderer.drawCursor(c)
          case Some(c@Cursor(None, Some(h), None)) if currentState.hasHand(h) => renderer.drawCursor(c)
          case None => renderer.clearCursor()
        }
        activeCursor = ret
      }
    case Editing =>
      val ret = renderer.getCursor(evt.clientX, evt.clientY)

      if (ret != activeCursor) {
        ret match {
          case Some(c) => renderer.drawCursor(c)
          case None => renderer.clearCursor()
        }
        activeCursor = ret
      }
    case Viewing => // do nothing
  }

  def mouseDown(evt: MouseEvent): Unit = mouseDown(evt.clientX, evt.clientY)

  private[this] def mouseDown(x: Double, y: Double): Unit = currentMode match {
    case Playing =>
      (selectedCursor, renderer.getCursor(x, y)) match {
        case (Some(selected), Some(Cursor(Some(moveTo), None, None))) => moveAction(selected.moveFrom, moveTo)
        case (None, Some(selected)) => selectAction(selected)
        case _ => // do nothing
      }
    case Editing =>
      (selectedCursor, renderer.getCursor(x, y)) match {
        case (Some(selected), Some(exchangeTo)) => exchangeAction(selected, exchangeTo)
        case (None, Some(selected)) => selectAction(selected)
        case _ => // do nothing
      }
    case _ =>
  }

  /**
    * Move action in the play mode
    *
    * @param from from
    * @param to   to
    */
  private[this] def moveAction(from: MoveFrom, to: Square): Unit = {
    clearSelection()

    if (currentState.canAttack(from, to)) {
      val nextGame: Option[Game] = game.currentState.getPromotionFlag(from, to) match {
        case Some(PromotionFlag.CannotPromote) => game.makeMove(MoveBuilderSfen(from, to, promote = false))
        case Some(PromotionFlag.CanPromote) => game.makeMove(MoveBuilderSfen(from, to, renderer.askPromote(config.lang)))
        case Some(PromotionFlag.MustPromote) => game.makeMove(MoveBuilderSfen(from, to, promote = true))
        case None => None
      }
      nextGame.foreach { g =>
        game = g
        renderer.setRecord(game, config.lang)
        renderer.selectRecord(-1)
        updateCurrentState()
        updateUrls()
      }
    }
  }

  private[this] def selectAction(selected: Cursor): Unit = {
    val canSelect = (currentMode, selected) match {
      case (Playing, Cursor(Some(sq), None, None)) => game.currentState.board.get(sq).exists(game.currentState.turn == _.owner)
      case (Playing, Cursor(None, Some(h), None)) => h.owner == game.currentState.turn && game.currentState.hand.get(h).exists(_ > 0)
      case (Editing, Cursor(Some(sq), None, None)) => editingBoard.contains(sq)
      case (Editing, Cursor(None, Some(h), None)) => editingHand(h) > 0
      case (Editing, Cursor(None, None, Some(pt))) => editingBox(pt) > 0
      case _ => false
    }
    if (canSelect) {
      selectedCursor = Some(selected)
      renderer.drawSelectedArea(selected)
    }
  }

  /**
    * Exchange action in the edit mode
    *
    * @param selected   from
    * @param exchangeTo to
    */
  private[this] def exchangeAction(selected: Cursor, exchangeTo: Cursor): Unit = {
    clearSelection()

    (selected, exchangeTo) match {
      // square is selected
      case (Cursor(Some(s1), None, None), Cursor(Some(s2), None, None)) =>
        (editingBoard(s1), editingBoard.get(s2)) match {
          case (p1, Some(p2)) if p1 == p2 =>
            // change piece attributes
            editingBoard = editingBoard.updated(s1, p1.canPromote.fold(p1.promoted, !p1.demoted))
          case (p1, Some(p2)) =>
            // change pieces
            editingBoard = editingBoard.updated(s1, p2).updated(s2, p1)
          case (p1, None) =>
            editingBoard = editingBoard.updated(s2, p1) - s1
        }
      case (Cursor(Some(s), None, None), Cursor(None, Some(h), None)) if editingBoard(s).ptype != KING =>
        val pt = editingBoard(s).ptype.demoted
        editingBoard -= s
        editingHand = MapUtil.incrementMap(editingHand,Hand(h.owner, pt))
      case (Cursor(Some(s), None, None), Cursor(None, None, Some(_))) =>
        val pt = editingBoard(s).ptype.demoted
        editingBoard -= s
        editingBox = MapUtil.incrementMap(editingBox, pt)

      // hand is selected
      case (Cursor(None, Some(h), None), Cursor(Some(s), None, None)) if !editingBoard.get(s).exists(_.ptype == KING) =>
        editingHand = MapUtil.decrementMap(editingHand, h)
        editingBoard.get(s).foreach { p => editingHand = MapUtil.incrementMap(editingHand, Hand(h.owner, p.ptype.demoted)) }
        editingBoard = editingBoard.updated(s, h.toPiece)
      case (Cursor(None, Some(h1), None), Cursor(None, Some(h2), None)) if h1.owner != h2.owner =>
        editingHand = MapUtil.decrementMap(editingHand, h1)
        editingHand = MapUtil.incrementMap(editingHand, Hand(!h1.owner, h1.ptype))
      case (Cursor(None, Some(h), None), Cursor(None, None, Some(_))) =>
        editingHand = MapUtil.decrementMap(editingHand, h)
        editingBox = MapUtil.incrementMap(editingBox, h.ptype)

      // box is selected
      case (Cursor(None, None, Some(pt)), Cursor(Some(s), None, None)) =>
        editingBox = MapUtil.decrementMap(editingBox, pt)
        editingBoard.get(s).foreach { p => editingBox = MapUtil.incrementMap(editingBox, p.ptype.demoted) }
        editingBoard = editingBoard.updated(s, Piece(Player.BLACK, pt))
      case (Cursor(None, None, Some(pt)), Cursor(None, Some(h), None)) if pt != KING =>
        editingBox = MapUtil.decrementMap(editingBox, pt)
        editingHand = MapUtil.incrementMap(editingHand, Hand(h.owner, pt))
      case _ => // do nothing
    }

    renderer.drawEditingPieces(config.pieceRenderer, editingBoard, editingHand, editingBox)
  }

  private[this] def clearSelection(): Unit = {
    selectedCursor.foreach(renderer.clearSelectedArea)
    selectedCursor = None
  }

  def touchStart(evt: TouchEvent): Unit = mouseDown(evt.changedTouches(0).clientX, evt.changedTouches(0).clientY)

  def setMode(mode: Mode): Unit = {
    def f() = {
      renderer.setMode(mode) // view
      currentMode = mode
      updateUrls() // urls
    }

    (currentMode, mode) match {
      case (Viewing, Viewing) => updateUrls()
      case (Playing, Viewing) => f()
      case (Viewing, Playing) =>
        if (isLatestState || renderer.askConfirm(config.lang)) {
          if (!isLatestState) {
            game = game.copy(moves = game.moves.take(currentMove), givenHistory = Some(game.history.take(currentMove + 1)))
            renderer.setRecord(game, config.lang)
          }
          currentMove = -1
          f()
        }
      case (Playing | Viewing, Editing) =>
        if (game.moves.isEmpty || renderer.askConfirm(config.lang)) {
          renderer.hideControlSection()

          editingTurn = currentState.turn
          editingBoard = currentState.board
          editingHand = currentState.hand
          editingBox = currentState.getUnusedPtypeCount

          renderer.clearCursor()
          clearSelection()
          renderer.clearLastMove()

          renderer.setRecord(Game(), config.lang)
          renderer.drawIndicators(editingTurn, GameStatus.Playing)
          renderer.drawPieceBox()
          renderer.drawEditingPieces(config.pieceRenderer, editingBoard, editingHand, editingBox)
          f()
        }
      case (Editing, Playing | Viewing) =>
        // check status
        Try(State(editingTurn, editingBoard, editingHand, None)) match {
          case Success(st) =>
            game = Game(st)
            renderer.hidePieceBox()
            renderer.showControlSection()
            renderer.updateControlBar()
            updateCurrentState()
            f()
          case Failure(e) =>
            renderer.alertEditedState(e.getMessage, config.lang)
        }
      case _ => // do nothing
    }
  }

  def setLanguage(lang: Language): Unit = {
    if (currentLang != lang) {
      // config
      config = config.copy(lang = lang)

      currentMode match {
        case Playing | Viewing =>
          // view
          renderer.drawIndexes(lang)
          renderer.setLang(lang)
          renderer.drawPieces(config.pieceRenderer, game.currentState)
          renderer.setRecord(game, lang)

          // urls
          updateUrls()
        case Editing =>
          renderer.drawIndexes(lang)
          renderer.setLang(lang)
          renderer.drawEditingPieces(config.pieceRenderer, editingBoard, editingHand, editingBox)
      }
    }
  }

  def setRecord(index: Int): Unit = {
    currentMove = math.min(game.moves.length, index)
    updateCurrentState()
    setMode(Viewing)
  }

  def setControl(controlType: Int): Unit = {
    val index = controlType match {
      case 0 => 0
      case 1 => renderer.getSelectedIndex - 1
      case 2 => renderer.getSelectedIndex + 1
      case 3 => -1
    }
    renderer.selectRecord(index)
    setRecord(index)
  }
}