package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.io.{RecordFormat, TextReader}
import com.mogproject.mogami.playground.view.EventManageable
import com.mogproject.mogami.playground.view.bootstrap.Tooltip
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.html._

import scala.util.{Failure, Success, Try}
import scalatags.JsDom.all._

/**
  *
  */
object RecordLoadButton extends EventManageable {

  private[this] lazy val inputElem: Input = input(
    tpe := "file",
    display := "none",
    onchange := { () =>
      displayMessage("Loading...")
      browseButton.disabled = true
      dom.window.setTimeout(() => readSingleFile(fileName => content => Controller.loadRecord(fileName, content)), 500)
    }
  ).render

  private[this] lazy val browseButton: Label = label(
    cls := "btn btn-default",
    onclick := { () => inputElem.value = "" },
    "Browse...",
    inputElem
  ).render

  private[this] lazy val textElem: Input = input(
    tpe := "text",
    cls := "form-control",
    aria.label := "...",
    readonly := "readonly",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "bottom"
  ).render

  private[this] lazy val textBoxElem: TextArea = textarea(
    cls := "form-control",
    rows := 5,
    placeholder := "Paste your record here.",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "top"
  ).render

  private[this] lazy val loadButton: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("dismiss") := "modal",
    data("original-title") := s"Load a record from the text area",
    onclick := { () =>
      val text = textBoxElem.value
      val format = RecordFormat.guessFormat(text)
      displayMessageRecordLoadText(s"Loading as ${format} Format...")
      loadButton.disabled = true
      dom.window.setTimeout(() => readRecordText(format, text), 500)
    },
    "Load"
  ).render

  private[this] lazy val messageArea: Div = div(
    cls := "col-md-9 col-xs-9 text-muted",
    marginTop := 6
  ).render

  lazy val output: Div = div(
    label("Load from File"),
    div(cls := "input-group",
      marginTop := 3,
      div(cls := "input-group-btn",
        browseButton
      ),
      textElem
    ),
    br(),
    label("Load from Text"),
    textBoxElem,
    br(),
    div(
      cls := "row",
      div(cls := "col-md-3 col-xs-3", loadButton),
      messageArea
    )
  ).render

  def displayMessage(message: String): Unit = {
    textElem.value = message
  }

  def displayTooltip(message: String): Unit = {
    Tooltip.display(textElem, message, 2000)
  }

  def displayMessageRecordLoadText(message: String): Unit = {
    messageArea.innerHTML = message
  }

  def displayTooltipRecordLoadText(message: String): Unit = {
    Tooltip.display(textBoxElem, message, 2000)
  }

  private[this] def readSingleFile(callback: String => String => Unit): Unit = {
    val maxFileSizeKB = 20

    val head = (inputElem.files.length >= 0).option(inputElem.files(0))
    (for {
      f <- head
    } yield {
      def sizeChecker(sz: Int): Boolean = if (sz <= maxFileSizeKB * 1024) {
        false
      } else {
        abort(s"[Error] File too large. (must be <= ${maxFileSizeKB}KB)")
        true
      }

      Try(TextReader.readTextFile(f, callback(f.name), sizeChecker)) match {
        case Success(_) => // do nothing
        case Failure(_) => abort("[Error] Failed to open the file.")
      }
    }).getOrElse {
      abort("[Error] Failed to select the file.")
    }
  }

  private[this] def readRecordText(format: RecordFormat, text: String): Unit = {
    Controller.loadRecordText(format, text)
    clearLoadText()
  }

  def abort(message: String): Unit = {
    displayMessage(message)
    displayTooltip("Failed!")
    clear()
  }

  def clear(): Unit = {
    browseButton.disabled = false
  }

  def clearLoadText(): Unit = {
    loadButton.disabled = false
  }
}
