package com.mogproject.mogami.playground.view.parts

import org.scalajs.dom
import org.scalajs.dom.html.{Button, Div, Input}

import scalatags.JsDom.all._

/**
  * Create a textbox and a copy button that copies the value of the text box.
  */
trait CopyButtonLike {
  protected def ident: String

  protected def labelString: String

  protected lazy val inputElem: Input = input(
    tpe := "text", id := ident, cls := "form-control", aria.label := "...", readonly := "readonly"
  ).render

  protected lazy val copyButton: Button = button(
    cls := "btn btn-default",
    tpe := "button",
    data("clipboard-target") := s"#${ident}",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "bottom",
    onclick := { () => dom.window.setTimeout({ () => copyButton.focus() }, 0) },
    "Copy"
  ).render

  lazy val output: Div = div(
    label(labelString),
    div(cls := "input-group",
      inputElem,
      div(
        cls := "input-group-btn",
        copyButton
      )
    )
  ).render

  def updateValue(value: String): Unit = inputElem.value = value

  def getValue: String = inputElem.value
}
