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
    EditSection.outputs,
    ActionSection.outputs,
    AboutSection.outputs
  ).render

  def initialize(): Unit = {
    LanguageSection.initialize()
    GameMenuSection.initialize()
    EditSection.initialize()
    ActionSection.initialize()
    AboutSection.initialize()
  }

}
