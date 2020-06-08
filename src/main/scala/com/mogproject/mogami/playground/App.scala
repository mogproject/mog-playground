package com.mogproject.mogami.playground

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.playground.view.PlaygroundView
import org.scalajs.dom.Element

import scala.scalajs.js.annotation.JSExportTopLevel

/**
  * entry point
  */
@JSExportTopLevel("App")
object App extends PlaygroundAppLike {
  PlaygroundSettings

  override def createView(config: PlaygroundConfiguration, rootElem: Element): PlaygroundView = {
    PlaygroundView(config.deviceType.isMobile, config.freeMode, config.embeddedMode, config.isDev, config.isDebug, rootElem)
  }

}
