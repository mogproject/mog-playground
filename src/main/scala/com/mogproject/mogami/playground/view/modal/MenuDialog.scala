package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.playground.controller.{English, Japanese, Language}
import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import com.mogproject.mogami.playground.view.section.{AboutSection, EditSection, GameMenuSection, LanguageSection}
import org.scalajs.dom.html.{Div, Element}
import org.scalajs.jquery.jQuery

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Menu dialog
  */
object MenuDialog {

  private[this] val title = "Menu"

  private[this] val ok = "OK"

  private[this] val elem: Div =
    div(cls := "modal face", tabindex := "-1", role := "dialog",
      div(cls := "modal-dialog", role := "document",
        div(cls := "modal-content",
          // header
          div(cls := "modal-header",
            h4(cls := "modal-title", title)
          ),

          // body
          div(cls := "modal-body",
            LanguageSection.output,
            GameMenuSection.output,
            EditSection.output,
            AboutSection.output
          ),

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
