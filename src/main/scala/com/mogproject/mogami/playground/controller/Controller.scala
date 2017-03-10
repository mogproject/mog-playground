package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.playground.view.Renderer
import com.mogproject.mogami._
import com.mogproject.mogami.core.GameInfo
import com.mogproject.mogami.playground.api.google.URLShortener
import com.mogproject.mogami.playground.controller.mode._
import org.scalajs.dom.Element

/**
  * logic controller
  */
object Controller {

  private[this] val urlShortener = URLShortener()

  // mutable reference
  private[this] var modeController: Option[ModeController] = None

  /**
    * Initialize the game and renderer
    *
    * @param elem parent HTML element
    * @param args Arguments instance
    */
  def initialize(elem: Element, args: Arguments): Unit = {
    val config = args.config
    val game = args.game

    // create renderer
    val renderer = Renderer(elem, args.config.layout)

    // draw board (do only one in the life time)
    renderer.drawBoard()

    // update mode
    modeController = Some(game.moves.nonEmpty.fold(
      ViewModeController(renderer, config, game, args.currentMove),
      PlayModeController(renderer, config, game, -1)
    ))

    // render all parts
    modeController.get.initialize()

    // create image if the action is ImageAction
    if (args.action == ImageAction) renderer.drawAsImage()
  }

  /**
    * Update mode controller and execute a call back method
    */
  private[this] def doAction(f: => ModeController => Option[ModeController], callback: ModeController => Unit): Unit = {
    val old = modeController
    modeController.flatMap(f) foreach { mc =>
      if (old.exists(_.mode != mc.mode)) {
        // mode change
        old.foreach(_.terminate())
        mc.initialize()
      } else {
        callback(mc)
      }
      modeController = Some(mc)
    }
  }

  /**
    * Update mode controller and render all components
    *
    * @param mc Option of the mode controller
    */
  def update(mc: Option[ModeController], callback: ModeController => Unit = _.renderAll()): Unit = {
    mc.foreach { m =>
      if (modeController.exists(_.mode != m.mode)) {
        // mode change
        modeController.foreach(_.terminate())
        m.initialize()
      } else {
        callback(m)
      }
      modeController = Some(m)
    }
  }

  // events
  def canActivate(cursor: Cursor): Boolean = modeController.get.canActivate(cursor)

  def canSelect(selected: Cursor): Boolean = modeController.get.canSelect(selected)

  def invokeCursor(selected: Cursor, invoked: Cursor): Unit = doAction(_.invokeCursor(selected, invoked), _.renderAll())

  // actions
  def setMode(mode: Mode): Unit = doAction(_.setMode(mode), _ => {})

  def setMessageLanguage(lang: Language): Unit = doAction(_.setMessageLanguage(lang), _.renderAll())

  def setRecordLanguage(lang: Language): Unit = doAction(_.setRecordLanguage(lang), _.renderAll())

  def setPieceLanguage(lang: Language): Unit = doAction(_.setPieceLanguage(lang), _.renderAll())

  def setRecord(index: Int): Unit = doAction(_.setRecord(index), _.renderAll())

  def setControl(controlType: Int): Unit = update(modeController.flatMap(_.setControl(controlType)))

  def setEditTurn(player: Player): Unit = doAction(_.setEditTurn(player), _.renderAll())

  def setEditInitialState(initialState: State, isHandicap: Boolean): Unit =
    doAction(_.setEditInitialState(initialState, isHandicap), _.renderAll())

  def setGameInfo(gameInfo: GameInfo): Unit = doAction(_.setGameInfo(gameInfo), _.renderAll())

  def toggleFlip(): Unit = doAction(_.toggleFlip(), _.renderAll())

  def showMenu(): Unit = modeController.get.renderer.showMenuModal()

  def shortenSnapshotUrl(): Unit = modeController match {
    case Some(gc: GameController) => gc.shortenSnapshotUrl(urlShortener)
    case _ =>
  }

  def shortenRecordUrl(): Unit = modeController match {
    case Some(gc: GameController) => gc.shortenRecordUrl(urlShortener)
    case _ =>
  }

  def saveRecordCsa(): Unit = modeController match {
    case Some(gc: GameController) => gc.saveRecordCsa()
    case _ =>
  }

  def saveRecordKif(): Unit = modeController match {
    case Some(gc: GameController) => gc.saveRecordKif()
    case _ =>
  }

  def saveRecordKi2(): Unit = modeController match {
    case Some(gc: GameController) => gc.saveRecordKi2()
    case _ =>
  }
}