package com.mogproject.mogami.playground.view.piece

import com.mogproject.mogami.{Hand, Piece, Ptype, Square}
import org.scalajs.dom.CanvasRenderingContext2D

trait PieceRenderer {
  def drawOnBoard(ctx: CanvasRenderingContext2D, piece: Piece, square: Square): Unit

  def drawInHand(ctx: CanvasRenderingContext2D, piece: Hand, numPieces: Int): Unit

  def drawInBox(ctx: CanvasRenderingContext2D, ptype: Ptype): Unit

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

  protected def drawPieceText(ctx: CanvasRenderingContext2D, text: String, x: Int, y: Int, rotated: Boolean, font: String, color: String, pieceWidth: Int): Unit = {
    ctx.font = font

    val textWidth = ctx.measureText(text).width.toInt
    drawText(ctx, text, x + (pieceWidth - textWidth) / 2, y, rotated, font, color)
  }
}
