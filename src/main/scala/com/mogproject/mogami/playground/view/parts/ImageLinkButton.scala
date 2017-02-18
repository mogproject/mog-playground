package com.mogproject.mogami.playground.view.parts

import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object ImageLinkButton extends CopyButtonLike {

  override protected val ident = "image-link-copy"

  override protected val labelString = "Snapshot Image"

  private[this] val viewButton = a(
    cls := "btn btn-default",
    tpe := "button",
    target := "_blank",
    "View"
  ).render

  override lazy val output: Div = div(
    label(labelString),
    div(cls := "input-group",
      inputElem,
      div(
        cls := "input-group-btn",
        viewButton,
        copyButton
      )
    )
  ).render

  override def updateValue(value: String): Unit = {
    super.updateValue(value)
    viewButton.href = value
  }
}
