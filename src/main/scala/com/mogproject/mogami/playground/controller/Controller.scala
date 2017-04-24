package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.core.state.StateCache.Implicits._
import com.mogproject.mogami.playground.view.Renderer
import com.mogproject.mogami._
import com.mogproject.mogami.playground.api.google.URLShortener
import com.mogproject.mogami.playground.controller.mode._
import com.mogproject.mogami.playground.io.RecordFormat
import org.scalajs.dom.Element

import scala.util.{Failure, Success, Try}

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

    // create game
    def loadGame(game: => Game): Game = Try(game) match {
      case Success(g) => g
      case Failure(e) =>
        println(s"Failed to create a game: ${e}")
        Game()
    }

    val gg = ((args.usen, args.sfen) match {
      case (Some(u), _) => loadGame(Game.parseUsenString(u)) // parse USEN string
      case (_, Some(s)) => loadGame(Game.parseSfenString(s)) // parse SFEN string
      case _ => Game()
    }).copy(gameInfo = args.gameInfo)

    // update game info and comments
    val game = args.comments.foldLeft(gg) { case (g, (br, cmts)) =>
      g.updateBranch(br)(b => Some(b.updateComments(cmts))).getOrElse {
        println(s"Ignored comments on the invalid branch no: ${br}")
        g
      }
    }

    // create renderer
    val renderer = Renderer(elem, args.config.layout)

    // draw board (do only one in the life time)
    renderer.drawBoard()

    // update mode
    val isSnapshot = game.trunk.moves.isEmpty && game.trunk.finalAction.isEmpty && game.branches.isEmpty

    modeController = Some(isSnapshot.fold(
      PlayModeController(renderer, config, game, 0, 0),
      ViewModeController(renderer, config, game, args.gamePosition.branch, args.gamePosition.position)
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

  def canInvokeWithoutSelection(cursor: Cursor): Boolean = modeController.get.canInvokeWithoutSelection(cursor)

  def invokeCursor(selected: Cursor, invoked: Cursor): Unit = doAction(_.invokeCursor(selected, invoked), _.renderAll())

  def invokeHoldEvent(invoked: Cursor): Unit = doAction(_.invokeHoldEvent(invoked), _.renderAll())

  def processMouseUp(selected: Cursor, released: Cursor): Option[Cursor] = modeController match {
    case Some(pc: PlayModeController) => pc.processMouseUp(selected, released)
    case _ => None
  }

  // actions
  def setMode(mode: Mode): Unit = if (!modeController.exists(_.mode == mode)) doAction(_.setMode(mode), _ => {})

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

  def saveRecord(format: RecordFormat, fileName: String): Unit = modeController match {
    case Some(gc: GameController) => gc.saveRecord(format, fileName)
    case _ =>
  }

  def getRecord(format: RecordFormat): String = modeController match {
    case Some(gc: GameController) => gc.getRecord(format)
    case _ => ""
  }

  def loadRecord(fileName: String, content: String): Unit = doAction(_.loadRecord(fileName, content), _.renderAll())

  def loadRecordText(format: RecordFormat, content: String): Unit = doAction(_.loadRecordText(format, content), _.renderAll())

  // Action Section
  def setResign(): Unit = doAction({
    case pc: PlayModeController => pc.setResign()
    case _ => None
  }, _.renderAll())
}