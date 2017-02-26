package com.mogproject.mogami.playground.view.parts

import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
trait ShortenButtonLike extends CopyButtonLike {
  def onClick(): Unit

  override protected val labelString = ""

  private[this] val shortenButton = button(
    cls := "btn btn-default",
    tpe := "button",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := "Create a short URL by Google URL Shortener",
    onclick := { () => onClick() },
    "Shorten URL ",
    span(cls := s"glyphicon glyphicon-arrow-right", aria.hidden := true)
  ).render

  override lazy val output: Div = div(
    div(cls := "input-group",
      marginTop := 3,
      div(cls := "input-group-btn",
        shortenButton
      ),
      inputElem,
      div(cls := "input-group-btn",
        copyButton
      )
    )
  ).render

  def updateValue(value: String, completed: Boolean): Unit = {
    updateValue(value)
    shortenButton.disabled = completed
    copyButton.disabled = !completed
  }

}
