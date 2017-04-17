package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.playground.view.bootstrap.{BootstrapJQuery, Tooltip}
import com.mogproject.mogami.playground.view.section._
import org.scalajs.dom.html.Div
import org.scalajs.jquery.jQuery

import scalatags.JsDom.all._

/**
  * Menu dialog
  */
object MenuDialog {

  private[this] val title = "Menu"

  private[this] val ok = "OK"

  private[this] lazy val elem: Div =
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

  private[this] var dialogElem: Option[BootstrapJQuery] = None

  private[this] def createDialog(layout: Layout): BootstrapJQuery = {
    val e = jQuery(elem)

    e.on("hidden.bs.modal", () ⇒ {
      // Hide all tooltips
      Tooltip.hideAllToolTip()
    })

    Tooltip.enableHoverToolTip(layout)

    val ret = e.asInstanceOf[BootstrapJQuery]
    dialogElem = Some(ret)
    ret
  }

  def show(layout: Layout): Unit = {
    dialogElem.getOrElse(createDialog(layout)).modal("show")

  }

  def hide(): Unit = {
    dialogElem.foreach(_.modal("hide"))
  }

}
