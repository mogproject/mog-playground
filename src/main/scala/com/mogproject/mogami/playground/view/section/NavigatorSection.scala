package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.controller.mode.{Editing, Mode, Playing, Viewing}
import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.playground.view.parts.{FlipButton, MenuButton, ModeSelector}
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  * Navbar
  */
case class NavigatorSection(layout: Layout) extends Section {
  override val output: Div = div(
    div(cls := "container", padding := 0,
      div(cls := "navbar-header",
        ul(cls := "nav navbar-nav",
          li(cls := "navbar-brand hidden-xs", "Shogi Playground"),
          li(ModeSelector.output),
          li(FlipButton.output),
          li(paddingLeft := "10px", div(cls := "visible-xs", MenuButton.output))
        )
      )
    )
  ).render

  override def initialize(): Unit = {
    super.initialize()

    ModeSelector.initialize()
    FlipButton.initialize()
    MenuButton.initialize()
  }

  def updateMode(mode: Mode): Unit = {
    ModeSelector.updateValue(mode)
    updateBackground(mode)
  }

  private[this] def updateBackground(mode: Mode): Unit = output.style.backgroundColor = mode match {
    case Playing => layout.color.bsDefault
    case Viewing => layout.color.bsInfo
    case Editing => layout.color.bsWarning
  }
}
