package com.mogproject.mogami.playground.view.parts.share

import com.mogproject.mogami.playground.view.parts.common.CopyButtonLike
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
object NotesViewButton extends CopyButtonLike with ViewButtonLike {
  override protected val ident = "notes-view"

  override protected val labelString = "Notes View"

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
    updateViewUrl(value)
  }
}
