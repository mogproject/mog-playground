package com.mogproject.mogami.playground.state

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.state.BasePlaygroundState
import com.mogproject.mogami.playground.model.PlaygroundModel
import com.mogproject.mogami.playground.view.PlaygroundView

/**
  *
  */
case class PlaygroundState(model: PlaygroundModel, view: PlaygroundView) extends BasePlaygroundState[PlaygroundModel, PlaygroundView] {
  override def adapter(m: PlaygroundModel, b: BasePlaygroundModel): PlaygroundModel = PlaygroundModel.adapter(m, b)

  override def copy(model: PlaygroundModel = model, view: PlaygroundView = view): BasePlaygroundState[PlaygroundModel, PlaygroundView] = PlaygroundState(model, view)
}