package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.{MoveFrom, _}
import com.mogproject.mogami.util.Implicits._

/**
  * cursor
  */
case class Cursor(board: Option[Square], hand: Option[Hand], box: Option[Ptype], player: Option[Player]) {
  require(isBoard || isHand || isBox || isPlayer)

  def isBoard: Boolean = board.isDefined

  def isHand: Boolean = hand.isDefined

  def isBox: Boolean = box.isDefined

  def isPlayer: Boolean = player.isDefined

  def moveFrom: MoveFrom = (board, hand) match {
    case (Some(s), None) => Left(s)
    case (None, Some(h)) => Right(h)
    case _ => throw new IllegalArgumentException(s"cannot create MoveFrom instance: ${this}")
  }

  def unary_! : Cursor = this match {
    case Cursor(Some(sq), _, _, _) => Cursor(Some(!sq), None, None, None)
    case Cursor(_, Some(h), _, _) => Cursor(None, Some(!h), None, None)
    case Cursor(_, _, _, Some(p)) => Cursor(None, None, None, Some(!p))
    case _ => this
  }
}

object Cursor {
  def apply(square: Square, isFlipped: Boolean = false): Cursor = Cursor(Some(isFlipped.when[Square](!_)(square)), None, None, None)

  def apply(hand: Hand): Cursor = Cursor(None, Some(hand), None, None)

  def apply(piece: Piece): Cursor = apply(Hand(piece))

  def apply(player: Player, ptype: Ptype): Cursor = apply(Hand(player, ptype))

  def apply(ptype: Ptype): Cursor = Cursor(None, None, Some(ptype), None)

  def apply(player: Player): Cursor = Cursor(None, None, None, Some(player))
}