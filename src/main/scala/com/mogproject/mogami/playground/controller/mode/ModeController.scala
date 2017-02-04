package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami.{Game, Player, State}
import com.mogproject.mogami.playground.controller.{Configuration, Cursor, Language}
import com.mogproject.mogami.playground.view.Renderer
import com.mogproject.mogami.util.Implicits._

import scala.scalajs.js.URIUtils.encodeURIComponent

/**
  *
  */
abstract class ModeController(val mode: Mode, val renderer: Renderer, val config: Configuration) {

  // rendering
  /**
    * Initialization (executed when the mode changes)
    */
  def initialize(): Unit = {
    renderer.updateMode(mode)
  }

  /**
    * Termination (executed when the mode changes)
    */
  def terminate(): Unit = {
    // clear cursors
    renderer.clearActiveCursor()
    renderer.clearSelectedArea()
    renderer.clearLastMove()
  }

  def renderAll(): Unit = {
    // clear selection
    renderer.clearSelectedArea()

    // player names
    renderer.drawPlayerNames(config.lang)

    // draw indexes
    renderer.drawIndexes(config.lang)

    // update menu bar
    renderer.updateLang(config.lang)
  }

  // cursor check
  def canActivate(cursor: Cursor): Boolean = false

  def canSelect(cursor: Cursor): Boolean = false

  // cursor click
  def invokeCursor(selected: Cursor, cursor: Cursor): Option[ModeController] = None

  // actions
  def setMode(nextMode: Mode): Option[ModeController]

  def setLanguage(lang: Language): Option[ModeController]

  def setRecord(index: Int): Option[ModeController] = None

  def setControl(controlType: Int): Option[ModeController] = None

  // actions for edit mode
  def setEditTurn(turn: Player): Option[ModeController] = None

  def setEditInitialState(initialState: State): Option[ModeController] = None

  // helper methods
  protected def renderUrls(game: Game, position: Int = -1): Unit = {
    val st = (position < 0).fold(game.currentState, game.history(position))
    val isLatestState = position < 0 || position == game.moves.length

    val configParams = config.toQueryParameters
    val moveParams = isLatestState.fold(List.empty, List(s"move=${position}"))

    val snapshot = ("sfen=" + encodeURIComponent(Game(st).toSfenString)) +: configParams
    val record = (("sfen=" + encodeURIComponent(game.toSfenString)) +: configParams) ++ moveParams

    renderer.updateSnapshotUrl(s"${config.baseUrl}?${snapshot.mkString("&")}")
    renderer.updateRecordUrl(s"${config.baseUrl}?${record.mkString("&")}")
  }

}
