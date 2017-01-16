package com.mogproject.mogami.playground.view.piece

import com.mogproject.mogami.playground.view.Layout
import com.mogproject.mogami.{Hand, Player, Piece, Ptype, Square}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.CanvasRenderingContext2D

/**
  * English pieces
  */
case class EnglishPieceRenderer(layout: Layout) extends PieceRenderer {
  private[this] def drawPiece(ctx: CanvasRenderingContext2D, piece: Piece, x: Int, y: Int, color: String): Unit = {
    drawPieceText(ctx, "☖", x, y + 2, piece.owner.isWhite, layout.font.mark, layout.color.fg, layout.PIECE_WIDTH)
    drawPieceText(ctx, piece.ptype.demoted.toEnglishSimpleName, x, y, piece.owner.isWhite, layout.font.pieceEnglish, color, layout.PIECE_WIDTH)
  }

  override def drawOnBoard(ctx: CanvasRenderingContext2D, piece: Piece, square: Square): Unit = {
    val (x, y) = if (piece.owner.isBlack) {
      (layout.board.left + layout.PIECE_WIDTH * (9 - square.file),
        layout.board.top + layout.PIECE_HEIGHT * square.rank - 7)
    } else {
      (-layout.board.left - layout.PIECE_WIDTH * (10 - square.file),
        -layout.board.top - layout.PIECE_HEIGHT * (square.rank - 1) - 8)
    }
    val col = piece.isPromoted.fold(layout.color.red, layout.color.fg)

    drawPiece(ctx, piece, x, y, col)
  }

  override def drawInHand(ctx: CanvasRenderingContext2D, piece: Hand, numPieces: Int): Unit = {
    if (numPieces >= 1) {
      // piece type
      val (x, y) = if (piece.owner.isBlack) {
        (layout.handBlack.left + layout.HAND_UNIT_WIDTH * (piece.ptype.sortId - 1),
          layout.handBlack.top + layout.PIECE_HEIGHT - 6)
      } else {
        (-layout.handWhite.right + layout.HAND_UNIT_WIDTH * (piece.ptype.sortId - 1),
          layout.handWhite.top - 11)
      }
      drawPiece(ctx, piece.toPiece, x, y, layout.color.fg)

      // number of pieces
      if (numPieces > 1) {
        val xx = x + layout.PIECE_WIDTH - (if (piece.ptype.sortId == 7) 4 else 3)
        drawText(ctx, numPieces.toString, xx, y + 2, piece.owner.isWhite, layout.font.number, layout.color.fg)
      }
    }
  }

  override def drawInBox(ctx: CanvasRenderingContext2D, ptype: Ptype): Unit = {
    val x = layout.pieceBox.left + layout.PIECE_BOX_UNIT_WIDTH * ptype.sortId
    val y = layout.pieceBox.top + layout.PIECE_HEIGHT - 6
    drawPiece(ctx, Piece(Player.BLACK, ptype), x, y, layout.color.fg)
  }
}
