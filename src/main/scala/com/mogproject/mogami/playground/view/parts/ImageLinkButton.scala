package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.view.parts.common.DropdownMenu
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._


object ImageLinkButton extends CopyButtonLike {

  override protected val ident = "image-link-copy"

  override protected val labelString = "Snapshot Image"

  private[this] val sizeButton = DropdownMenu(Vector("Small", "Medium", "Large"), 1, _ => updateValueWithSize())

  private[this] val viewButton = a(
    cls := "btn btn-default",
    tpe := "button",
    target := "_blank",
    "View"
  ).render

  override lazy val output: Div = div(
    label(labelString),
    div(cls := "input-group",
      inputElem,
      div(cls := "input-group-btn", sizeButton.output),
      div(
        cls := "input-group-btn",
        viewButton,
        copyButton
      )
    )
  ).render

  override def updateValue(value: String): Unit = updateValueWithSize(Some(value))

  private[this] def updateValueWithSize(baseUrl: Option[String] = None): Unit = {
    val sizeParams = sizeButton.getValue match {
      case 0 => "&size=240"
      case 1 => "&size=320"
      case 2 => "&size=400"
      case _ => ""
    }

    val base = baseUrl.getOrElse(getValue)
    val url = base.replaceAll("[&]size=\\d+", "") + sizeParams

    super.updateValue(url)
    viewButton.href = url
  }

}
