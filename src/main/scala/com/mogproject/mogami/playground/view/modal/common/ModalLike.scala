package com.mogproject.mogami.playground.view.modal.common

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import org.scalajs.jquery.{JQuery, jQuery}

import scalatags.JsDom
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
trait ModalLike {
  def title: String

  type ElemType = TypedTag[Div]

  protected val bodyDefinition: scalatags.generic.AttrPair[Element, String] = cls := "modal-body"

  protected val footerDefinition: scalatags.generic.AttrPair[Element, String] = cls := "modal-footer"

  def modalBody: ElemType

  def modalFooter: ElemType

  def displayCloseButton: Boolean = true

  def isStatic: Boolean = false

  def initialize(dialog: JQuery): Unit = {}

  //todo: something is wrong!
  private[this] lazy val closeButton = button(tpe := "button", cls := "close", data("dismiss") := "modal", aria.label := "Close",
    span(aria.hidden := true, raw("&times;"))
  )

  lazy val elem: Div =
    div(cls := "modal face", tabindex := "-1", role := "dialog", isStatic.fold(data("backdrop") := "static", Seq.empty[JsDom.Modifier]),
      div(cls := "modal-dialog", role := "document",
        div(cls := "modal-content",
          // header
          div(cls := "modal-header",
            h4(cls := "modal-title", float := "left", title),
            displayCloseButton.fold(closeButton, Seq.empty[JsDom.Modifier])
          ),
          // body
          modalBody,

          // footer
          modalFooter

        )
      )
    ).render

  def show(): Unit = {
    val dialog = jQuery(elem)
    dialog.on("hidden.bs.modal", () ⇒ {
      // Remove from DOM
      dialog.remove()
    })

    initialize(dialog)

    dialog.asInstanceOf[BootstrapJQuery].modal("show")
  }


}
