package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.playground.controller.{English, Japanese, Language}
import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import org.scalajs.dom.html.{Div, Element}
import org.scalajs.jquery.jQuery

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Alert dialog
  */
case class AlertDialog(lang: Language, message: TypedTag[Element]) {

  private[this] val title = lang match {
    case Japanese => "確認"
    case English => "Confirmation"
  }

  private[this] val ok = lang match {
    case Japanese => "OK"
    case English => "OK"
  }

  private[this] val elem: Div =
    div(cls := "modal face", tabindex := "-1", role := "dialog",
      div(cls := "modal-dialog", role := "document",
        div(cls := "modal-content",
          // header
          div(cls := "modal-header",
            h4(cls := "modal-title", title)
          ),

          // body
          div(cls := "modal-body", message),

          // footer
          div(cls := "modal-footer",
            div(cls := "row",
              div(cls := "col-xs-4 col-xs-offset-8 col-md-3 col-md-offset-9",
                button(tpe := "button", cls := "btn btn-default btn-block", data("dismiss") := "modal", ok)
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
