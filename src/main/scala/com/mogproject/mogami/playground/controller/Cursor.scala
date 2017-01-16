package com.mogproject.mogami.playground.controller

import com.mogproject.mogami._

/**
  * cursor
  */
case class Cursor(board: Option[Square], hand: Option[Hand], box: Option[Ptype]) {
  require(isBoard || isHand || isBox)

  def isBoard: Boolean = board.isDefined

  def isHand: Boolean = hand.isDefined

  def isBox: Boolean = box.isDefined

  def moveFrom: MoveFrom = (board, hand) match {
    case (Some(s), None) => Left(s)
    case (None, Some(h)) => Right(h)
    case _ => throw new IllegalArgumentException(s"cannot create MoveFrom instance: ${this}")
  }
}

object Cursor {
  def apply(square: Square): Cursor = Cursor(Some(square), None, None)

  def apply(hand: Hand): Cursor = Cursor(None, Some(hand), None)

  def apply(piece: Piece): Cursor = apply(Hand(piece))

  def apply(player: Player, ptype: Ptype): Cursor = apply(Hand(player, ptype))

  def apply(ptype: Ptype): Cursor = Cursor(None, None, Some(ptype))
}