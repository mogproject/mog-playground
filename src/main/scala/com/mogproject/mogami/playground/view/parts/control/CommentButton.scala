package com.mogproject.mogami.playground.view.parts.control

import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.bootstrap.Tooltip
import org.scalajs.dom.html.{Button, Div, TextArea}

import scalatags.JsDom.all._

/**
  *
  */
object CommentButton {
  //
  // Elements
  //
  private[this] lazy val textCommentInput: TextArea = textarea(
    cls := "form-control",
    rows := 5,
    placeholder := "Comment",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "top",
    onfocus := { () =>
      if (textCommentInput.disabled.isEmpty || !textCommentInput.disabled.get) {
        textClearButton.disabled = false
        textUpdateButton.disabled = false
      }
    }
  ).render

  private[this] lazy val textClearButton: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := s"Clear this comment",
    onclick := { () =>
      textCommentInput.value = ""
      Controller.setComment("")
      textClearButton.disabled = true
      textUpdateButton.disabled = true
      displayCommentInputTooltip("Cleared!")
    },
    "Clear"
  ).render

  private[this] lazy val textUpdateButton: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := s"Update this comment",
    onclick := { () =>
      val text = textCommentInput.value
      Controller.setComment(text)
      textUpdateButton.disabled = true
      displayCommentInputTooltip("Updated!")
    },
    "Update"
  ).render

  // Layout
  lazy val output: Div = div(
    textCommentInput,
    div(
      cls := "row",
      marginTop := 3,
      div(cls := "col-xs-4 col-lg-3", textClearButton),
      div(cls := "col-xs-offset-4 col-xs-4 col-lg-offset-6 col-lg-3", textUpdateButton)
    )
  ).render


  //
  // Tooltip
  //
  def displayCommentInputTooltip(message: String): Unit = {
    Tooltip.display(textCommentInput, message, 2000)
  }

  //
  // Controls
  //
  def setReadOnly(): Unit = {
    textCommentInput.disabled = true
    textClearButton.disabled = true
    textUpdateButton.disabled = true
  }

  def resetReadOnly(): Unit = {
    textCommentInput.disabled = false
    textClearButton.disabled = false
    textUpdateButton.disabled = false
  }

  //
  // Operations
  //
  def updateComment(text: String): Unit = {
    textCommentInput.value = text
    textClearButton.disabled = text.isEmpty || (textCommentInput.disabled.isDefined && textCommentInput.disabled.get)
  }
}
