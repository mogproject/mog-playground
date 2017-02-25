package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.Controller
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object SnapshotShortenButton extends CopyButtonLike {
  override protected val ident = "snapshot-short"

  override protected val labelString = ""

  private[this] val shortenButton = button(
    cls := "btn btn-default",
    tpe := "button",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := "Create a short URL by Google URL Shortener",
    onclick := { () => Controller.shortenSnapshotUrl() },
    "Shorten URL ",
    span(cls := s"glyphicon glyphicon-arrow-right", aria.hidden := true)
  ).render

  override lazy val output: Div = div(
    div(cls := "input-group",
      div(
        cls := "input-group-btn",
        shortenButton
      ),
      inputElem,
      div(
        cls := "input-group-btn",
        copyButton
      )
    )
  ).render

  override def updateValue(value: String): Unit = {
    super.updateValue(value)
    shortenButton.disabled = value.nonEmpty
    copyButton.disabled = value.isEmpty
  }

}
