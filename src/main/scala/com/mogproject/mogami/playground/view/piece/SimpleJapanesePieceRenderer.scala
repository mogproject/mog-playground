package com.mogproject.mogami.playground.view.piece

import com.mogproject.mogami.Piece
import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.CanvasRenderingContext2D

/**
  * Japanese pieces (one character)
  */
case class SimpleJapanesePieceRenderer(layout: Layout) extends PieceRenderer {
  override def drawPiece(ctx: CanvasRenderingContext2D, piece: Piece, left: Int, top: Int, scale: Int = 1): Unit = {
    val w = layout.PIECE_WIDTH * scale
    val h = layout.PIECE_HEIGHT * scale
    val col = piece.isPromoted.fold(layout.color.red, layout.color.fg)
    drawTextCenter(ctx, piece.ptype.toJapaneseSimpleName, left, top, w, h, layout.font.pieceJapanese(w), col, piece.owner.isWhite)
  }
}
