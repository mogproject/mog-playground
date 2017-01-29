package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.playground.controller.{Configuration, Cursor, Language}
import com.mogproject.mogami.playground.view.Renderer
import com.mogproject.mogami.{Game, Move, State}
import com.mogproject.mogami.util.Implicits._

import scala.scalajs.js.URIUtils.encodeURIComponent

/**
  * View mode
  */
case class ViewModeController(override val renderer: Renderer,
                              override val config: Configuration,
                              game: Game,
                              position: Int
                             ) extends ModeController(Viewing, renderer, config) {

  private[this] val realPosition: Int = (position < 0).fold(game.moves.length, math.min(position, game.moves.length))

  override def initialize(): Unit = {
    super.initialize()

    renderer.updateRecordContent(game, config.lang)
    renderAll()
  }

  override def terminate(): Unit = {
    super.terminate()
  }

  override def renderAll(): Unit = {
    super.renderAll()

    // draw status
    renderer.drawPieces(config.pieceRenderer, selectedState)
    renderer.drawIndicators(selectedState.turn, isLatestState.fold(game.status, GameStatus.Playing))
    renderer.drawLastMove(getLastMove)

    // update menu bar
    renderer.updateRecordIndex(position)

    // update control bar
    val canMoveBackward = game.moves.nonEmpty && position != 0
    val canMoveForward = 0 <= position && position < renderer.getMaxRecordIndex
    renderer.updateControlBar(canMoveBackward, canMoveBackward, canMoveForward, canMoveForward)

    // render URLs
    renderUrls()
  }

  private[this] def renderUrls(): Unit = {
    val configParams = config.toQueryParameters
    val moveParams = isLatestState.fold(List.empty, List(s"move=${realPosition}"))

    val snapshot = ("sfen=" + encodeURIComponent(Game(selectedState).toSfenString)) +: configParams
    val record = (("sfen=" + encodeURIComponent(game.toSfenString)) +: configParams) ++ moveParams

    renderer.updateSnapshotUrl(s"${config.baseUrl}?${snapshot.mkString("&")}")
    renderer.updateRecordUrl(s"${config.baseUrl}?${record.mkString("&")}")
  }

  private[this] def getLastMove: Option[Move] = (realPosition > 0).option(game.moves(realPosition - 1))

  private[this] def isLatestState: Boolean = realPosition == game.moves.length

  def getTruncatedGame: Game = isLatestState.fold(
    game,
    game.copy(moves = game.moves.take(realPosition), givenHistory = Some(game.history.take(realPosition + 1)))
  )

  private[this] def selectedState: State = game.history(realPosition)

  //
  // Actions
  //
  override def setMode(nextMode: Mode): Option[ModeController] = nextMode match {
    case Playing if isLatestState || renderer.askConfirm(config.lang) =>
      Some(PlayModeController(renderer, config, isLatestState.fold(game, getTruncatedGame)))
    case Viewing => None
    case Editing if game.moves.isEmpty || renderer.askConfirm(config.lang) =>
      val st = selectedState
      Some(EditModeController(renderer, config, st.turn, st.board, st.hand, st.getUnusedPtypeCount))
  }

  override def setLanguage(lang: Language): Option[ModeController] = Some(this.copy(config = config.copy(lang = lang)))

  override def setRecord(index: Int): Option[ModeController] = Some(this.copy(position = index))

  override def setControl(controlType: Int): Option[ModeController] = {
    controlType match {
      case 0 => Some(this.copy(position = 0))
      case 1 => Some(this.copy(position = renderer.getSelectedIndex - 1))
      case 2 => Some(this.copy(position = renderer.getSelectedIndex + 1))
      case 3 => Some(this.copy(position = -1))
      case _ => throw new IllegalArgumentException(s"Unexpected control: mode=${mode} controlType=${controlType}")
    }
  }
}
