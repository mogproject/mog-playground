package com.mogproject.mogami.playground.view.section

import org.scalajs.jquery.jQuery
import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import org.scalajs.dom.html.Div

import scala.scalajs.js
import scalatags.JsDom.all._

/**
  *
  */
object MenuPane {

  private[this] val sections = Seq(
    GameMenuSection,
    ActionSection,
    GameHelpSection,
    EditSection,
    SettingsSection,
    AboutSection
  )

  val output: Div = div(
    cls := "panel-group", id := "accordion", role := "tablist", aria.multiselectable := true,
    sections.map(_.outputs)
  ).render

  def initialize(): Unit = {
    sections.foreach(_.initialize())
  }

  def collapseMenu(): Unit = {
    jQuery(".panel-collapse").asInstanceOf[BootstrapJQuery].collapse {
      val r = js.Dynamic.literal()
      r.toggle = false
      r.parent = "#accordion" // necessary to keep group settings
      r
    }
    jQuery(".panel-collapse").asInstanceOf[BootstrapJQuery].collapse("hide")

    sections.foreach(_.collapseTitle())
  }

  def expandMenu(): Unit = {
    sections.foreach(_.expandTitle())
  }
}
