package com.mogproject.mogami.playground.view.parts

import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
trait CopyButtonLike {
  protected def ident: String

  protected def labelString: String

  private[this] lazy val inputElem = input(
    tpe := "text", id := ident, cls := "form-control", aria.label := "...", readonly := "readonly"
  ).render

  lazy val output: Div = div(
    label(labelString),
    div(cls := "input-group",
      inputElem,
      span(
        cls := "input-group-btn",
        button(cls := "btn btn-default", data("clipboard-target") := s"#${ident}", tpe := "button",
          data("toggle") := "tooltip", data("trigger") := "manual", data("placement") := "bottom",
          "Copy!"
        )
      )
    )
  ).render

  def updateValue(value: String): Unit = inputElem.value = value
}
