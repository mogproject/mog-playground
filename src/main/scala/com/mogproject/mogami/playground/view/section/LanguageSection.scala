package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts.{MessageLanguageSelector, PieceLanguageSelector, RecordLanguageSelector}
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object LanguageSection extends Section {
  override def initialize(): Unit = {
    MessageLanguageSelector.initialize()
    RecordLanguageSelector.initialize()
    PieceLanguageSelector.initialize()
  }

  override val output: Div = div(
    h4("Language"),
    MessageLanguageSelector.output,
    RecordLanguageSelector.output,
    PieceLanguageSelector.output
  ).render

}
