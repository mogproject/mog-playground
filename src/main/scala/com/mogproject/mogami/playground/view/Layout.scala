package com.mogproject.mogami.playground.view

/**
  * layout constants
  */
case class Layout(canvasWidth: Int) {

  lazy val canvasHeight: Int = pieceBox.bottom + MARGIN_BOTTOM

  def scaleByCanvas(x: Int): Int = canvasWidth * x / 1000

  def scaleByPiece(pieceWidth: Int, x: Int): Int = pieceWidth * x / 1000

  // constants
  lazy val PIECE_WIDTH: Int = scaleByCanvas(107)
  lazy val PIECE_HEIGHT: Int = scaleByCanvas(113)
  lazy val INDICATOR_HEIGHT: Int = scaleByCanvas(32)
  lazy val MARGIN_TOP: Int = scaleByCanvas(7)
  val MARGIN_BOTTOM: Int = 2
  lazy val MARGIN_LEFT: Int = scaleByCanvas(7)
  lazy val MARGIN_RIGHT: Int = canvasWidth - board.right
  lazy val BOARD_WIDTH: Int = PIECE_WIDTH * 9
  lazy val BOARD_HEIGHT: Int = PIECE_HEIGHT * 9

  // rectangles
  val handWhite = Rectangle(MARGIN_LEFT, MARGIN_TOP, BOARD_WIDTH - PIECE_WIDTH * 2, PIECE_HEIGHT)
  val indicatorWhite = Rectangle(MARGIN_LEFT, handWhite.bottom + 2, BOARD_WIDTH, INDICATOR_HEIGHT)
  val board = Rectangle(MARGIN_LEFT, indicatorWhite.bottom + 2, BOARD_WIDTH, BOARD_HEIGHT)
  val indicatorBlack = Rectangle(MARGIN_LEFT, board.bottom + 2, BOARD_WIDTH, INDICATOR_HEIGHT)
  val handBlack = Rectangle(MARGIN_LEFT + PIECE_WIDTH * 2, indicatorBlack.bottom + 2, BOARD_WIDTH - PIECE_WIDTH * 2, PIECE_HEIGHT)
  val fileIndex: Rectangle = indicatorWhite
  val rankIndex = Rectangle(board.right + 1, board.top, MARGIN_RIGHT, BOARD_HEIGHT)
  val pieceBox = Rectangle(MARGIN_LEFT + PIECE_WIDTH, handBlack.bottom + INDICATOR_HEIGHT + 2, BOARD_WIDTH - PIECE_WIDTH, PIECE_HEIGHT)

  // fonts
  object font {
    private[this] val japanese = """"游明朝", YuMincho, "ヒラギノ明朝 ProN W3", "Hiragino Mincho ProN", "HG明朝E", "ＭＳ Ｐ明朝", "ＭＳ 明朝", serif"""
    private[this] val english = "Times, serif"

    def pentagon(pieceWidth: Int = PIECE_WIDTH): String = s"${scaleByPiece(pieceWidth, 706)}pt ${japanese}"

    def pieceJapanese(pieceWidth: Int = PIECE_WIDTH): String = s"${scaleByPiece(pieceWidth, 648)}pt ${japanese}"

    def pieceEnglish(pieceWidth: Int = PIECE_WIDTH): String = s"${scaleByPiece(pieceWidth, 530)}pt ${english}"

    lazy val numberOfPieces = s"${scaleByPiece(PIECE_WIDTH, 383)}pt ${english}"
    lazy val numberIndex = s"${scaleByPiece(PIECE_WIDTH, 236)}pt ${japanese}"
  }

  // colors
  object color {
    val fg = "black"
    // foreground
    val bg = "#fefdfa"
    // background
    val red = "#b22222"
    // promoted pieces
    val win = "#83ff9d"
    val lose = "#ff5843"
    val draw = "#99877a"
    val active = "#45A1CF"
    val cursor = "#E1B265"
    val dark = "#353535"
    val light = "#E0FFFF"
    val pieceBox = "#cccccc" // background of the piece box
  }

}
