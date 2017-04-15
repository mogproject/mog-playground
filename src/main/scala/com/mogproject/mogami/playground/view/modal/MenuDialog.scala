package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.playground.view.bootstrap.{BootstrapJQuery, Tooltip}
import com.mogproject.mogami.playground.view.section._
import org.scalajs.dom
import org.scalajs.dom.html.Div
import org.scalajs.jquery.jQuery

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
            h4(cls := "modal-title", float := "left", title),
            button(tpe := "button", cls := "close", data("dismiss") := "modal", aria.label := "Close",
              span(aria.hidden := true, raw("&times;"))
            )
          ),

          // body
          div(cls := "modal-body",
            MenuPane.output
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

  private[this] lazy val dialog = {
    val e = jQuery(elem)

    e.on("hidden.bs.modal", () ⇒ {
      // Hide all tooltips
      Tooltip.hideAllToolTip()

      // Reset scroll
      dom.window.scrollTo(0, 0)
    })

    e
  }

  def show(layout: Layout): Unit = {
    dialog.asInstanceOf[BootstrapJQuery].modal("show")
    Tooltip.enableHoverToolTip(layout)
  }

}
