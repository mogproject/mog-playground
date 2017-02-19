package com.mogproject.mogami.playground.view.section

import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object LanguageSection extends Section {
  override def initialize(): Unit = {}

  val output: Div = div(
    h4("Language")
  ).render

}
