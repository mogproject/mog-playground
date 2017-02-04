package com.mogproject.mogami.playground.view

import com.mogproject.mogami.core._
import com.mogproject.mogami.playground.controller.{Controller, Cursor}
import org.scalajs.dom.html.Canvas
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.{CanvasRenderingContext2D, MouseEvent, TouchEvent}

/**
  *
  */
trait CursorManageable {
  // variables
  private[this] var activeCursor: Option[Cursor] = None
  private[this] var selectedCursor: Option[Cursor] = None
  private[this] var lastMoveArea: Set[Cursor] = Set.empty

  // constants
  private[this] val boxPtypes: Seq[Ptype] = Ptype.KING +: Ptype.inHand

  protected val layout: Layout

  protected val canvas2: Canvas
  protected val layer0: CanvasRenderingContext2D
  protected val layer3: CanvasRenderingContext2D


  /**
    * Convert MouseEvent to Cursor
    *
    * @return Cursor if the mouse position is inside the specific area
    */
  def getCursor(clientX: Double, clientY: Double): Option[Cursor] = {
    val rect = canvas2.getBoundingClientRect()
    val (x, y) = (clientX - rect.left, clientY - rect.top)

    (layout.board.isInside(x, y), layout.handBlack.isInside(x, y), layout.handWhite.isInside(x, y), layout.pieceBox.isInside(x, y)) match {
      case (true, _, _, _) =>
        val file = 9 - ((x - layout.board.left) / layout.PIECE_WIDTH).toInt
        val rank = 1 + ((y - layout.board.top) / layout.PIECE_HEIGHT).toInt
        Some(Cursor(Square(file, rank)))
      case (false, false, false, false) =>
        None
      case (false, false, false, true) =>
        val offset = x - layout.pieceBox.left
        val i = (offset / layout.PIECE_WIDTH).toInt
        (i <= 7 && offset % layout.PIECE_WIDTH <= layout.PIECE_WIDTH).option(Cursor(boxPtypes(i)))
      case (false, isBlack, _, _) =>
        val offset = isBlack.fold(x - layout.handBlack.left, layout.handWhite.right - x)
        val i = (offset / layout.PIECE_WIDTH).toInt
        (i <= 6 && offset % layout.PIECE_WIDTH <= layout.PIECE_WIDTH).option {
          Cursor(Piece(isBlack.fold(Player.BLACK, Player.WHITE), Ptype.inHand(i)))
        }
    }
  }

  /**
    * Convert Cursor object to Rectangle.
    */
  private[this] def cursorToRect(cursor: Cursor): Rectangle = {
    val (x, y) = cursor match {
      case Cursor(None, Some(Hand(Player.BLACK, pt)), None) =>
        (layout.handBlack.left + (pt.sortId - 1) * layout.PIECE_WIDTH, layout.handBlack.top)
      case Cursor(None, Some(Hand(Player.WHITE, pt)), None) =>
        (layout.handWhite.right - (pt.sortId - 1) * layout.PIECE_WIDTH - layout.PIECE_WIDTH, layout.handWhite.top)
      case Cursor(Some(sq), None, None) =>
        (layout.board.left + (9 - sq.file) * layout.PIECE_WIDTH, layout.board.top + (sq.rank - 1) * layout.PIECE_HEIGHT)
      case Cursor(None, None, Some(pt)) =>
        (layout.pieceBox.left + pt.sortId * layout.PIECE_WIDTH, layout.pieceBox.top)
      case _ => (0, 0) // never happens
    }
    Rectangle(x, y, layout.PIECE_WIDTH, layout.PIECE_HEIGHT)
  }

  /**
    * Draw a highlighted cursor.
    */
  def drawActiveCursor(cursor: Cursor): Unit = {
    clearActiveCursor()
    cursorToRect(cursor).draw(layer3, layout.color.cursor, -2)
    activeCursor = Some(cursor)
  }

  /**
    * Clear an active cursor.
    */
  def clearActiveCursor(): Unit = {
    activeCursor.foreach(cursorToRect(_).clear(layer3))
    activeCursor = None
  }

  /**
    * Draw the selected area.
    */
  def drawSelectedArea(cursor: Cursor): Unit = {
    cursorToRect(cursor).drawFill(layer0, layout.color.cursor, 2)
    selectedCursor = Some(cursor)
  }

  /**
    * Clear a selected area.
    */
  def clearSelectedArea(): Unit = {
    selectedCursor.foreach(cursorToRect(_).clear(layer0))
    selectedCursor = None
  }

  /**
    * Draw the last move area.
    */
  def drawLastMove(move: Option[Move]): Unit = {
    val newArea: Set[Cursor] = move match {
      case None => Set.empty
      case Some(mv) =>
        val fr = mv.from match {
          case None => Cursor(mv.player, mv.oldPtype)
          case Some(sq) => Cursor(sq)
        }
        Set(fr, Cursor(mv.to))
    }

    (lastMoveArea -- newArea).foreach(cursorToRect(_).clear(layer0))
    (newArea -- lastMoveArea).foreach(cursorToRect(_).drawFill(layer0, layout.color.light, 1))
    lastMoveArea = newArea
  }

  def clearLastMove(): Unit = drawLastMove(None)

  //
  // mouseDown
  //
  def touchStart(evt: TouchEvent): Unit = mouseDown(evt.changedTouches(0).clientX, evt.changedTouches(0).clientY)

  def mouseDown(evt: MouseEvent): Unit = mouseDown(evt.clientX, evt.clientY)

  private[this] def mouseDown(x: Double, y: Double): Unit = (selectedCursor, getCursor(x, y)) match {
    case (Some(sel), Some(invoked)) => clearSelectedArea(); Controller.invokeCursor(sel, invoked)
    case (Some(sel), None) => clearSelectedArea()
    case (None, Some(sel)) if Controller.canSelect(sel) => drawSelectedArea(sel)
    case _ => // do nothing
  }

  //
  // mouseMove
  //
  def mouseMove(evt: MouseEvent): Unit = getCursor(evt.clientX, evt.clientY) match {
    case x if x == activeCursor => // do nothing
    case x@Some(cursor) if Controller.canActivate(cursor) => drawActiveCursor(cursor)
    case _ => clearActiveCursor()
  }

}
