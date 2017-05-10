package com.mogproject.mogami.playground.view.renderer.piece

import com.mogproject.mogami.Piece
import com.mogproject.mogami.playground.view.layout.BoardLayout
import com.mogproject.mogami.playground.view.renderer.TextRenderer
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.CanvasRenderingContext2D

/**
  * English pieces
  */
case class EnglishPieceRenderer(layout: BoardLayout) extends PieceRenderer {
  val yOffset: Int = layout.PIECE_HEIGHT * 40 / 1000

  override def drawPiece(ctx: CanvasRenderingContext2D, piece: Piece, left: Int, top: Int, scale: Double = 1.0): Unit = {
    val w = (layout.PIECE_WIDTH * scale).toInt
    val h = (layout.PIECE_HEIGHT * scale).toInt
    val color = piece.isPromoted.fold(layout.color.red, layout.color.fg)

    TextRenderer(ctx, "☖", layout.font.pentagon(w), layout.color.fg, left, top, w, h)
      .alignCenter.alignMiddle.withRotate(piece.owner.isWhite).shift(0, yOffset).render()
    TextRenderer(ctx, piece.ptype.demoted.toEnglishSimpleName, layout.font.pieceEnglish(w), color, left, top, w, h)
      .alignCenter.alignMiddle.withRotate(piece.owner.isWhite).shift(0, yOffset).render()
  }
}
