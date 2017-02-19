package com.mogproject.mogami.playground.view.section

import org.scalajs.dom.html.Div

/**
  *
  */
trait Section {
  def initialize(): Unit = {}

  def output: Div

  def show(): Unit = output.style.display = "block"

  def hide(): Unit = output.style.display = "none"
}
