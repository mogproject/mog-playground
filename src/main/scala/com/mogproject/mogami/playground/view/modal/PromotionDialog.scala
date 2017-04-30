package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.Piece
import com.mogproject.mogami.playground.controller.{Configuration, English, Japanese}
import com.mogproject.mogami.playground.view.EventManageable
import com.mogproject.mogami.playground.view.modal.common.ModalLike
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html.Canvas
import org.scalajs.jquery.JQuery

import scalatags.JsDom.all._

/**
  * Promotion dialog
  */
// todo: draw on canvas
case class PromotionDialog(config: Configuration,
                           piece: Piece,
                           callbackUnpromote: () => Unit,
                           callbackPromote: () => Unit
                          ) extends EventManageable with ModalLike {
  //
  // promotion specific
  //
  private[this] val textScale: Double = 1.5

  private[this] def createCanvas: Canvas = canvas(
    widthA := config.pieceRenderer.layout.PIECE_WIDTH * textScale,
    heightA := config.pieceRenderer.layout.PIECE_HEIGHT * textScale,
    marginLeft := "auto",
    marginRight := "auto",
    left := 0,
    right := 0,
    top := 0,
    zIndex := 0
  ).render

  private[this] val canvasUnpromote: Canvas = createCanvas

  private[this] val canvasPromote: Canvas = createCanvas

  private[this] val contextUnpromote: CanvasRenderingContext2D =
    canvasUnpromote.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  private[this] val contextPromote: CanvasRenderingContext2D =
    canvasPromote.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  private[this] val buttonUnpromote = button(tpe := "button", cls := "btn btn-default btn-block",
    style := s"height: ${config.pieceRenderer.layout.PIECE_HEIGHT * textScale}px !important",
    data("dismiss") := "modal",
    canvasUnpromote
  ).render

  private[this] val buttonPromote = button(tpe := "button", cls := "btn btn-default btn-block",
    style := s"height: ${config.pieceRenderer.layout.PIECE_HEIGHT * textScale}px !important",
    data("dismiss") := "modal",
    canvasPromote
  ).render

  //
  // modal traits
  //
  override def displayCloseButton: Boolean = false

  override def isStatic: Boolean = true

  override val title: String = config.messageLang match {
    case Japanese => "成りますか?"
    case English => "Do you want to promote?"
  }

  override val modalBody: ElemType = div()

  override val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-5 col-xs-offset-1 col-md-3 col-md-offset-3", buttonUnpromote),
      div(cls := "col-xs-5 col-md-3", buttonPromote)
    )
  )

  override def initialize(dialog: JQuery): Unit = {
    setModalClickEvent(buttonUnpromote, dialog, callbackUnpromote)
    setModalClickEvent(buttonPromote, dialog, callbackPromote)

    // draw large pieces
    config.pieceRenderer.drawPiece(contextUnpromote, config.flip.when[Piece](!_)(piece), 0, 0, textScale)
    config.pieceRenderer.drawPiece(contextPromote, config.flip.when[Piece](!_)(piece.promoted), 0, 0, textScale)
  }

}
