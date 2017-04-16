package com.mogproject.mogami.playground.view.section

import org.scalajs.dom.html.Div
import scalatags.JsDom.all._

/**
  *
  */
object MenuPane {

  val output: Div = div(
    cls := "panel-group", id := "accordion", role := "tablist", aria.multiselectable := true,
    LanguageSection.outputs,
    GameMenuSection.outputs,
    ActionSection.outputs,
    GameHelpSection.outputs,
    EditSection.outputs,
    AboutSection.outputs
  ).render

  def initialize(): Unit = {
    LanguageSection.initialize()
    GameMenuSection.initialize()
    ActionSection.initialize()
    GameHelpSection.initialize()
    EditSection.initialize()
    AboutSection.initialize()
  }

}
