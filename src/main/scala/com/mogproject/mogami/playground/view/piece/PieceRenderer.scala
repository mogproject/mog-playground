package com.mogproject.mogami.playground.view.piece

import com.mogproject.mogami.{Hand, Piece, Square}
import org.scalajs.dom.CanvasRenderingContext2D

trait PieceRenderer {
  def drawOnBoard(ctx: CanvasRenderingContext2D, piece: Piece, square: Square): Unit

  def drawInHand(ctx: CanvasRenderingContext2D, piece: Hand, numPieces: Int): Unit

  protected def drawText(ctx: CanvasRenderingContext2D, text: String, x: Int, y: Int, rotated: Boolean, font: String, color: String): Unit = {
    ctx.font = font
    ctx.fillStyle = color

    if (rotated) {
      ctx.save()
      ctx.rotate(math.Pi)
      ctx.fillText(text, x, y)
      ctx.restore()
    } else {
      ctx.fillText(text, x, y)
    }
  }
}
