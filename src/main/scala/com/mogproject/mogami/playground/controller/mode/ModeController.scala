package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami._
import com.mogproject.mogami.{Player, State}
import com.mogproject.mogami.playground.controller._
import com.mogproject.mogami.playground.io.RecordFormat
import com.mogproject.mogami.playground.view.renderer.Renderer


/**
  *
  */
trait ModeController {

  def mode: Mode

  def renderer: Renderer

  def config: Configuration

  def gameInfo: GameInfo

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

  protected val defaultNames: Map[(Language, Player), String] = Map(
    (Japanese, BLACK) -> "先手",
    (Japanese, WHITE) -> "後手",
    (English, BLACK) -> "Black",
    (English, WHITE) -> "White"
  )

  protected val handicapNames: Map[(Language, Player), String] = Map(
    (Japanese, BLACK) -> "下手",
    (Japanese, WHITE) -> "上手",
    (English, BLACK) -> "Shimote",
    (English, WHITE) -> "Uwate"
  )

  def renderAll(): Unit = {
    // clear selection
    renderer.clearSelectedArea()

    // player names
    renderer.drawPlayerNames(
      gameInfo.tags.getOrElse('blackName, defaultNames(config.recordLang, BLACK)),
      gameInfo.tags.getOrElse('whiteName, defaultNames(config.recordLang, WHITE))
    )

    // draw indexes
    renderer.drawIndexes()

    // update flip button
    renderer.updateFlip(config)

    // update language buttons
    renderer.updateMessageLang(config.messageLang)
    renderer.updateRecordLang(config.recordLang)
    renderer.updatePieceLang(config.pieceLang)

    // update flip button
    renderer.updateFlip(config)
  }

  def refreshBoard(): Unit = {
    renderer.initializeBoardRenderer(config)
    renderAll()
  }

  def renderAfterUpdatingComment(updateTextArea: Boolean): Unit = {}

  def updateConfig(config: Configuration): ModeController

  // cursor check
  def canActivate(cursor: Cursor): Boolean = false

  def canSelect(cursor: Cursor): Boolean = false

  def canInvokeWithoutSelection(cursor: Cursor): Boolean = cursor.isPlayer

  // cursor click
  def invokeCursor(selected: Cursor, cursor: Cursor): Option[ModeController] = None

  // cursor hold
  def invokeHoldEvent(cursor: Cursor): Option[ModeController] = None

  // actions
  def setMode(nextMode: Mode): Option[ModeController]

  def setMessageLanguage(lang: Language): Option[ModeController]

  def setRecordLanguage(lang: Language): Option[ModeController]

  def setPieceLanguage(lang: Language): Option[ModeController]

  def setRecord(index: Int): Option[ModeController] = None

  def setControl(controlType: Int): Option[ModeController] = None

  def setGameInfo(gameInfo: GameInfo): Option[ModeController] = None

  def setComment(text: String): Option[ModeController] = None

  def toggleFlip(): Option[ModeController] = None

  def loadRecord(fileName: String, content: String): Option[ModeController] = None

  def loadRecordText(format: RecordFormat, content: String): Option[ModeController] = None

  // actions for branch section
  def changeBranch(branchNo: BranchNo, moveOffset: Option[Int]): Option[ModeController] = None

  def deleteBranch(branchNo: BranchNo): Option[ModeController] = None

  // actions for edit mode
  def setEditTurn(turn: Player): Option[ModeController] = None

  def setEditInitialState(initialState: State, isHandicap: Boolean): Option[ModeController] = None

  // helper functions
  protected def getConvertedPlayerNames(oldLang: Language, newLang: Language): GameInfo = {
    (for {
      names <- List(defaultNames, handicapNames)
      if List((BLACK, 'blackName), (WHITE, 'whiteName)).forall { case (p, k) => gameInfo.tags.get(k).contains(names(oldLang, p)) }
    } yield {
      names
    }).headOption.map { names =>
      gameInfo.copy(tags = gameInfo.tags ++ Map('blackName -> names((newLang, BLACK)), 'whiteName -> names((newLang, WHITE))))
    }.getOrElse(gameInfo)
  }
}
