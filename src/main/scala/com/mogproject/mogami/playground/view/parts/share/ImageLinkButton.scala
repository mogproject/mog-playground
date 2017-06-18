package com.mogproject.mogami.playground.view.parts.share

import com.mogproject.mogami.playground.view.parts.common.{CopyButtonLike, DropdownMenu}
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._


object ImageLinkButton extends CopyButtonLike with ViewButtonLike {

  /**
    * definitions of image sizes
    */
  sealed abstract class ImageSize(val w: Int)

  case object Small extends ImageSize(240)

  case object Medium extends ImageSize(320)

  case object Large extends ImageSize(400)


  /**
    * Image link buttons
    */
  override protected val ident = "image-link-copy"

  override protected val labelString = "Snapshot Image"

  private[this] val sizeButton = DropdownMenu(Vector(Small, Medium, Large), 1, "Image Size", _ => updateValueWithSize())

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
    val sizeParams = s"&size=${sizeButton.getValue.w}"
    val base = baseUrl.getOrElse(getValue)
    val url = base.replaceAll("[&]size=\\d+", "") + sizeParams

    super.updateValue(url)
    updateViewUrl(url)
  }

}
