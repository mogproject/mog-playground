package com.mogproject.mogami.playground.view.section

import org.scalajs.dom.html.Div
import scalatags.JsDom.all._

/**
  *
  */
object MenuPane {

  val output: Div = div(
    cls := "panel-group", id := "accordion", role := "tablist", aria.multiselectable := true,
    GameMenuSection.outputs,
    ActionSection.outputs,
    GameHelpSection.outputs,
    EditSection.outputs,
    SettingsSection.outputs,
    AboutSection.outputs
  ).render

  def initialize(): Unit = {
    GameMenuSection.initialize()
    ActionSection.initialize()
    GameHelpSection.initialize()
    EditSection.initialize()
    SettingsSection.initialize()
    AboutSection.initialize()
  }

}
