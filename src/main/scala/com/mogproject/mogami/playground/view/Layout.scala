package com.mogproject.mogami.playground.view

/**
  * layout constants
  */
case class Layout(canvasWidth: Int) {

  lazy val canvasHeight: Int = pieceBox.bottom + MARGIN_BOTTOM
  lazy val canvasHeightCompact: Int = handBlack.bottom + MARGIN_BOTTOM

  def scaleByCanvas(x: Int): Int = canvasWidth * x / 1000

  def scaleByPiece(pieceWidth: Int, x: Int): Int = pieceWidth * x / 1000

  // constants
  lazy val PIECE_WIDTH: Int = scaleByCanvas(107)
  lazy val PIECE_HEIGHT: Int = scaleByCanvas(113)
  lazy val MARGIN_BLOCK: Int = scaleByCanvas(32)
  lazy val MARGIN_TOP: Int = 4
  val MARGIN_BOTTOM: Int = 4
  lazy val MARGIN_LEFT: Int = scaleByCanvas(20)
  lazy val MARGIN_RIGHT: Int = canvasWidth - board.right
  lazy val BOARD_WIDTH: Int = PIECE_WIDTH * 9
  lazy val BOARD_HEIGHT: Int = PIECE_HEIGHT * 9
  lazy val INDICATOR_HEIGHT: Int = scaleByCanvas(40)

  // sizes
  val playerAreaWidth: Int = PIECE_WIDTH * 2 - 4
  lazy val playerIconWidth: Int = scaleByCanvas(64)
  lazy val playerIconHeight: Int = PIECE_HEIGHT - INDICATOR_HEIGHT - 3
  lazy val playerNameWidth: Int = playerAreaWidth - playerIconWidth - 2

  // rectangles
  val playerWhite = Rectangle(MARGIN_LEFT + BOARD_WIDTH - PIECE_WIDTH * 2 + 4, MARGIN_TOP, playerAreaWidth, PIECE_HEIGHT)
  val indicatorWhite = Rectangle(playerWhite.left + 1, playerWhite.top + 1, playerWhite.width - 2, INDICATOR_HEIGHT)
  val playerIconWhite = Rectangle(playerWhite.right - playerIconWidth, indicatorWhite.bottom + 1, playerIconWidth, playerIconHeight)
  val playerNameWhite = Rectangle(playerWhite.left + 1, playerIconWhite.top, playerNameWidth, playerIconWhite.height)

  val handWhite = Rectangle(MARGIN_LEFT, MARGIN_TOP, BOARD_WIDTH - PIECE_WIDTH * 2, PIECE_HEIGHT)

  val fileIndex: Rectangle = Rectangle(MARGIN_LEFT, handWhite.bottom + 2, BOARD_WIDTH, MARGIN_BLOCK)

  val board = Rectangle(MARGIN_LEFT, handWhite.bottom + MARGIN_BLOCK + 2, BOARD_WIDTH, BOARD_HEIGHT)

  val playerBlack = Rectangle(MARGIN_LEFT, board.bottom + MARGIN_BLOCK + 2, playerAreaWidth, PIECE_HEIGHT)
  val indicatorBlack = Rectangle(playerBlack.left + 1, playerBlack.bottom - INDICATOR_HEIGHT - 1, playerBlack.width - 2, INDICATOR_HEIGHT)
  val playerIconBlack = Rectangle(playerBlack.left + 1, playerBlack.top + 1, playerIconWidth, playerIconHeight)
  val playerNameBlack = Rectangle(playerIconBlack.right + 1, playerIconBlack.top, playerNameWidth, playerIconBlack.height)

  val handBlack = Rectangle(MARGIN_LEFT + PIECE_WIDTH * 2, playerBlack.top, BOARD_WIDTH - PIECE_WIDTH * 2, PIECE_HEIGHT)

  val rankIndex = Rectangle(board.right + 1, board.top, MARGIN_RIGHT, BOARD_HEIGHT)
  val pieceBox = Rectangle(MARGIN_LEFT + PIECE_WIDTH, handBlack.bottom + MARGIN_BLOCK + 2, BOARD_WIDTH - PIECE_WIDTH, PIECE_HEIGHT)

  // fonts
  object font {
    private[this] val japanese = """"游明朝", YuMincho, "ヒラギノ明朝 ProN W3", "Hiragino Mincho ProN", "HG明朝E", "ＭＳ Ｐ明朝", "ＭＳ 明朝", serif"""
    private[this] val english = "Times, serif"
    private[this] val menu = "Helvetica Neue"

    def pentagon(pieceWidth: Int = PIECE_WIDTH): String = s"${scaleByPiece(pieceWidth, 706)}pt ${japanese}"

    def pieceJapanese(pieceWidth: Int = PIECE_WIDTH): String = s"${scaleByPiece(pieceWidth, 648)}pt ${japanese}"

    def pieceEnglish(pieceWidth: Int = PIECE_WIDTH): String = s"${scaleByPiece(pieceWidth, 530)}pt ${english}"

    lazy val numberOfPieces = s"${scaleByPiece(PIECE_WIDTH, 383)}pt ${english}"
    lazy val numberIndex = s"${scaleByPiece(PIECE_WIDTH, 236)}pt ${japanese}"
    lazy val indicator = s"${scaleByCanvas(28)}pt ${menu}"
    lazy val playerIcon = s"${scaleByCanvas(40)}pt ${japanese}"
    lazy val playerNameJapanese = s"${scaleByCanvas(32)}pt ${japanese}"
    lazy val playerNameEnglish = s"${scaleByCanvas(32)}pt ${english}"
    lazy val pieceBoxLabel = s"${scaleByCanvas(28)}pt ${menu}"
  }

  // colors
  object color {
    // foreground
    val fg = "black"

    // background
    val bg = "#fefdfa"

    // promoted pieces
    val red = "#b22222"

    // indicators
    val active = "#3276b1"
    val win = "#339933"
    val lose = "#ff5843"
    val draw = "#99877a"

    val cursor = "#E1B265"
    val flash = "#805819"
    val dark = "#353535"
    val light = "#E0FFFF"

    // indicator text
    val white = "#ffffff"
    val pieceBox = "#cccccc" // background of the piece box

    // numbers
    val stroke = "#333333"
  }

  lazy val strokeSize: Int = scaleByCanvas(10)
}
