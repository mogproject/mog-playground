package com.mogproject.mogami.playground.controller

import com.mogproject.mogami.{MoveFrom, Piece, Square}

/**
  * cursor
  */
case class Cursor(moveFrom: MoveFrom) {
  def isBoard: Boolean = moveFrom.isLeft

  def isHand: Boolean = moveFrom.isRight
}
