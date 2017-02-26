package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts.EditReset
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object EditSection extends Section {
  override def initialize(): Unit = {
    EditReset.initialize()
  }

  val output: Div = div(display := "none",
    EditReset.output,
    h4("Help"),
    ul(
      li("Click on a player name to set the turn to move.")
    )
  ).render
}
