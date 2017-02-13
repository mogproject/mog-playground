package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.{Controller, English}
import com.mogproject.mogami.playground.controller.mode.{Editing, Mode, Playing, Viewing}
import org.scalajs.dom.html.{Anchor, Div}

import scalatags.JsDom.all._


object ModeSelector extends ButtonLike[Mode, Anchor, Div] {
  override protected val keys = Seq(Playing, Viewing, Editing)

  override protected val labels = Map(
    English -> Seq("Play", "View", "Edit")
  )

  override protected def generateInput(key: Mode): Anchor = a(cls := "btn btn-primary thin-btn").render

  override protected def invoke(key: Mode): Unit = Controller.setMode(key)

  override def output: Div = div(cls := "input-group",
    div(id := "radioBtn", cls := "btn-group", inputs)
  ).render

  override def initialize(): Unit = {
    super.initialize()
    updateLabel(English)
  }

}
