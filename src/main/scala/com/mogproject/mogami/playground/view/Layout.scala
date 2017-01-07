package com.mogproject.mogami.playground.view

/**
  * layout constants
  */
case class Layout(canvasWidth: Int, canvasHeight: Int) {

  // constants
  val PIECE_WIDTH = 34
  val PIECE_HEIGHT = 36
  val INDICATOR_HEIGHT = 10
  val HAND_UNIT_WIDTH = 43
  val BOARD_LEFT: Int = (canvasWidth - PIECE_WIDTH * 9) / 2
  val BOARD_WIDTH: Int = PIECE_WIDTH * 9
  val BOARD_HEIGHT: Int = PIECE_HEIGHT * 9

  // rectangles
  val handWhite = Rectangle(BOARD_LEFT, 0, BOARD_WIDTH, PIECE_HEIGHT)
  val indicatorWhite = Rectangle(BOARD_LEFT, handWhite.bottom + 2, BOARD_WIDTH, INDICATOR_HEIGHT)
  val board = Rectangle(BOARD_LEFT, indicatorWhite.bottom + 2, BOARD_WIDTH, BOARD_HEIGHT)
  val indicatorBlack = Rectangle(BOARD_LEFT, board.bottom + 2, BOARD_WIDTH, INDICATOR_HEIGHT)
  val handBlack = Rectangle(BOARD_LEFT, indicatorBlack.bottom + 2, BOARD_WIDTH, PIECE_HEIGHT)

  // fonts
  object font {
    val pieceJapanese = """22pt "游明朝", YuMincho, "ヒラギノ明朝 ProN W3", "Hiragino Mincho ProN", "HG明朝E", "ＭＳ Ｐ明朝", "ＭＳ 明朝", serif"""
    val number = "13pt Times, serif"
  }

  // colors
  object color {
    val fg = "black"  // foreground
    val bg = "#fefdfa"  // background
    val red = "#b22222"  // promoted pieces
    val active = "#45A1CF"
    val cursor = "#E1B265"
    val dark = "#353535"
    val light = "#bdbbb0"
  }
}
