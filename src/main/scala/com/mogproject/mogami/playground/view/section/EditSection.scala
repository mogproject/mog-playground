package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts.{EditReset, EditTurn}
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object EditSection extends Section {
  override def initialize(): Unit = {
    EditTurn.initialize()
    EditReset.initialize()
  }

  val output: Div = div(display := "none",
    EditTurn.output,
    EditReset.output,
    h4("Help"),
    p("to be implemented")
  ).render
}
