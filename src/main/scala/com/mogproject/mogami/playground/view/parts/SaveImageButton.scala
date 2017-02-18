package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.playground.controller.{English, Language}
import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html.{Button, Canvas, Div}

import scalatags.JsDom.all._

/**
  *
  */
case class SaveImageButton(canvases: Seq[Canvas]) extends ButtonLike[Int, Button, Div] {
  override protected val keys = Seq(0, 1)

  override protected val labels: Map[Language, Seq[String]] = Map(
    English -> Seq("View", "Download")
  )

  override protected def generateInput(key: Int): Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block"
  ).render

  override protected def invoke(key: Int): Unit = {
    val data = createImage
    val elem = (key match {
      case 0 =>
        // view
        a(
          href := data,
          target := "_blank"
        )
      case 1 =>
        // download
        a(
          href := data,
          attr("download") := "snapshot.png"
        )
    }).render

    dom.document.body.appendChild(elem)
    elem.click()
    dom.document.body.removeChild(elem)
  }

  private[this] def createImage: String = {
    val c = canvases.head

    // create a hidden canvas
    val hiddenCanvas: Canvas = canvas(
      widthA := c.width,
      heightA := c.height
    ).render

    val hiddenContext: CanvasRenderingContext2D = hiddenCanvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    // set background as white
    hiddenContext.fillStyle = "#ffffff"
    hiddenContext.fillRect(0, 0, c.width, c.height)

    // copy layers
    canvases.foreach(src => hiddenContext.drawImage(src, 0, 0))

    // export image
    hiddenCanvas.toDataURL("image/png")
  }

  override val output: Div = div(
    label("Snapshot Image"),
    div(cls := "row", inputs.map(e => div(cls := "col-md-4 col-xs-6", e)))
  ).render

  override def initialize(): Unit = {
    super.initialize()
    updateLabel(English)
  }

}
