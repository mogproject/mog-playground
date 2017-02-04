package com.mogproject.mogami.playground.view.piece

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.Piece
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.CanvasRenderingContext2D

/**
  * English pieces
  */
case class EnglishPieceRenderer(layout: Layout) extends PieceRenderer {
  val yOffset: Int = layout.PIECE_HEIGHT * 40 / 1000

  override def drawPiece(ctx: CanvasRenderingContext2D, piece: Piece, left: Int, top: Int, scale: Int = 1): Unit = {
    val w = layout.PIECE_WIDTH * scale
    val h = layout.PIECE_HEIGHT * scale
    val color = piece.isPromoted.fold(layout.color.red, layout.color.fg)
    drawTextCenter(ctx, "☖", left, top, w, h, layout.font.pentagon(w), layout.color.fg, piece.owner.isWhite, 0, yOffset)
    drawTextCenter(ctx, piece.ptype.demoted.toEnglishSimpleName, left, top, w, h, layout.font.pieceEnglish(w), color, piece.owner.isWhite, 0, yOffset)
  }
}
