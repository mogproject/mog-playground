package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami.{Player, State}
import com.mogproject.mogami.playground.controller.{Configuration, Cursor, Language}
import com.mogproject.mogami.playground.view.Renderer


/**
  *
  */
trait ModeController {

  def mode: Mode

  def renderer: Renderer

  def config: Configuration

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
    renderer.drawPlayerNames(config)

    // draw indexes
    renderer.drawIndexes(config)

    // update flip button
    renderer.updateFlip(config)

    // update language buttons
    renderer.updateMessageLang(config.messageLang)
    renderer.updateRecordLang(config.recordLang)
    renderer.updatePieceLang(config.pieceLang)

    // update flip button
    renderer.updateFlip(config)
  }

  // cursor check
  def canActivate(cursor: Cursor): Boolean = false

  def canSelect(cursor: Cursor): Boolean = false

  // cursor click
  def invokeCursor(selected: Cursor, cursor: Cursor): Option[ModeController] = None

  // actions
  def setMode(nextMode: Mode): Option[ModeController]

  def setMessageLanguage(lang: Language): Option[ModeController]

  def setRecordLanguage(lang: Language): Option[ModeController]

  def setPieceLanguage(lang: Language): Option[ModeController]

  def setRecord(index: Int): Option[ModeController] = None

  def setControl(controlType: Int): Option[ModeController] = None

  def toggleFlip(): Option[ModeController] = None

  // actions for edit mode
  def setEditTurn(turn: Player): Option[ModeController] = None

  def setEditInitialState(initialState: State): Option[ModeController] = None

}
