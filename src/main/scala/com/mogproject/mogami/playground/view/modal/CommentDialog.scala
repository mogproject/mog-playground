package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.playground.controller._
import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import com.mogproject.mogami.playground.view.parts.control.CommentButton
import org.scalajs.dom.html.Div
import org.scalajs.jquery.jQuery

import scalatags.JsDom.all._

/**
  * Game information dialog
  */
case class CommentDialog(config: Configuration, text: String) {

  lazy val commentButton = CommentButton(isDisplayOnly = false, isModal = true, text = text)

  private[this] val title = config.messageLang match {
    case Japanese => "コメント"
    case English => "Comment"
  }

  private[this] val elem: Div =
    div(cls := "modal face", tabindex := "-1", role := "dialog",
      div(cls := "modal-dialog", role := "document",
        div(cls := "modal-content",
          // header
          div(cls := "modal-header",
            h4(cls := "modal-title", float := "left", title),
            button(tpe := "button", cls := "close", data("dismiss") := "modal", aria.label := "Close",
              span(aria.hidden := true, raw("&times;"))
            )
          ),
          form(
            // body
            div(cls := "modal-body",
              commentButton.textCommentInput
            ),

            // footer
            div(cls := "modal-footer",
              div(cls := "row",
                div(cls := "col-xs-4 col-lg-3", commentButton.textClearButton),
                div(cls := "col-xs-offset-4 col-xs-4 col-lg-offset-6 col-lg-3", commentButton.textUpdateButton)
              )
            )
          )
        )
      )
    ).render

  def show(): Unit = {
    val dialog = jQuery(elem)
    dialog.on("hidden.bs.modal", () ⇒ {
      // Remove from DOM
      dialog.remove()
    })
    dialog.asInstanceOf[BootstrapJQuery].modal("show")
  }
}
