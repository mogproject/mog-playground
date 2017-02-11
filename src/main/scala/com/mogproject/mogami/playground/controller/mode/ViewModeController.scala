package com.mogproject.mogami.playground.controller.mode

import com.mogproject.mogami.playground.controller.Configuration
import com.mogproject.mogami.playground.view.Renderer
import com.mogproject.mogami.Game
import com.mogproject.mogami.util.Implicits._

/**
  * View mode
  */
case class ViewModeController(renderer: Renderer,
                              config: Configuration,
                              game: Game,
                              displayPosition: Int
                             ) extends GameController {
  val mode: Mode = Viewing

  override def copy(config: Configuration, game: Game, displayPosition: Int): GameController =
    ViewModeController(renderer, config, game, displayPosition)

}
