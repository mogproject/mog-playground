package com.mogproject.mogami.playground.view

import com.mogproject.mogami._
import com.mogproject.mogami.playground.controller.{Configuration, Controller, Cursor}
import org.scalajs.dom.html.Canvas
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, MouseEvent, TouchEvent}

/**
  *
  */
trait CursorManageable {
  // variables
  private[this] var activeCursor: Option[Cursor] = None
  private[this] var selectedCursor: Option[Cursor] = None

  // constants
  private[this] val boxPtypes: Seq[Ptype] = Ptype.KING +: Ptype.inHand

  protected val layout: Layout

  protected val canvas2: Canvas
  protected val layer0: CanvasRenderingContext2D
  protected val layer3: CanvasRenderingContext2D
  protected val layer4: CanvasRenderingContext2D


  /**
    * Convert MouseEvent to Cursor
    *
    * @return Cursor if the mouse position is inside the specific area
    */
  def getCursor(clientX: Double, clientY: Double): Option[Cursor] = {
    // todo: refactor
    // flip the cursor here when config.flip=true

    val rect = canvas2.getBoundingClientRect()
    val (x, y) = (clientX - rect.left, clientY - rect.top)

    if (layout.board.isInside(x, y)) {
      val file = 9 - ((x - layout.board.left) / layout.PIECE_WIDTH).toInt
      val rank = 1 + ((y - layout.board.top) / layout.PIECE_HEIGHT).toInt
      Some(Cursor(Square(file, rank)))
    } else if (layout.handBlack.isInside(x, y)) {
      getCursorHand(x, isBlack = true)
    } else if (layout.handWhite.isInside(x, y)) {
      getCursorHand(x, isBlack = false)
    } else if (layout.playerBlack.isInside(x, y)) {
      Some(Cursor(Player.BLACK))
    } else if (layout.playerWhite.isInside(x, y)) {
      Some(Cursor(Player.WHITE))
    } else if (layout.pieceBox.isInside(x, y)) {
      val offset = x - layout.pieceBox.left
      val i = (offset / layout.PIECE_WIDTH).toInt
      (i <= 7 && offset % layout.PIECE_WIDTH <= layout.PIECE_WIDTH).option(Cursor(boxPtypes(i)))
    } else {
      None
    }
  }

  private[this] def getCursorHand(x: Double, isBlack: Boolean): Option[Cursor] = {
    val offset = isBlack.fold(x - layout.handBlack.left, layout.handWhite.right - x)
    val i = (offset / layout.HAND_PIECE_WIDTH).toInt
    (i <= 6 && offset % layout.HAND_PIECE_WIDTH <= layout.HAND_PIECE_WIDTH).option {
      Cursor(Piece(isBlack.fold(Player.BLACK, Player.WHITE), Ptype.inHand(i)))
    }
  }

  /**
    * Convert Cursor object to Rectangle.
    */
  private[this] def cursorToRect(cursor: Cursor, isFlipped: Boolean = false): Rectangle = {
    isFlipped.when[Cursor](!_)(cursor) match {
      case Cursor(None, Some(Hand(Player.BLACK, pt)), None, None) =>
        Rectangle(
          layout.handBlack.left + (pt.sortId - 1) * layout.HAND_PIECE_WIDTH,
          layout.handBlack.top,
          layout.HAND_PIECE_WIDTH,
          layout.HAND_PIECE_HEIGHT
        )
      case Cursor(None, Some(Hand(Player.WHITE, pt)), None, None) =>
        Rectangle(
          layout.handWhite.right - (pt.sortId - 1) * layout.HAND_PIECE_WIDTH - layout.HAND_PIECE_WIDTH,
          layout.handWhite.top,
          layout.HAND_PIECE_WIDTH,
          layout.HAND_PIECE_HEIGHT
        )
      case Cursor(Some(sq), None, None, None) =>
        Rectangle(
          layout.board.left + (9 - sq.file) * layout.PIECE_WIDTH,
          layout.board.top + (sq.rank - 1) * layout.PIECE_HEIGHT,
          layout.PIECE_WIDTH,
          layout.PIECE_HEIGHT
        )
      case Cursor(None, None, Some(pt), None) =>
        Rectangle(
          layout.pieceBox.left + pt.sortId * layout.PIECE_WIDTH,
          layout.pieceBox.top,
          layout.PIECE_WIDTH,
          layout.PIECE_HEIGHT
        )
      case Cursor(None, None, None, Some(Player.BLACK)) => layout.playerBlack
      case Cursor(None, None, None, Some(Player.WHITE)) => layout.playerWhite
      case _ => Rectangle(0, 0, layout.PIECE_WIDTH, layout.PIECE_HEIGHT) // never happens
    }
  }

  /**
    * Draw a highlighted cursor.
    */
  def drawActiveCursor(cursor: Cursor): Unit = {
    clearActiveCursor()
    cursorToRect(cursor).draw(layer3, layout.color.cursor, -2)
    activeCursor = Some(cursor)
  }

  def flashCursor(cursor: Cursor): Unit = {
    val c = cursorToRect(cursor)
    c.draw(layer4, layout.color.flash, -2)
    val f = () => c.clear(layer4)
    dom.window.setTimeout(f, 300)
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
  def drawLastMove(config: Configuration, move: Option[Move]): Unit = {
    val newArea: Set[Cursor] = move match {
      case None => Set.empty
      case Some(mv) =>
        val fr = mv.from match {
          case None => Cursor(mv.player, mv.oldPtype)
          case Some(sq) => Cursor(sq)
        }
        Set(fr, Cursor(mv.to))
    }

    clearLastMove()
    newArea.foreach(a => cursorToRect(a, config.flip).drawFill(layer0, layout.color.light, 1))
  }

  def clearLastMove(): Unit = {
    layout.board.clear(layer0)
    layout.handWhite.clear(layer0)
    layout.handBlack.clear(layer0)
  }

  //
  // mouseDown
  //
  def touchStart(evt: TouchEvent): Unit = {
    if (evt.changedTouches.length == 1) {
      evt.preventDefault()
      mouseDown(evt.changedTouches(0).clientX, evt.changedTouches(0).clientY)
    }
  }

  /**
    * Detect the left click. (button == 0)
    */
  def mouseDown(evt: MouseEvent): Unit = if (evt.button == 0) mouseDown(evt.clientX, evt.clientY)

  private[this] def mouseDown(x: Double, y: Double): Unit = mouseDown(getCursor(x, y))

  private[this] def mouseDown(cursor: Option[Cursor]): Unit = {
    cursor.foreach(c => if (Controller.canActivate(c)) flashCursor(c))
    (selectedCursor, cursor) match {
      case (_, Some(invoked)) if Controller.canInvokeWithoutSelection(invoked) => Controller.invokeCursor(invoked, invoked)
      case (Some(sel), Some(invoked)) => clearSelectedArea(); Controller.invokeCursor(sel, invoked)
      case (Some(sel), None) => clearSelectedArea()
      case (None, Some(sel)) if Controller.canSelect(sel) => drawSelectedArea(sel)
      case _ => // do nothing
    }
  }

  //
  // mouseMove
  //
  def mouseMove(evt: MouseEvent): Unit = getCursor(evt.clientX, evt.clientY) match {
    case x if x == activeCursor => // do nothing
    case x@Some(cursor) if Controller.canActivate(cursor) => drawActiveCursor(cursor)
    case _ => clearActiveCursor()
  }

  //
  // utilities
  //
  protected def flipSquare(square: Square): Square = Square(10 - square.file, 10 - square.rank)

}
