package com.mogproject.mogami.playground.view.piece

import com.mogproject.mogami.core.Player.BLACK
import com.mogproject.mogami.playground.view.{Layout, TextRenderer}
import com.mogproject.mogami.{Hand, Piece, Ptype, Square}
import org.scalajs.dom.CanvasRenderingContext2D

trait PieceRenderer {

  def layout: Layout

  def drawPiece(ctx: CanvasRenderingContext2D, piece: Piece, left: Int, top: Int, scale: Int = 1): Unit

  def drawOnBoard(ctx: CanvasRenderingContext2D, piece: Piece, square: Square): Unit = {
    val left = layout.board.left + layout.PIECE_WIDTH * (9 - square.file)
    val top = layout.board.top + layout.PIECE_HEIGHT * (square.rank - 1)
    drawPiece(ctx, piece, left, top)
  }

  private[this] def drawNumbers(ctx: CanvasRenderingContext2D, n: Int, left: Int, top: Int, rotated: Boolean): Unit = {
    if (n > 1) {
      TextRenderer(ctx, n.toString, layout.font.numberOfPieces, layout.color.cursor, left, top, layout.PIECE_WIDTH, layout.PIECE_HEIGHT)
        .alignRight.alignBottom.withRotate(rotated).withStroke(layout.color.stroke, layout.strokeSize).render()
    }
  }

  def drawInHand(ctx: CanvasRenderingContext2D, piece: Hand, numPieces: Int): Unit = {
    // piece type
    val (left, top) = if (piece.owner.isBlack) {
      (layout.handBlack.left + layout.PIECE_WIDTH * (piece.ptype.sortId - 1), layout.handBlack.top)
    } else {
      (layout.handWhite.left + layout.PIECE_WIDTH * (7 - piece.ptype.sortId), layout.handWhite.top)
    }
    drawPiece(ctx, piece.toPiece, left, top)

    // number of pieces
    drawNumbers(ctx, numPieces, left, top, piece.owner.isWhite)
  }

  def drawInBox(ctx: CanvasRenderingContext2D, ptype: Ptype, numPieces: Int): Unit = {
    val left = layout.pieceBox.left + layout.PIECE_WIDTH * ptype.sortId
    val top = layout.pieceBox.top
    drawPiece(ctx, Piece(BLACK, ptype), left, top)
    drawNumbers(ctx, numPieces, left, top, rotated = false)
  }
}
