package com.mogproject.mogami.playground.view.parts.manage

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.io._
import com.mogproject.mogami.playground.view.bootstrap.Tooltip
import com.mogproject.mogami.playground.view.parts.common.DropdownMenu
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.html._

import scala.util.{Failure, Success, Try}
import scalatags.JsDom.all._

/**
  *
  */
object SaveLoadButton {
  // constants
  private[this] val DEFAULT_FILE_NAME = "record"

  private[this] val textLoadInputId = "textLoadInput"

  //
  // elements #1: Load from File
  //
  private[this] lazy val fileLoadInput: Input = input(
    tpe := "file",
    display := "none",
    onchange := { () =>
      displayFileLoadMessage("Loading...")
      fileLoadButton.disabled = true
      dom.window.setTimeout(() => readSingleFile(fileName => content => Controller.loadRecord(fileName, content)), 500)
    }
  ).render

  private[this] lazy val fileLoadButton: Label = label(
    cls := "btn btn-default btn-block",
    onclick := { () =>
      displayFileLoadMessage("")
      fileLoadInput.value = ""
    },
    "Browse",
    fileLoadInput
  ).render

  private[this] lazy val fileLoadMessage: Div = div(
    cls := "col-sm-9 col-xs-8 text-muted",
    marginTop := 6
  ).render

  //
  // elements #2: Load from Text
  //
  // @note `textLoadInput` area is also used for clipboard copy
  private[this] lazy val textLoadInput: TextArea = textarea(
    id := textLoadInputId,
    cls := "form-control",
    rows := 5,
    placeholder := "Paste your record here.",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "top"
  ).render

  private[this] lazy val textLoadButton: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := s"Load a record from the text area",
    onclick := { () =>
      val text = textLoadInput.value
      val format = RecordFormat.detect(text)
      displayTextLoadMessage(s"Loading as ${format} Format...")
      textLoadButton.disabled = true
      dom.window.setTimeout(() => readRecordText(format, text), 500)
    },
    "Load"
  ).render

  private[this] lazy val textLoadMessage: Div = div(
    cls := "col-sm-9 col-xs-8 text-muted",
    marginTop := 6
  ).render

  private[this] lazy val textClearButton: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := s"Clear the text area",
    onclick := { () =>
      displayTextLoadMessage("")
      textLoadInput.value = ""
      displayTextLoadTooltip("Cleared!")
    },
    "Clear"
  ).render

  //
  // elements #3: Save to File/ Clipboard
  //
  private[this] val fileSaveName: Input = input(
    tpe := "text",
    cls := "form-control",
    placeholder := "File name",
    value := DEFAULT_FILE_NAME
  ).render

  private[this] val fileSaveFormat: DropdownMenu[RecordFormat] = DropdownMenu(Vector(CSA, KIF, KI2), 1, "Format", _ => ())

  private[this] val fileSaveButton: Button = button(
    cls := "btn btn-default",
    tpe := "button",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("dismiss") := "modal",
    data("original-title") := "Save the record as a file",
    onclick := { () => Controller.saveRecord(fileSaveFormat.getValue, getFileName) },
    "Save"
  ).render

  private[this] lazy val textCopyButton: Button = button(
    cls := "btn btn-default",
    tpe := "button",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("trigger") := "manual",
    data("clipboard-target") := "#" + textLoadInputId,
    onclick := { () =>
      displayTextLoadMessage("")
      textLoadInput.value = Controller.getRecord(fileSaveFormat.getValue)
      dom.window.setTimeout(() => textCopyButton.focus(), 0)
    },
    "Copy"
  ).render

  //
  // layout
  //
  lazy val output: Div = div(
    label("Load from File"),
    div(
      cls := "row",
      marginTop := 3,
      div(cls := "col-xs-4 col-sm-3", fileLoadButton),
      fileLoadMessage
    ),
    br(),
    label("Load from Text"),
    textLoadInput,
    div(
      cls := "row",
      marginTop := 3,
      div(cls := "col-xs-4 col-sm-3", textLoadButton),
      textLoadMessage
    ),
    div(
      cls := "row",
      marginTop := 3,
      div(cls := "col-xs-4 col-sm-3", textClearButton)
    ),
    br(),
    label("Save to File / Clipboard"),
    div(cls := "input-group",
      fileSaveName,
      span(cls := "input-group-addon", padding := 6, "."),
      div(cls := "input-group-btn", fileSaveFormat.output),
      div(
        cls := "input-group-btn",
        fileSaveButton,
        textCopyButton
      )
    )
  ).render


  //
  // File I/O
  //
  private[this] def readSingleFile(callback: String => String => Unit): Unit = {
    val maxFileSizeKB = 100

    val head = (fileLoadInput.files.length >= 0).option(fileLoadInput.files(0))
    (for {
      f <- head
    } yield {
      def sizeChecker(sz: Int): Boolean = if (sz <= maxFileSizeKB * 1024) {
        false
      } else {
        abortFileLoad(s"[Error] File too large. (must be <= ${maxFileSizeKB}KB)")
        true
      }

      Try(TextReader.readTextFile(f, callback(f.name), sizeChecker)) match {
        case Success(_) => // do nothing
        case Failure(_) => abortFileLoad("[Error] Failed to open the file.")
      }
    }).getOrElse {
      abortFileLoad("[Error] Failed to select the file.")
    }
  }

  private[this] def readRecordText(format: RecordFormat, text: String): Unit = {
    Controller.loadRecordText(format, text)
    clearTextLoad()
  }

  //
  // messaging
  //
  def displayFileLoadMessage(message: String): Unit = {
    fileLoadMessage.innerHTML = message
  }

  def displayFileLoadTooltip(message: String): Unit = {
    Tooltip.display(fileLoadButton, message, 2000)
  }

  def displayTextLoadMessage(message: String): Unit = {
    textLoadMessage.innerHTML = message
  }

  def displayTextLoadTooltip(message: String): Unit = {
    Tooltip.display(textLoadInput, message, 2000)
  }

  def abortFileLoad(message: String): Unit = {
    displayFileLoadMessage(message)
    displayFileLoadTooltip("Failed!")
    clearFileLoad()
  }

  def clearFileLoad(): Unit = {
    fileLoadButton.disabled = false
  }

  def clearTextLoad(): Unit = {
    textLoadButton.disabled = false
  }

  //
  // helper functions
  //
  private[this] def getFileName: String = {
    val base = if (fileSaveName.value.isEmpty) DEFAULT_FILE_NAME else fileSaveName.value
    base + "." + fileSaveFormat.getValue.toString.toLowerCase()
  }
}
