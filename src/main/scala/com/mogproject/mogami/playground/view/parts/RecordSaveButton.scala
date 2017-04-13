package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.api.Clipboard
import com.mogproject.mogami.playground.api.Clipboard.Event
import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.io.{CSA, KI2, KIF, RecordFormat}
import com.mogproject.mogami.playground.view.bootstrap.Tooltip
import com.mogproject.mogami.playground.view.parts.common.DropdownMenu
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Button, Div, Input}

import scala.scalajs.js
import scalatags.JsDom.all._

/**
  *
  */
object RecordSaveButton {
  private[this] val DEFAULT_FILE_NAME = "record"

  private[this] val labelString = "Save to File / Clipboard"

  private[this] val recordNameElem: Input = input(
    tpe := "text",
    cls := "form-control",
    placeholder := "File name",
    value := DEFAULT_FILE_NAME
  ).render

  private[this] val recordFormatButton: DropdownMenu[RecordFormat] = DropdownMenu(Vector(CSA, KIF, KI2), 1, "Format", _ => ())

  private[this] val recordSaveButton: Button = button(
    cls := "btn btn-default",
    tpe := "button",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("dismiss") := "modal",
    data("original-title") := s"Save the record as a file",
    onclick := { () => Controller.saveRecord(recordFormatButton.getValue, getFileName) },
    "Save"
  ).render

  private[this] lazy val copyButton: Button = button(
    cls := "btn btn-default",
    tpe := "button",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "bottom",
    onclick := { () => copyToClipboard() },
    "Copy"
  ).render

  lazy val output: Div = div(
    label(labelString),
    div(cls := "input-group",
      recordNameElem,
      span(cls := "input-group-addon", padding := 6, "."),
      div(cls := "input-group-btn", recordFormatButton.output),
      div(
        cls := "input-group-btn",
        recordSaveButton,
        copyButton
      )
    )
  ).render

  private[this] def getFileName: String = {
    val base = if (recordNameElem.value.isEmpty) DEFAULT_FILE_NAME else recordNameElem.value
    base + "." + recordFormatButton.getValue.toString.toLowerCase()
  }

  private[this] def copyToClipboard(): Unit = {
    val r = js.Dynamic.literal()
    r.text = { _: Element => Controller.getRecord(recordFormatButton.getValue) }
    val opts = r.asInstanceOf[Clipboard.Options]

    val cp = new Clipboard(".btn", opts)
    cp.on("success", (e: Event) => Tooltip.display(e.trigger, "Copied!"))
    cp.on("error", (e: Event) => Tooltip.display(e.trigger, "Failed!"))
  }

}
