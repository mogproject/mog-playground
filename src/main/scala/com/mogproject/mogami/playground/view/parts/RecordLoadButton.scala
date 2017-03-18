package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.EventManageable
import com.mogproject.mogami.playground.view.bootstrap.Tooltip
import org.scalajs.dom.html.{Button, Div, Input, Label}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.raw.FileReader

import scala.util.Try
import scalatags.JsDom.all._

/**
  *
  */
object RecordLoadButton extends EventManageable {

  private[this] lazy val inputElem: Input = input(
    tpe := "file", display := "none", onchange := { () => textElem.value = inputElem.value; loadButton.disabled = false }
  ).render

  private[this] lazy val textElem: Input = input(
    tpe := "text",
    cls := "form-control",
    aria.label := "...",
    readonly := "readonly"
  ).render

  private[this] lazy val browseButton: Label = label(
    cls := "btn btn-default",
    "Browse...",
    inputElem
  ).render

  private[this] lazy val loadButton: Button = button(
    cls := "btn btn-default",
    tpe := "button",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "bottom",
    disabled := true,
    "Load"
  ).render

  lazy val output: Div = div(
    label("Load from File"),
    div(cls := "input-group",
      marginTop := 3,
      div(cls := "input-group-btn",
        browseButton
      ),
      textElem,
      div(cls := "input-group-btn",
        loadButton
      )
    )
  ).render

  def displayMessage(message: String): Unit = {
    Tooltip.display(loadButton, message, 2000)
  }

  private[this] def readSingleFile(callback: String => Unit): Unit = {
    val head = (inputElem.files.length >= 0).option(inputElem.files(0))
    (for {
      f <- head
    } yield {
      val r = new FileReader()
      r.onload = evt => {
        val ret = evt.target.asInstanceOf[FileReader].result.toString
        if (ret.length >= 10 * 1024) {
          abort("[Error] File too large. (must be <= 10KB)")
        } else {
          callback(ret)
          clear()
        }
      }
      val t = Try(r.readAsText(f))
      if (t.isFailure) {
        abort("[Error] Failed to open the file.")
      }
    }).getOrElse {
      abort("[Error] Failed to select the file.")
    }
  }

  def initialize(): Unit = {
    setClickEvent(loadButton, { () =>
      browseButton.disabled = true
      loadButton.disabled = true
      readSingleFile(Controller.loadRecord)
    })
  }

  def abort(message: String): Unit = {
    textElem.value = message
    displayMessage("Failed!")
    clear()
  }

  def clear(): Unit = {
    browseButton.disabled = false
  }
}
