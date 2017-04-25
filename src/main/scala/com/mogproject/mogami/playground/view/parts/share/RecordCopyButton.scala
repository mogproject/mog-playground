package com.mogproject.mogami.playground.view.parts.share

import com.mogproject.mogami.playground.view.parts.common.CopyButtonLike
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object RecordCopyButton extends CopyButtonLike {
  override protected val ident = "record-copy"

  override protected val labelString = "Record URL"

  private[this] lazy val warningLabel = div(
    cls := "alert alert-warning",
    display := display.none.v,
    strong("Warning!"),
    " Comments will not be shared due to the URL length limit."
  ).render

  override lazy val output: Div = div(
    label(labelString),
    warningLabel,
    div(cls := "input-group",
      inputElem,
      div(
        cls := "input-group-btn",
        copyButton
      )
    )
  ).render

  //
  // actions
  //
  def showWarning(): Unit = warningLabel.style.display = display.block.v

  def hideWarning(): Unit = warningLabel.style.display = display.none.v
}
