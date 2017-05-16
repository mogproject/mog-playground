package com.mogproject.mogami.playground.view.parts.analyze

import com.mogproject.mogami.playground.controller.Controller
import org.scalajs.dom.html.{Button, Div}

import scalatags.JsDom.all._

/**
  *
  */
object PointCountButton {

  private[this] val countButton: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := "Count points for this position",
    data("dismiss") := "modal",
    onclick := { () => Controller.countPoints() },
    "Count"
  ).render

  private[this] lazy val countMessage: Div = div(
    cls := "col-xs-8 col-sm-9 text-muted",
    marginTop := 6
  ).render

  lazy val output: Div = div(
    div(cls := "row",
      div(cls := "col-xs-4 col-sm-3",
        countButton
      ),
      countMessage
    )
  ).render

  //
  // messaging
  //
  def displayMessage(message: String): Unit = {
    countMessage.innerHTML = message.replace("\n", br().toString())
  }

  def clearMessage(): Unit = {
    countMessage.innerHTML = ""
  }

  def disableCountButton(): Unit = countMessage.disabled = true

  def enableCountButton(): Unit = countMessage.disabled = false
}
