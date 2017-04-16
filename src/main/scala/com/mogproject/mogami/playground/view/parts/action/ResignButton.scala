package com.mogproject.mogami.playground.view.parts.action

import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import com.mogproject.mogami.playground.view.EventManageable
import org.scalajs.dom.html.Button

import scalatags.JsDom.all._

/**
  *
  */
object ResignButton extends EventManageable {
  lazy val output: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := s"Resign this game",
    data("dismiss") := "modal"
  ).render

  def initialize(): Unit = {
    setClickEvent(output, () => Controller.setResign())
  }

  def update(lang: Language, enabled: Boolean): Unit = {
    output.innerHTML = lang match {
      case Japanese => "投了"
      case English => "Resign"
    }
    output.disabled = !enabled
  }
}
