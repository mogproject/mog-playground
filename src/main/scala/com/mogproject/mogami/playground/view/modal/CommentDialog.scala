package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.playground.controller._
import com.mogproject.mogami.playground.view.modal.common.ModalLike
import com.mogproject.mogami.playground.view.parts.control.CommentButton

import scalatags.JsDom.all._

/**
  * Game information dialog
  */
case class CommentDialog(config: Configuration, text: String) extends ModalLike {

  private[this] lazy val commentButton = CommentButton(isDisplayOnly = false, isModal = true, text = text)

  override val title: String = config.messageLang match {
    case Japanese => "コメント"
    case English => "Comment"
  }

  override val modalBody: ElemType = div(bodyDefinition, commentButton.textCommentInput)

  override val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-4", commentButton.textClearButton),
      div(cls := "col-xs-offset-4 col-xs-4", commentButton.textUpdateButton)
    )
  )

}
