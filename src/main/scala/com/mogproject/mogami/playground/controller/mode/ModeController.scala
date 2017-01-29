package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami.{Player, State}
import com.mogproject.mogami.playground.controller.{Configuration, Cursor, Language}
import com.mogproject.mogami.playground.view.Renderer

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
}
