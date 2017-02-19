package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.{Game, Move, State}
import com.mogproject.mogami.playground.controller.{Configuration, Controller, Language}
import com.mogproject.mogami.util.Implicits._

import scala.scalajs.js.URIUtils.encodeURIComponent

/**
  * used for Play and View mode
  */
trait GameController extends ModeController {

  def config: Configuration

  def game: Game

  def displayPosition: Int

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
  protected val realPosition: Int = (displayPosition < 0).fold(game.moves.length, math.min(displayPosition, game.moves.length))

  protected def getLastMove: Option[Move] = (realPosition > 0).option(game.moves(realPosition - 1))

  protected def isLatestState: Boolean = realPosition == game.moves.length

  protected def getTruncatedGame: Game = isLatestState.fold(
    game,
    game.copy(moves = game.moves.take(realPosition), givenHistory = Some(game.history.take(realPosition + 1)))
  )

  protected def selectedState: State = game.history(realPosition)


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
      val mc = Some(EditModeController(renderer, config, st.turn, st.board, st.hand, st.getUnusedPtypeCount))
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

  override def setRecordLanguage(lang: Language): Option[ModeController] = Some(this.copy(config = config.copy(recordLang = lang)))

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
      case 2 => Some(this.copy(displayPosition = renderer.getSelectedIndex + 1))
      case 3 => Some(this.copy(displayPosition = -1))
      case _ => throw new IllegalArgumentException(s"Unexpected control: mode=${mode} controlType=${controlType}")
    }
  }

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
    renderer.drawPieces(config, selectedState)
    renderer.drawIndicators(config, selectedState.turn, isLatestState.fold(game.status, GameStatus.Playing))
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
    val moveParams = isLatestState.fold(List.empty, List(s"move=${realPosition}"))

    val instantGame = Game(selectedState)
    val instantGameWithLastMove =
      if (realPosition == 0)
        instantGame
      else
        Game(
          game.history(realPosition - 1),
          game.moves.slice(realPosition - 1, realPosition),
          givenHistory = Some(game.history.slice(realPosition - 1, realPosition + 1))
        )

    val snapshot = ("sfen=" + encodeURIComponent(instantGame.toSfenString)) +: configParams
    val record = (("sfen=" + encodeURIComponent(game.toSfenString)) +: configParams) ++ moveParams
    val image = "action=image" :: ("sfen=" + encodeURIComponent(instantGameWithLastMove.toSfenString)) +: configParams

    renderer.updateSnapshotUrl(s"${config.baseUrl}?${snapshot.mkString("&")}")
    renderer.updateRecordUrl(s"${config.baseUrl}?${record.mkString("&")}")
    renderer.updateImageLinkUrl(s"${config.baseUrl}?${image.mkString("&")}")
    renderer.updateSfenString(selectedState.toSfenString)
  }

}
