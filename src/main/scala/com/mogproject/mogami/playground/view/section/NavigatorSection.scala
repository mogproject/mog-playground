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
      div(cls := "row")(
        div(cls := "navbar-header col-md-10 col-md-offset-1", width := "100%",
          ul(cls := "nav navbar-nav",
            li(cls := "navbar-brand hidden-xs hidden-sm", "Shogi Playground"),
            li(ModeSelector.output),
            FlipButton.output,
            li(cls := "pull-right visible-xs visible-sm", MenuButton.output)
          )
        )
      )
    )
  ).render

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
