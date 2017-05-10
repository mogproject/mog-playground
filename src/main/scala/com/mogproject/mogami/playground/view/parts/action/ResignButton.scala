package com.mogproject.mogami.playground.view.parts.action

import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import com.mogproject.mogami.playground.view.parts.common.EventManageable
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
    data("original-title") := "Resign this game",
    data("dismiss") := "modal",
    onclick := { () => Controller.setResign() }
  ).render

  def update(lang: Language, canResign: Boolean): Unit = {
    output.innerHTML = lang match {
      case Japanese => "投了"
      case English => "Resign"
    }
    output.disabled = !canResign
  }
}
