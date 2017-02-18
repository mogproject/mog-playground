package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.playground.controller.{English, Japanese, Language}
import com.mogproject.mogami.playground.view.EventManageable
import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import org.scalajs.dom.html.{Div, Element}

import scalatags.JsDom.all._
import org.scalajs.jquery.jQuery

import scalatags.JsDom.TypedTag

/**
  * Yes-no dialog
  */
case class YesNoDialog(lang: Language, message: TypedTag[Element], callback: () => Unit) extends EventManageable {

  private[this] val title = lang match {
    case Japanese => "確認"
    case English => "Confirmation"
  }

  private[this] val yes = lang match {
    case Japanese => "はい"
    case English => "Yes"
  }

  private[this] val no = lang match {
    case Japanese => "いいえ"
    case English => "No"
  }

  private[this] val yesButton = button(
    tpe := "button", cls := "btn btn-primary btn-block", data("dismiss") := "modal", yes
  ).render

  private[this] val elem: Div =
    div(cls := "modal face", tabindex := "-1", role := "dialog", data("backdrop") := "static",
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
              div(cls := "col-xs-4 col-xs-offset-4 col-md-3 col-md-offset-6",
                button(tpe := "button", cls := "btn btn-default btn-block", data("dismiss") := "modal", no)
              ),
              div(cls := "col-xs-4 col-md-3",
                yesButton
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

    setModalClickEvent(yesButton, dialog, callback)

    dialog.asInstanceOf[BootstrapJQuery].modal("show")
  }
}
