package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.EventManageable
import com.mogproject.mogami.playground.view.bootstrap.Tooltip
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.html.{Div, Input, Label}
import org.scalajs.dom.raw.FileReader

import scala.util.Try
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
      dom.window.setTimeout(() => readSingleFile(Controller.loadRecord), 500)
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

  lazy val output: Div = div(
    label("Load from File"),
    div(cls := "input-group",
      marginTop := 3,
      div(cls := "input-group-btn",
        browseButton
      ),
      textElem
    )
  ).render

  def displayMessage(message: String): Unit = {
    textElem.value = message
  }


  def displayTooltip(message: String): Unit = {
    Tooltip.display(textElem, message, 2000)
  }

  private[this] def readSingleFile(callback: (String, String) => Unit): Unit = {
    val maxFileSizeKB = 20

    displayMessage("Loading...")
    val head = (inputElem.files.length >= 0).option(inputElem.files(0))
    (for {
      f <- head
    } yield {
      val r = new FileReader()
      r.onload = evt => {
        val ret: String = evt.target.asInstanceOf[FileReader].result.toString
        if (ret.length >= maxFileSizeKB * 1024) {
          abort(s"[Error] File too large. (must be <= ${maxFileSizeKB}KB)")
        } else {
          callback(f.name, ret.replace("\r", "")) // remove carriage return
          clear()
        }
      }
      val t = Try(r.readAsText(f)) // todo: @see https://github.com/polygonplanet/encoding.js
      if (t.isFailure) {
        abort("[Error] Failed to open the file.")
      }
    }).getOrElse {
      abort("[Error] Failed to select the file.")
    }
  }

  def abort(message: String): Unit = {
    displayMessage(message)
    displayTooltip("Failed!")
    clear()
  }

  def clear(): Unit = {
    browseButton.disabled = false
  }
}
