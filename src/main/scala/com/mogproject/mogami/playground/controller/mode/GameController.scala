package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami._
import com.mogproject.mogami.core.game.Game.{BranchNo, GamePosition}
import com.mogproject.mogami.core.move.IllegalMove
import com.mogproject.mogami.playground.api.google.URLShortener
import com.mogproject.mogami.playground.controller._
import com.mogproject.mogami.playground.io._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.{Game, Move, State}
import com.mogproject.mogami.core.state.StateCache.Implicits._

import scala.util.{Failure, Success, Try}

/**
  * used for Play and View mode
  */
trait GameController extends ModeController {

  def config: Configuration

  def game: Game

  /** trunk initial = 0 */
  def displayPosition: Int

  def displayBranchNo: BranchNo

  def displayBranch: Branch = game.getBranch(displayBranchNo).getOrElse(
    throw new RuntimeException(s"failed to select branch: ${displayBranchNo}")
  )

  def gamePosition: GamePosition = GamePosition(displayBranchNo, statusPosition + displayBranch.offset)

  override def gameInfo: GameInfo = game.gameInfo

  lazy val argumentsBuilder = ArgumentsBuilder(game, gamePosition, config)

  /**
    * Abstract copy method
    */
  def copy(config: Configuration = this.config,
           game: Game = this.game,
           displayBranchNo: BranchNo = this.displayBranchNo,
           displayPosition: Int = this.displayPosition): GameController

  /**
    * Initialization
    */
  override def initialize(): Unit = {
    super.initialize()
    renderAll()
  }

  //
  // helper functions
  //
  protected val lastDisplayPosition: Int = displayBranch.moves.length + (displayBranch.status match {
    case GameStatus.IllegallyMoved => 2
    case GameStatus.Playing => 0
    case _ => 1
  })

  protected val statusPosition: Int = math.min(displayPosition, game.moves.length)

  protected val lastStatusPosition: Int = displayBranch.moves.length

  protected def getLastMove: Option[Move] = (displayPosition, statusPosition, displayBranch.finalAction) match {
    case (x, _, Some(IllegalMove(mv))) if lastStatusPosition < x => Some(mv)
    case (_, 0, _) => None
    case _ => Some(game.moves(statusPosition - 1))
  }

  protected def isLastStatusPosition: Boolean =
    displayBranch.finalAction.isDefined.fold(lastStatusPosition < displayPosition, statusPosition == lastStatusPosition)

  protected def selectedState: State = game.getState(gamePosition).get


  /**
    * Change mode
    *
    * @param nextMode next mode
    */
  override def setMode(nextMode: Mode): Option[ModeController] = nextMode match {
    case Playing if mode == Viewing => Some(PlayModeController(renderer, config, game, displayBranchNo, displayPosition))
    case Viewing if mode == Playing => Some(ViewModeController(renderer, config, game, displayBranchNo, displayPosition))
    case Editing =>
      val st = selectedState
      val mc = Some(EditModeController(renderer, config, st.turn, st.board, st.hand, st.unusedPtypeCount, game.gameInfo))
      game.moves.isEmpty.fold(mc, {
        renderer.askConfirm(config.messageLang, () => Controller.update(mc))
        None
      })
    case _ => throw new RuntimeException("never happens")
  }

  /**
    * Change language settings
    *
    * @param lang language
    */
  override def setMessageLanguage(lang: Language): Option[ModeController] = Some(this.copy(config = config.copy(messageLang = lang)))

  override def setRecordLanguage(lang: Language): Option[ModeController] =
    Some(this.copy(config = config.copy(recordLang = lang), game = game.copy(gameInfo = getConvertedPlayerNames(config.recordLang, lang))))

  override def setPieceLanguage(lang: Language): Option[ModeController] = Some(this.copy(config = config.copy(pieceLang = lang)))

  /**
    * Flip the board
    *
    * @return
    */
  override def toggleFlip(): Option[ModeController] = Some(this.copy(config = config.copy(flip = !config.flip)))

  /**
    * Change the display position
    *
    * @param index display position
    */
  override def setRecord(index: Int): Option[ModeController] = Some(this.copy(displayPosition = index))

  /**
    * Change the display position by backward/forward buttons
    *
    * @param controlType 0: |<-, 1: <-, 2: ->, 3: ->|
    */
  override def setControl(controlType: Int): Option[ModeController] = {
    controlType match {
      case 0 => Some(this.copy(displayPosition = 0))
      case 1 => Some(this.copy(displayPosition = math.max(0, renderer.getSelectedIndex - 1)))
      case 2 =>
        if (statusPosition < game.moves.length) {
          val sq = game.moves(statusPosition).to
          renderer.flashCursor(Cursor(config.flip.fold(!sq, sq)))
        }
        Some(this.copy(displayPosition = renderer.getSelectedIndex + 1))
      case 3 => Some(this.copy(displayPosition = lastDisplayPosition))
      case _ => throw new IllegalArgumentException(s"Unexpected control: mode=${mode} controlType=${controlType}")
    }
  }

