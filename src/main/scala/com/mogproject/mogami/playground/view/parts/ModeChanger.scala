package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.controller.mode.{Editing, Mode, Playing, Viewing}
import com.mogproject.mogami.playground.view.EventManageable
import org.scalajs.dom.html.{Anchor, Div}

import scalatags.JsDom.all._


object ModeChanger extends EventManageable {
  private[this] val anchors: Map[Mode, Anchor] = Map(
    Playing -> a(cls := "btn btn-primary thin-btn active", "Play").render,
    Viewing -> a(cls := "btn btn-primary thin-btn notActive", "View").render,
    Editing -> a(cls := "btn btn-primary thin-btn notActive", "Edit").render
  )

  val element: Div = div(cls := "input-group",
    div(id := "radioBtn", cls := "btn-group",
      anchors(Playing),
      anchors(Viewing),
      anchors(Editing)
    )
  ).render

  def initialize(): Unit = {
    anchors.foreach { case (m, e) => setClickEvent(e, () => Controller.setMode(m)) }
  }

  def updateModeChangerValue(newValue: Mode): Unit = {
    anchors.foreach { case (mode, elem) =>
      if (mode == newValue) {
        elem.classList.remove("notActive")
        elem.classList.add("active")
      } else {
        elem.classList.remove("active")
        elem.classList.add("notActive")
      }
    }
  }

}
