package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts.common.AccordionMenu
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
trait Section {
  def initialize(): Unit = {
    accordions.foreach(_.initialize())
  }

  def output: Div = div().render

  val accordions: Seq[AccordionMenu] = Seq.empty

  def outputs: Seq[Div] = accordions.map(_.output)

  def show(): Unit = outputs.foreach(_.style.display = display.block.v)

  def hide(): Unit = outputs.foreach(_.style.display = display.none.v)

  def collapseTitle(): Unit = accordions.foreach(_.collapseTitle())

  def expandTitle(): Unit = accordions.foreach(_.expandTitle())
}
