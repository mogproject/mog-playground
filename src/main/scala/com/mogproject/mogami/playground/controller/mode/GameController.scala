package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.core.Game.GameStatus.{Resigned, TimedUp}
import com.mogproject.mogami.core.GameInfo
import com.mogproject.mogami.core.move.IllegalMove
import com.mogproject.mogami.playground.api.google.URLShortener
import com.mogproject.mogami.playground.controller.{Configuration, Controller, Cursor, Language}
import com.mogproject.mogami.playground.io._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.{Game, Move, State}

import scala.scalajs.js.URIUtils.encodeURIComponent
import scala.util.{Failure, Success, Try}

/**
  * used for Play and View mode
  */
trait GameController extends ModeController {

  def config: Configuration

  def game: Game

  def displayPosition: Int

  require(displayPosition >= 0)

  override def gameInfo: GameInfo = game.gameInfo

  /**
    * Abstract copy method
    */
  def copy(config: Configuration = this.config, game: Game = this.game, displayPosition: Int = this.displayPosition): GameController

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
  protected val lastDisplayPosition: Int = game.moves.length + (game.status match {
    case GameStatus.IllegallyMoved => 2
    case GameStatus.Playing => 0
    case _ => 1
  })

  protected val statusPosition: Int = math.min(displayPosition, game.moves.length)

  protected val lastStatusPosition: Int = game.moves.length

  protected def getLastMove: Option[Move] = (displayPosition, statusPosition, game.finalAction) match {
    case (x, _, Some(IllegalMove(mv))) if lastStatusPosition < x => Some(mv)
    case (_, 0, _) => None
    case _ => Some(game.moves(statusPosition - 1))
  }

  protected def isLastStatusPosition: Boolean =
    game.finalAction.isDefined.fold(lastStatusPosition < displayPosition, statusPosition == lastStatusPosition)

  protected def getTruncatedGame: Game = (!isLastStatusPosition || Seq(Resigned, TimedUp).contains(game.status)).fold(
    game.copy(moves = game.moves.take(statusPosition), finalAction = None, givenHistory = Some(game.history.take(statusPosition + 1))),
    game
  )

  protected def selectedState: State = game.history(statusPosition)


  /**
    * Change mode
    *
    * @param nextMode next mode
    */
  override def setMode(nextMode: Mode): Option[ModeController] = nextMode match {
    case Playing if mode == Viewing => Some(PlayModeController(renderer, config, game, displayPosition))
    case Viewing if mode == Playing => Some(ViewModeController(renderer, config, game, displayPosition))
    case Editing =>
      val st = selectedState
      val mc = Some(EditModeController(renderer, config, st.turn, st.board, st.hand, st.getUnusedPtypeCount, game.gameInfo))
      game.moves.isEmpty.fold(mc, {
        renderer.askConfirm(config.messageLang, () => Controller.update(mc))
        None
      })
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
      case 1 => Some(this.copy(displayPosition = renderer.getSelectedIndex - 1))
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

  //
  // renderer
  //
  override def renderAll(): Unit = {
    super.renderAll()
    renderState()
    renderControl()
    renderUrls()
  }

  protected def renderState(): Unit = {
    (lastStatusPosition < displayPosition, game.finalAction) match {
      case (true, Some(IllegalMove(mv))) => renderer.drawIllegalStatePieces(config, selectedState, mv)
      case _ => renderer.drawPieces(config, selectedState)
    }

    renderer.drawIndicators(config, selectedState.turn, isLastStatusPosition.fold(game.status, GameStatus.Playing))
    renderer.drawLastMove(config, getLastMove)
  }

  protected def renderControl(): Unit = {
    // record
    renderer.updateRecordContent(game, config.recordLang)
    renderer.updateRecordIndex(displayPosition)

    // backward/forward
    val index = renderer.getRecordIndex(displayPosition)
    val canMoveBackward = 0 < index
    val canMoveForward = 0 <= displayPosition && displayPosition < renderer.getMaxRecordIndex
    renderer.updateControlBar(canMoveBackward, canMoveBackward, canMoveForward, canMoveForward)
  }

  protected def renderUrls(): Unit = {
    val configParams = config.toQueryParameters
    val moveParams = (statusPosition == 0).fold(List.empty, List(s"move=${statusPosition}"))
    val gameInfoParams = List(("bn", 'blackName), ("wn", 'whiteName)).flatMap { case (q, k) =>
      game.gameInfo.tags.get(k).map(s => s"${q}=${encodeURIComponent(s)}")
    }

    val instantGame = Game(selectedState)
    val (instantGameWithLastMove, moveParamsImage) =
      if (statusPosition == 0)
        (instantGame, Nil)
      else
        (Game(
          game.history(statusPosition - 1),
          game.moves.slice(statusPosition - 1, statusPosition),
          givenHistory = Some(game.history.slice(statusPosition - 1, statusPosition + 1))
        ), List("move=1"))

    val snapshot = List("sfen=" + encodeURIComponent(instantGame.toSfenString)) ++ gameInfoParams ++ configParams
    val record = List("sfen=" + encodeURIComponent(game.toSfenString)) ++ gameInfoParams ++ configParams ++ moveParams
    val image = List("action=image", "sfen=" + encodeURIComponent(instantGameWithLastMove.toSfenString)) ++ gameInfoParams ++ configParams ++ moveParamsImage

    renderer.updateSnapshotUrl(s"${config.baseUrl}?${snapshot.mkString("&")}")
    renderer.updateSnapshotShortUrl("", completed = false)
    renderer.updateRecordUrl(s"${config.baseUrl}?${record.mkString("&")}")
    renderer.updateRecordShortUrl("", completed = false)
    renderer.updateImageLinkUrl(s"${config.baseUrl}?${image.mkString("&")}")
    renderer.updateSfenString(selectedState.toSfenString)
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

  def saveRecordCsa(): Unit = FileWriter.saveTextFile(game.toCsaString, "record.csa")

  def saveRecordKif(): Unit = FileWriter.saveTextFile(game.toKifString, "record.kif")

  def saveRecordKi2(): Unit = FileWriter.saveTextFile(game.toKi2String, "record.ki2")

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
        renderer.displayMessageRecordLoad(fileName)
        renderer.displayTooltipRecordLoad(s"Loaded! (${g.moves.length} moves)")
        Some(ViewModeController(this.renderer, this.config, g, 0))
      case Failure(e) =>
        renderer.displayMessageRecordLoad(s"Error: ${e.getMessage}")
        renderer.displayTooltipRecordLoad("Failed!")

        println("hoge")
        println(s"""${content.toCharArray.toList.map(_.toInt)}""")
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
        renderer.displayMessageRecordLoadText("")
        renderer.displayTooltipRecordLoadText(s"Loaded! (${g.moves.length} moves)")
        Some(ViewModeController(this.renderer, this.config, g, 0))
      case Failure(e) =>
        renderer.displayMessageRecordLoadText(s"Error: ${e.getMessage}")
        renderer.displayTooltipRecordLoadText("Failed!")
        None
    }
  }
}
