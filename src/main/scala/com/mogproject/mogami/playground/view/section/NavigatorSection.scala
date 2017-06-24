package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.playground.controller.mode.{Editing, Mode, Playing, Viewing}
import com.mogproject.mogami.playground.view.layout.Layout
import com.mogproject.mogami.playground.view.parts.navigator.{FlipButton, MenuButton, ModeSelector}
import com.mogproject.mogami.playground.view.renderer.BoardRenderer.FlipType
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  * Navbar
  */
case class NavigatorSection(isMobile: Boolean) extends Section {
  override val output: Div = div(
    div(cls := "container", padding := 0,
      div(cls := "navbar-header",
        ul(cls := "nav navbar-nav",
          li(cls := "navbar-brand hidden-xs", "Shogi Playground"),
          li(ModeSelector.output),
          li(FlipButton.output),
          isMobile.fold(li(paddingLeft := "10px", div(MenuButton.output)), "")
        )
      )
    )
  ).render

  override def initialize(): Unit = {
    super.initialize()

    ModeSelector.initialize()
    FlipButton.initialize()
    if (isMobile) MenuButton.initialize()
  }

  def updateMode(mode: Mode): Unit = {
    ModeSelector.updateValue(mode)
    updateBackground(mode)
  }

  def updateFlip(flip: FlipType): Unit = FlipButton.updateValue(flip)

  private[this] def updateBackground(mode: Mode): Unit = output.style.backgroundColor = mode match {
    case Playing => Layout.color.bsDefault
    case Viewing => Layout.color.bsInfo
    case Editing => Layout.color.bsWarning
  }
}
