package com.mogproject.mogami.playground.controller.mode


import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._

/**
  *
  */
trait CursorAdjustable {

  /**
    * Adjust the cursor position for flicking.
    *
    * @return None if unable to adjust
    */
  protected[mode] def adjustMovement(piece: Piece, from: Square, to: Square): Option[Square] = {
    val x = piece.owner.isWhite.when[Int](-_)(from.file - to.file)
    val y = piece.owner.isWhite.when[Int](-_)(from.rank - to.rank)

    (piece.ptype match {
      case PAWN => adjustmentTable.get(PAWN).flatMap(_.apply(categorizeMovement(x, y)))
      case GOLD | PPAWN | PLANCE | PKNIGHT | PSILVER => adjustmentTable.get(GOLD).flatMap(_.apply(categorizeMovement(x, y)))
      case SILVER => adjustmentTable.get(SILVER).flatMap(_.apply(categorizeMovement(x, y)))
      case KNIGHT => adjustmentTable.get(KNIGHT).flatMap(_.apply(categorizeMovement(x, y)))
      case KING => adjustmentTable.get(KING).flatMap(_.apply(categorizeMovement(x, y)))
      case _ => Some((x, y))
    }) map {
      case (xx, yy) => piece.owner.isBlack.fold((xx, yy), (-xx, -yy))
    } map {
      case (f, r) => Square(from.file - f, from.rank - r)
    }
  }

  /**
    * @note
    *
    * x
    * -   0   +
    * =========
    * dcccbaaa9 |+
    * edccbaa98 |
    * eedcba988 |
    * eeedb9888 |
    * ffff*7777 |y
    * 000135666 |
    * 001234566 |
    * 012234456 |
    * 122234445 |-
    *
    */
  protected[mode] def categorizeMovement(x: Int, y: Int): Int = {
    val t = (math.atan2(y, x) + math.Pi) / math.Pi * 4
    val a = math.floor(t).toInt
    a * 2 - (a == t).fold(1, 0)
  }

  private[this] lazy val adjustmentTable: Map[Ptype, Vector[Option[(Int, Int)]]] = Map(
    PAWN -> Vector(
      None, None, None, None,
      None, None, None, None,
      Some((0, 1)), Some((0, 1)), Some((0, 1)), Some((0, 1)),
      Some((0, 1)), Some((0, 1)), Some((0, 1)), None),
    GOLD -> Vector(
      Some((-1, 0)), None, Some((0, -1)), Some((0, -1)),
      Some((0, -1)), None, Some((1, 0)), Some((1, 0)),
      Some((1, 1)), Some((1, 1)), Some((0, 1)), Some((0, 1)),
      Some((0, 1)), Some((-1, 1)), Some((-1, 1)), Some((-1, 0))),
    SILVER -> Vector(
      Some((-1, -1)), Some((-1, -1)), Some((-1, -1)), None,
      Some((1, -1)), Some((1, -1)), Some((1, -1)), None,
      Some((1, 1)), Some((1, 1)), Some((0, 1)), Some((0, 1)),
      Some((0, 1)), Some((-1, 1)), Some((-1, 1)), None),
    KNIGHT -> Vector(
      None, None, None, None,
      None, None, None, None,
      None, Some((1, 2)), Some((1, 2)), None,
      Some((-1, 2)), Some((-1, 2)), None, None),
    KING -> Vector(
      None, Some((-1, -1)), None, Some((0, -1)),
      None, Some((1, -1)), None, Some((1, 0)),
      None, Some((1, 1)), None, Some((0, 1)),
      None, Some((-1, 1)), None, Some((-1, 0)))
  )

}
