package com.mogproject.mogami.playground

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.playground.model.PlaygroundModel
import com.mogproject.mogami.playground.state.PlaygroundState
import com.mogproject.mogami.playground.view.PlaygroundView
import org.scalajs.dom.Element

/**
  * entry point
  */
object App extends PlaygroundAppLike[PlaygroundModel, PlaygroundView, PlaygroundState] {
  PlaygroundSettings

  override def createModel(mode: Mode, config: BasePlaygroundConfiguration): PlaygroundModel = PlaygroundModel(mode, config)

  override def createView(config: BasePlaygroundConfiguration, rootElem: Element): PlaygroundView = {
    PlaygroundView(config.deviceType.isMobile, config.freeMode, config.embeddedMode, config.isDev, config.isDebug, rootElem)
  }

  override def createState(model: PlaygroundModel, view: PlaygroundView): PlaygroundState = PlaygroundState(model, view)

  override def samAdapter: (PlaygroundModel, BasePlaygroundModel) => PlaygroundModel = PlaygroundModel.adapter

}
