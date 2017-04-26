package com.mogproject.mogami.playground.view.parts.control

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.playground.controller.Controller
import com.mogproject.mogami.playground.view.bootstrap.Tooltip
import org.scalajs.dom.html.{Button, Div, TextArea}

import scalatags.JsDom.all._

/**
  *
  */
case class CommentButton(isDisplayOnly: Boolean, isModal: Boolean, text: String = "") {

  //
  // Elements
  //
  lazy val textCommentInput: TextArea = textarea(
    cls := "form-control input-small",
    rows := isDisplayOnly.fold(2, isModal.fold(10, 5)),
    placeholder := "Comment",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "top",
    if (isDisplayOnly) {
      readonly := true
    } else "",
    if (isDisplayOnly) {
      onclick := { () => Controller.showCommentModal() }
    } else {
      onfocus := { () =>
        textClearButton.disabled = false
        textUpdateButton.disabled = false
      }
    },
    text
  ).render

  lazy val textClearButton: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := s"Clear this comment",
    data("dismiss") := "modal",
    onclick := { () =>
      textCommentInput.value = ""
      Controller.setComment("", isModal)
      if (!isModal) {
        textClearButton.disabled = true
        textUpdateButton.disabled = true
        displayCommentInputTooltip("Cleared!")
      }
    },
    "Clear"
  ).render

  lazy val textUpdateButton: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := s"Update this comment",
    data("dismiss") := "modal",
    onclick := { () =>
      val text = textCommentInput.value
      Controller.setComment(text, isModal)
      if (!isModal) {
        textUpdateButton.disabled = true
        displayCommentInputTooltip("Updated!")
      }
    },
    "Update"
  ).render

  // Layout
  lazy val output: Div = div(
    paddingTop := "10px",
    textCommentInput,
    if (isDisplayOnly) "" else div(
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
  // Operations
  //
  def updateComment(text: String): Unit = {
    textCommentInput.value = text
    if (!isDisplayOnly) {
      textClearButton.disabled = text.isEmpty
    }
  }

  def getComment: String = textCommentInput.value
}
