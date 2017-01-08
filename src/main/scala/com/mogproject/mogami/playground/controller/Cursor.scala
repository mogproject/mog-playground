package com.mogproject.mogami.playground.controller

import com.mogproject.mogami._

/**
  * cursor
  */
case class Cursor(moveFrom: MoveFrom) {
  def isBoard: Boolean = moveFrom.isLeft

  def isHand: Boolean = moveFrom.isRight
}

object Cursor {
  def apply(square: Square): Cursor = Cursor(Left(square))

  def apply(hand: Hand): Cursor = Cursor(Right(hand))

  def apply(piece: Piece): Cursor = Cursor(Hand(piece))

  def apply(player: Player, ptype: Ptype): Cursor = Cursor(Hand(player, ptype))
}