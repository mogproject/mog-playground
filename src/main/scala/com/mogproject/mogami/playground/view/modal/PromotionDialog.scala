package com.mogproject.mogami.playground.view.modal

import com.mogproject.mogami.Piece
import com.mogproject.mogami.playground.controller.{English, Japanese, Language}
import com.mogproject.mogami.playground.view.bootstrap.BootstrapJQuery
import com.mogproject.mogami.playground.view.piece.PieceRenderer
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html.{Canvas, Div}
import org.scalajs.jquery.jQuery

import scalatags.JsDom.all._

/**
  * Promotion dialog
  */
// todo: draw on canvas
case class PromotionDialog(lang: Language,
                           piece: Piece,
                           pieceRenderer: PieceRenderer,
                           callbackUnpromote: () => Unit,
                           callbackPromote: () => Unit
                          ) {

  private[this] val title = lang match {
    case Japanese => "成りますか?"
    case English => "Do you want to promote?"
  }

  private[this] def createCanvas: Canvas = canvas(
    widthA := pieceRenderer.layout.PIECE_WIDTH * 2,
    heightA := pieceRenderer.layout.PIECE_HEIGHT * 2,
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

  private[this] val elem: Div =
    div(cls := "modal face", tabindex := "-1", role := "dialog", data("backdrop") := "static",
      div(cls := "modal-dialog", role := "document",
        div(cls := "modal-content",
          // header
          div(cls := "modal-header",
            h4(cls := "modal-title", title)
          ),

          // footer
          div(cls := "modal-footer",
            div(cls := "row",
              div(cls := "col-xs-5 col-xs-offset-1 col-md-3 col-md-offset-3",
                button(tpe := "button", cls := "btn btn-default btn-block",
                  style := s"height: ${pieceRenderer.layout.PIECE_HEIGHT * 2}px !important",
                  data("dismiss") := "modal", onclick := callbackUnpromote,
                  canvasUnpromote
                )
              ),
              div(cls := "col-xs-5 col-md-3",
                button(tpe := "button", cls := "btn btn-default btn-block",
                  style := s"height: ${pieceRenderer.layout.PIECE_HEIGHT * 2}px !important",
                  data("dismiss") := "modal", onclick := callbackPromote,
                  canvasPromote
                )
              )
            )
          )
        )
      )
    ).render

  def show(): Unit = {
    val dialog = jQuery(elem)
    dialog.on("hidden.bs.modal", () ⇒ {
      // Remove from DOM
      dialog.remove()
    })

    pieceRenderer.drawPiece(contextUnpromote, piece, 0, 0, 2)
    pieceRenderer.drawPiece(contextPromote, piece.promoted, 0, 0, 2)
    dialog.asInstanceOf[BootstrapJQuery].modal("show")
  }
}
