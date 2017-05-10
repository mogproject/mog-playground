package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.playground.view.bootstrap.{BootstrapJQuery, Tooltip}
import com.mogproject.mogami.playground.view.modal.common.ModalLike
import com.mogproject.mogami.playground.view.section._
import org.scalajs.dom
import org.scalajs.jquery.jQuery

import scalatags.JsDom.all._

/**
  * Menu dialog
  */
object MenuDialog extends ModalLike {

  override val title = "Menu"

  override lazy val modalBody: ElemType = div(bodyDefinition, MenuPane.output)

  override lazy val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-4 col-xs-offset-8 col-md-3 col-md-offset-9",
        button(tpe := "button", cls := "btn btn-default btn-block", data("dismiss") := "modal", "OK")
      )
    )
  )

  private[this] var dialogElem: Option[BootstrapJQuery] = None

  private[this] def createDialog(): BootstrapJQuery = {
    val e = jQuery(elem)

    e.on("hidden.bs.modal", () ⇒ {
      // Hide all tooltips
      Tooltip.hideAllToolTip()

      // Reset scroll
      dom.window.scrollTo(0, 0)
    })

    Tooltip.enableHoverToolTip(true) // assume the device is mobile

    val ret = e.asInstanceOf[BootstrapJQuery]
    dialogElem = Some(ret)
    ret
  }

  override def show(): Unit = {
    dialogElem.getOrElse(createDialog()).modal("show")
  }

  def hide(): Unit = {
    dialogElem.foreach(_.modal("hide"))
  }

}
