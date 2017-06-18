package com.mogproject.mogami.playground.view.parts.share

import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
trait WarningLabelLike {

  protected lazy val warningLabel: Div = div(
    cls := "alert alert-warning",
    display := display.none.v,
    strong("Warning!"),
    " Comments will not be shared due to the URL length limit."
  ).render

  //
  // actions
  //
  def showWarning(): Unit = warningLabel.style.display = display.block.v

  def hideWarning(): Unit = warningLabel.style.display = display.none.v

}