  /**
    * Change the game information
    *
    * @param gameInfo game info
    */
  override def setGameInfo(gameInfo: GameInfo): Option[ModeController] = Some(this.copy(game = game.copy(gameInfo = gameInfo)))

  /**
    * Set comments
    */
  override def setComment(text: String): Option[ModeController] = {
    game.updateBranch(displayBranchNo)(br => Some(br.updateComment(gamePosition.position, text))).map(g => this.copy(game = g))
  }

  //
  // renderer
  //
  override def renderAll(): Unit = {
    super.renderAll()
    renderState()
    renderControl()
    renderUrls()
    renderComment()
  }

  override def renderAfterUpdatingComment(): Unit = {
    renderControl()
    renderRecordUrls()
  }

  protected def renderState(): Unit = {
    (lastStatusPosition < displayPosition, displayBranch.finalAction) match {
      case (true, Some(IllegalMove(mv))) => renderer.drawIllegalStatePieces(config, selectedState, mv)
      case _ => renderer.drawPieces(config, selectedState)
    }

    renderer.drawIndicators(config, selectedState.turn, isLastStatusPosition.fold(game.status, GameStatus.Playing))
    renderer.drawLastMove(config, getLastMove)
  }

  protected def renderControl(): Unit = {
    // record
    renderer.updateRecordContent(game, displayBranchNo, config.recordLang)
    renderer.updateRecordIndex(displayPosition)

    // backward/forward
    val index = renderer.getRecordIndex(displayPosition)
    val canMoveBackward = 0 < index
    val canMoveForward = 0 <= displayPosition && displayPosition < renderer.getMaxRecordIndex
    renderer.updateControlBar(canMoveBackward, canMoveForward)
  }

  private[this] def renderRecordUrls(): Unit = {
    renderer.updateRecordUrl(argumentsBuilder.toRecordUrl)
    renderer.updateRecordShortUrl("", completed = false)

    renderer.updateSnapshotUrl(argumentsBuilder.toSnapshotUrl)
    renderer.updateSnapshotShortUrl("", completed = false)
  }

  protected def renderUrls(): Unit = {
    renderRecordUrls()
    renderer.updateImageLinkUrl(argumentsBuilder.toImageLinkUrl)
    renderer.updateSfenString(selectedState.toSfenString)
  }

  protected def renderComment(): Unit = {
    renderer.updateComment(game.getComment(gamePosition).getOrElse(""))
  }

  def shortenSnapshotUrl(shortener: URLShortener): Unit = {
    renderer.updateSnapshotShortUrl("creating...", completed = false)
    shortener.makeShortenedURL(renderer.getSnapshotUrl, renderer.updateSnapshotShortUrl(_, completed = true), s => {
      renderer.updateSnapshotShortUrl(s, completed = false)
    })
  }

  def shortenRecordUrl(shortener: URLShortener): Unit = {
    renderer.updateRecordShortUrl("creating...", completed = false)
    shortener.makeShortenedURL(renderer.getRecordUrl, renderer.updateRecordShortUrl(_, completed = true), s => {
      renderer.updateRecordShortUrl(s, completed = false)
    })
  }

  def getRecord(format: RecordFormat): String = format match {
    case CSA => game.toCsaString
    case KIF => game.toKifString
    case KI2 => game.toKi2String
  }

  def saveRecord(format: RecordFormat, fileName: String): Unit = FileWriter.saveTextFile(getRecord(format), fileName)

  override def loadRecord(fileName: String, content: String): Option[ModeController] = {
    val fileType = fileName.split('.').lastOption.mkString
    val result = fileType.toUpperCase match {
      case "CSA" => Try(Game.parseCsaString(content))
      case "KIF" => Try(Game.parseKifString(content))
      case "KI2" => Try(Game.parseKi2String(content))
      case _ => Failure(new RuntimeException(s"Unknown file type: ${fileType}"))
    }

    result match {
      case Success(g) =>
        renderer.displayFileLoadMessage(s"Loaded: ${fileName}")
        renderer.displayFileLoadTooltip(s"Loaded! (${g.moves.length} moves)")
        renderer.hideMenuModal(1000)
        Some(ViewModeController(this.renderer, this.config, g, 0, 0))
      case Failure(e) =>
        renderer.displayFileLoadMessage(s"Error: ${e.getMessage}")
        renderer.displayFileLoadTooltip("Failed!")
        None
    }
  }

  override def loadRecordText(format: RecordFormat, content: String): Option[ModeController] = {
    val result = format match {
      case CSA => Try(Game.parseCsaString(content))
      case KIF => Try(Game.parseKifString(content))
      case KI2 => Try(Game.parseKi2String(content))
    }

    result match {
      case Success(g) =>
        renderer.displayFileLoadMessage("")
        renderer.displayTextLoadMessage("")
        renderer.displayTextLoadTooltip(s"Loaded! (${g.moves.length} moves)")
        renderer.hideMenuModal(1000)
        Some(ViewModeController(this.renderer, this.config, g, 0, 0))
      case Failure(e) =>
        renderer.displayTextLoadMessage(s"Error: ${e.getMessage}")
        renderer.displayTextLoadTooltip("Failed!")
        None
    }
  }
}
