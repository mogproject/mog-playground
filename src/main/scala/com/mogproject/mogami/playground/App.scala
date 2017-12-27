package com.mogproject.mogami.playground

import com.mogproject.mogami.{Game, GamePosition}
import com.mogproject.mogami.core.state.StateCache.Implicits.DefaultStateCache
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.model.{GameControl => _, _}
import com.mogproject.mogami.frontend.view.board.{SVGCompactLayout, SVGStandardLayout}
import com.mogproject.mogami.playground.model.PlaygroundModel
import com.mogproject.mogami.playground.state.PlaygroundState
import com.mogproject.mogami.playground.view.PlaygroundView

import scala.scalajs.js.JSApp
import org.scalajs.dom
import org.scalajs.dom.html.Div

import scala.util.{Failure, Success, Try}

/**
  * entry point
  */
object App extends JSApp {
  override def main(): Unit = {
    PlaygroundSettings

    // get args
    val args = Arguments()
      .loadLocalStorage()
      .parseQueryString(dom.window.location.search)
    if (args.config.isDebug) println("Debug Mode enabled.")
    if (args.config.isDev) println("Dev Mode enabled.")

    // load game
    val game = createGameFromArgs(args)

    // update mode
    val isSnapshot = game.trunk.moves.isEmpty && game.trunk.finalAction.isEmpty && game.branches.isEmpty

    val mode = isSnapshot.fold(
      PlayMode(GameControl(game, 0, 0)),
      ViewMode(GameControl(game, args.gamePosition.branch, math.max(0, args.gamePosition.position - game.trunk.offset)))
    )

    // create model
    val model = PlaygroundModel(mode, args.config)

    // create view
    val rootElem = dom.document.getElementById("app").asInstanceOf[Div]
    val view = PlaygroundView(args.config.deviceType.isMobile, args.config.isDev, args.config.isDebug, rootElem)

    // handle special actions
    args.action match {
      case NotesAction =>
        view.drawNotes(game, args.config.recordLang)
      case ImageAction =>
        // todo: support compact layout
        val conf = if (args.config.layout == SVGCompactLayout) args.config.copy(layout = SVGStandardLayout) else args.config
        view.drawAsImage(conf, mode.getGameControl.get)
      case PlayAction =>
        // initialize state
        if (args.config.isDebug) println("Initializing...")
        PlaygroundSAM.initialize(PlaygroundModel.adapter)
        SAM.initialize(PlaygroundState(model, view))

        // hide loading message and show the main contents
        if (args.config.isDebug) println("Finished initialization.")
        rootElem.style.display = scalatags.JsDom.all.display.block.v
        dom.document.getElementById("messageWindow").textContent = ""
    }
  }

  private[this] def createGameFromArgs(args: Arguments): Game = {
    def loadGame(game: => Game): Game = Try(game) match {
      case Success(g) => g
      case Failure(e) =>
        println(s"Failed to create a game: ${e}")
        Game()
    }

    val gg: Game = ((args.usen, args.sfen) match {
      case (Some(u), _) => loadGame(Game.parseUsenString(u)) // parse USEN string
      case (_, Some(s)) => loadGame(Game.parseSfenString(s)) // parse SFEN string
      case _ => Game()
    }).copy(newGameInfo = args.gameInfo)

    // update comments
    val comments = for {
      (b, m) <- args.comments
      (pos, c) <- m
      h <- gg.getHistoryHash(GamePosition(b, pos))
    } yield h -> c
    gg.copy(newComments = comments)
  }

}
