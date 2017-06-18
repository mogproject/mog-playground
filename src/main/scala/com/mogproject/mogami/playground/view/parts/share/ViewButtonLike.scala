package com.mogproject.mogami.playground.view.parts.share

import com.mogproject.mogami.playground.view.parts.common.CopyButtonLike
import org.scalajs.dom.html.Anchor

import scalatags.JsDom.all._


/**
  *
  */
trait ViewButtonLike {
  self: CopyButtonLike =>

  protected val viewButton: Anchor = a(
    cls := "btn btn-default",
    tpe := "button",
    target := "_blank",
    "View"
  ).render

  protected def updateViewUrl(url: String): Unit = viewButton.href = url
}
