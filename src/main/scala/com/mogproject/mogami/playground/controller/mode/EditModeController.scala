package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami._
import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.core.GameInfo
import com.mogproject.mogami.core.State.BoardType
import com.mogproject.mogami.playground.controller._
import com.mogproject.mogami.playground.view.Renderer
import com.mogproject.mogami.util.MapUtil
import com.mogproject.mogami.util.Implicits._

import scala.util.{Failure, Success, Try}

/**
  * Edit mode
  */
case class EditModeController(renderer: Renderer,
                              config: Configuration,
                              turn: Player,
                              board: BoardType,
                              hand: HandType,
                              box: Map[Ptype, Int],
                              override val gameInfo: GameInfo
                             ) extends ModeController {

  val mode: Mode = Editing

  override def initialize(): Unit = {
    super.initialize()
    renderer.hideControlSection()
    renderer.expandCanvas()
    renderer.drawBoard()
    renderer.showEditSection()
    renderer.updateRecordContent(Game(), config.recordLang)
    renderer.drawPieceBox()
    renderAll()
  }

  override def terminate(): Unit = {
    super.terminate()
    renderer.showControlSection()
    renderer.hideEditSection()
    //    renderer.hidePieceBox() // unnecessary
    renderer.contractCanvas()
    renderer.drawBoard()
  }

  override def renderAll(): Unit = {
    super.renderAll()

    renderer.updateEditResetLabel(config.messageLang)

    renderer.drawIndicators(config, turn, GameStatus.Playing)
    renderer.drawEditingPieces(config, board, hand, box)
  }

  override def canActivate(cursor: Cursor): Boolean = true

  override def canSelect(cursor: Cursor): Boolean = config.flip.when[Cursor](!_)(cursor) match {
    case Cursor(Some(sq), None, None, None) => board.contains(sq)
    case Cursor(None, Some(h), None, None) => hand(h) > 0
    case Cursor(None, None, Some(pt), None) => box(pt) > 0
    case _ => false
  }

  /**
    * Exchange action in Edit Mode
    *
    * @param selected from
    * @param invoked  to
    */
  override def invokeCursor(selected: Cursor, invoked: Cursor): Option[ModeController] = {
    (config.flip.when[Cursor](!_)(selected), config.flip.when[Cursor](!_)(invoked)) match {
      // square is selected
      case (Cursor(Some(s1), None, None, None), Cursor(Some(s2), None, None, None)) =>
        (board(s1), board.get(s2)) match {
          case (p1, Some(p2)) if s1 == s2 =>
            // change piece attributes
            Some(this.copy(board = board.updated(s1, p1.canPromote.fold(p1.promoted, !p1.demoted))))
          case (p1, Some(p2)) =>
            // change pieces
            Some(this.copy(board = board.updated(s1, p2).updated(s2, p1)))
          case (p1, None) =>
            Some(this.copy(board = board.updated(s2, p1) - s1))
        }
      case (Cursor(Some(s), None, None, None), Cursor(None, Some(h), None, None)) if board(s).ptype != KING =>
        val pt = board(s).ptype.demoted
        Some(this.copy(board = board - s, hand = MapUtil.incrementMap(hand, Hand(h.owner, pt))))
      case (Cursor(Some(s), None, None, None), Cursor(None, None, Some(_), None)) =>
        val pt = board(s).ptype.demoted
        Some(this.copy(board = board - s, box = MapUtil.incrementMap(box, pt)))

      // hand is selected
      case (Cursor(None, Some(h), None, None), Cursor(Some(s), None, None, None)) if !board.get(s).exists(_.ptype == KING) =>
        val hx = MapUtil.decrementMap(hand, h)
        val hy = board.get(s).map { p => MapUtil.incrementMap(hx, Hand(h.owner, p.ptype.demoted)) }.getOrElse(hx)
        Some(this.copy(board = board.updated(s, h.toPiece), hand = hy))
      case (Cursor(None, Some(h1), None, None), Cursor(None, Some(h2), None, None)) if h1.owner != h2.owner =>
        val hx = MapUtil.decrementMap(hand, h1)
        val hy = MapUtil.incrementMap(hx, Hand(!h1.owner, h1.ptype))
        Some(this.copy(hand = hy))
      case (Cursor(None, Some(h), None, None), Cursor(None, None, Some(_), None)) =>
        Some(this.copy(hand = MapUtil.decrementMap(hand, h), box = MapUtil.incrementMap(box, h.ptype)))

      // box is selected
      case (Cursor(None, None, Some(pt), None), Cursor(Some(s), None, None, None)) =>
        val bx = MapUtil.decrementMap(box, pt)
        val by = board.get(s).map { p => MapUtil.incrementMap(bx, p.ptype.demoted) }.getOrElse(bx)
        Some(this.copy(board = board.updated(s, Piece(Player.BLACK, pt)), box = by))
      case (Cursor(None, None, Some(pt), None), Cursor(None, Some(h), None, None)) if pt != KING =>
        Some(this.copy(hand = MapUtil.incrementMap(hand, Hand(h.owner, pt)), box = MapUtil.decrementMap(box, pt)))

      // player is clicked
      case (_, Cursor(None, None, None, Some(p))) =>
        Some(this.copy(turn = p))
      case _ => None
    }
  }

  //
  // Actions
  //
  override def setMode(nextMode: Mode): Option[ModeController] = if (nextMode != Editing) {
    Try(State(turn, board, hand, None)) match {
      case Success(st) =>
        nextMode match {
          case Playing => Some(PlayModeController(renderer, config, Game(st, gameInfo = gameInfo), 0))
          case Viewing => Some(ViewModeController(renderer, config, Game(st, gameInfo = gameInfo), 0))
          case Editing => None
        }
      case Failure(e) =>
        renderer.alertEditedState(e.getMessage, config.messageLang)
        None
    }
  } else None

  override def setMessageLanguage(lang: Language): Option[ModeController] = Some(this.copy(config = config.copy(messageLang = lang)))

  override def setRecordLanguage(lang: Language): Option[ModeController] =
    Some(this.copy(config = config.copy(recordLang = lang), gameInfo = getConvertedPlayerNames(config.recordLang, lang)))

  override def setPieceLanguage(lang: Language): Option[ModeController] = Some(this.copy(config = config.copy(pieceLang = lang)))

  override def toggleFlip(): Option[ModeController] = Some(this.copy(config = config.copy(flip = !config.flip)))

  override def setEditTurn(player: Player): Option[ModeController] =
    (player != turn).option(this.copy(turn = player))

  override def setEditInitialState(initialState: State, isHandicap: Boolean): Option[ModeController] = {
    Some(this.copy(
      turn = initialState.turn,
      board = initialState.board,
      hand = initialState.hand,
      box = initialState.unusedPtypeCount,
      gameInfo = isHandicap.fold(GameInfo(Map(
        'blackName -> handicapNames((config.recordLang, BLACK)),
        'whiteName -> handicapNames((config.recordLang, WHITE))
      )), GameInfo())
    ))
  }

}
