package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.playground.controller.{English, Japanese, Language}
import com.mogproject.mogami.playground.view.modal.common.ModalLike
import org.scalajs.dom.html.Element

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Alert dialog
  */
case class AlertDialog(lang: Language, message: TypedTag[Element]) extends ModalLike {

  override def displayCloseButton: Boolean = false

  override val title: String = lang match {
    case Japanese => "確認"
    case English => "Confirmation"
  }

  override val modalBody: ElemType = div(bodyDefinition, message)

  override val modalFooter: ElemType = div(footerDefinition, div(cls := "row",
    div(cls := "col-xs-4 col-xs-offset-8 col-md-3 col-md-offset-9",
      button(tpe := "button", cls := "btn btn-default btn-block", data("dismiss") := "modal", "OK")
    )
  ))

}
