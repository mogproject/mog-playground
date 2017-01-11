package com.mogproject.mogami.playground.view.piece

import com.mogproject.mogami.{Piece, Square, Hand}
import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.CanvasRenderingContext2D

/**
  * Japanese pieces (one character)
  */
case class SimpleJapanesePieceRenderer(layout: Layout) extends PieceRenderer {
  override def drawOnBoard(ctx: CanvasRenderingContext2D, piece: Piece, square: Square): Unit = {
    val (x, y) = if (piece.owner.isBlack) {
      (layout.board.left + layout.PIECE_WIDTH * (9 - square.file) + 3,
        layout.board.top + layout.PIECE_HEIGHT * square.rank - 6)
    } else {
      (-layout.board.left - layout.PIECE_WIDTH * (10 - square.file) + 3,
        -layout.board.top - layout.PIECE_HEIGHT * (square.rank - 1) - 7)
    }
    val col = piece.isPromoted.fold(layout.color.red, layout.color.fg)
    drawText(ctx, piece.ptype.toJapaneseSimpleName, x, y, piece.owner.isWhite, layout.font.pieceJapanese, col)
  }

  override def drawInHand(ctx: CanvasRenderingContext2D, piece: Hand, numPieces: Int): Unit = {
    if (numPieces >= 1) {
      // piece type
      val (x, y) = if (piece.owner.isBlack) {
        (layout.handBlack.left + layout.HAND_UNIT_WIDTH * (piece.ptype.sortId - 1) + 3,
          layout.handBlack.top + layout.PIECE_HEIGHT - 6)
      } else {
        (-layout.handWhite.right + layout.HAND_UNIT_WIDTH * (piece.ptype.sortId - 1) + 3,
          layout.handWhite.top - 11)
      }
      drawText(ctx, piece.ptype.toJapaneseSimpleName, x, y, piece.owner.isWhite, layout.font.pieceJapanese, layout.color.fg)

      // number of pieces
      if (numPieces > 1) {
        val xx = x + layout.PIECE_WIDTH - (if (piece.ptype.sortId == 7) 8 else 4)
        drawText(ctx, numPieces.toString, xx, y + 2, piece.owner.isWhite, layout.font.number, layout.color.fg)
      }
    }
  }
}
