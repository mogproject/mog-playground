package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.playground.view.bootstrap.{BootstrapJQuery, Tooltip}
import com.mogproject.mogami.playground.view.modal.common.ModalLike
import com.mogproject.mogami.playground.view.section._
import org.scalajs.jquery.jQuery

import scalatags.JsDom.all._

/**
  * Menu dialog
  */
object MenuDialog extends ModalLike {

  override val title = "Menu"

  override val modalBody: ElemType = div(bodyDefinition, MenuPane.output)

  override val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-4 col-xs-offset-8 col-md-3 col-md-offset-9",
        button(tpe := "button", cls := "btn btn-default btn-block", data("dismiss") := "modal", "OK")
      )
    )
  )

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
