package com.mogproject.mogami.playground.view.layout

import com.mogproject.mogami.playground.view.renderer.{Rectangle, RoundRect}

/**
  *
  */
case class BoardLayout(canvasWidth: Int) {

  lazy val canvasHeight: Int = pieceBox.bottom + MARGIN_BOTTOM
  lazy val canvasHeightCompact: Int = handBlack.bottom + MARGIN_BOTTOM

  private[this] def scaleByCanvas(x: Int): Int = canvasWidth * x / 1000

  def scaleByPiece(pieceWidth: Int, x: Int): Int = pieceWidth * x / 1000

  // constants
  lazy val PIECE_WIDTH: Int = scaleByCanvas(107)
  lazy val PIECE_HEIGHT: Int = scaleByCanvas(113)
  lazy val HAND_PIECE_WIDTH: Int = scaleByCanvas(107 * 6 / 7)
  lazy val HAND_PIECE_HEIGHT: Int = scaleByCanvas(113 * 6 / 7)
  lazy val MARGIN_BLOCK: Int = scaleByCanvas(32)
  lazy val MARGIN_TOP: Int = 4
  lazy val MARGIN_BOTTOM: Int = 4
  lazy val MARGIN_LEFT: Int = (canvasWidth - BOARD_WIDTH) / 3
  lazy val MARGIN_RIGHT: Int = canvasWidth - board.right
  lazy val BOARD_WIDTH: Int = PIECE_WIDTH * 9
  lazy val BOARD_HEIGHT: Int = PIECE_HEIGHT * 9
  lazy val INDICATOR_HEIGHT: Int = scaleByCanvas(40)
  lazy val DOT_SIZE: Int = math.max(0, math.min(3, (canvasWidth - 80) / 100))

  // sizes
  lazy val playerAreaWidth: Int = PIECE_WIDTH * 3 - 4
  lazy val playerIconWidth: Int = scaleByCanvas(64)
  lazy val playerIconHeight: Int = MARGIN_BLOCK / 2 + HAND_PIECE_HEIGHT - INDICATOR_HEIGHT
  lazy val playerNameWidth: Int = playerAreaWidth - playerIconWidth - 2
  lazy val playerNameHeight: Int = HAND_PIECE_HEIGHT - INDICATOR_HEIGHT - 1

  // rectangles
  val playerWhite = Rectangle(MARGIN_LEFT + BOARD_WIDTH - PIECE_WIDTH * 3 + 4, MARGIN_TOP, playerAreaWidth, HAND_PIECE_HEIGHT)
  val indicatorWhite = Rectangle(playerWhite.left + 1, playerWhite.top + 1, playerWhite.width - 2, INDICATOR_HEIGHT)
  val playerIconWhite = Rectangle(playerWhite.right - playerIconWidth, indicatorWhite.bottom - 1, playerIconWidth, playerIconHeight)
  val playerNameWhite = Rectangle(playerWhite.left + 1, playerIconWhite.top + 1, playerNameWidth, playerNameHeight)

  val handWhite = Rectangle(MARGIN_LEFT, MARGIN_TOP, BOARD_WIDTH - PIECE_WIDTH * 3, HAND_PIECE_HEIGHT)

  val fileIndex: Rectangle = Rectangle(MARGIN_LEFT, handWhite.bottom + 2, BOARD_WIDTH, MARGIN_BLOCK)

  val board = Rectangle(MARGIN_LEFT, handWhite.bottom + MARGIN_BLOCK + 2, BOARD_WIDTH, BOARD_HEIGHT)

  val playerBlack = Rectangle(MARGIN_LEFT, board.bottom + MARGIN_BLOCK + 2, playerAreaWidth, HAND_PIECE_HEIGHT)
  val indicatorBlack = Rectangle(playerBlack.left + 1, playerBlack.bottom - INDICATOR_HEIGHT - 1, playerBlack.width - 2, INDICATOR_HEIGHT)
  val playerIconBlack = Rectangle(playerBlack.left + 1, playerBlack.bottom - INDICATOR_HEIGHT - playerIconHeight, playerIconWidth, playerIconHeight)
  val playerNameBlack = Rectangle(playerIconBlack.right + 1, playerBlack.top + 1, playerNameWidth, playerNameHeight)

  val handBlack = Rectangle(MARGIN_LEFT + PIECE_WIDTH * 3, playerBlack.top, BOARD_WIDTH - PIECE_WIDTH * 3, HAND_PIECE_HEIGHT)

  val rankIndex = Rectangle(board.right - 1, board.top, MARGIN_RIGHT, BOARD_HEIGHT)
  val pieceBox = Rectangle(MARGIN_LEFT + PIECE_WIDTH, handBlack.bottom + MARGIN_BLOCK + 2, BOARD_WIDTH - PIECE_WIDTH, PIECE_HEIGHT)

  lazy val moveForwardWidth: Int = PIECE_WIDTH * 3
  lazy val moveForwardHeight: Int = PIECE_HEIGHT * 4

  lazy val moveForward = RoundRect(board.left + PIECE_WIDTH * 11 / 2, board.top + PIECE_HEIGHT * 5 / 2, moveForwardWidth, moveForwardHeight, PIECE_WIDTH / 2)
  lazy val moveBackward = RoundRect(board.left + PIECE_WIDTH / 2, board.top + PIECE_HEIGHT * 5 / 2, moveForwardWidth, moveForwardHeight, PIECE_WIDTH / 2)

  // fonts
  object font {
    private[this] val japanese = """"游明朝", YuMincho, "ヒラギノ明朝 ProN W3", "Hiragino Mincho ProN", "HG明朝E", "ＭＳ Ｐ明朝", "ＭＳ 明朝", serif"""
    private[this] val english = "Times, serif"
    private[this] val menu = "Helvetica Neue"

    def pentagon(pieceWidth: Int = PIECE_WIDTH): String = s"${scaleByPiece(pieceWidth, 706)}pt ${japanese}"

    def pieceJapanese(pieceWidth: Int = PIECE_WIDTH): String = s"${scaleByPiece(pieceWidth, 648)}pt ${japanese}"

    def pieceEnglish(pieceWidth: Int = PIECE_WIDTH): String = s"${scaleByPiece(pieceWidth, 530)}pt ${english}"

    lazy val numberOfPieces = s"${scaleByPiece(PIECE_WIDTH, 340)}pt ${english}"
    lazy val numberIndex = s"${scaleByPiece(PIECE_WIDTH, 180)}pt ${japanese}"
    lazy val indicator = s"${scaleByCanvas(28)}pt ${menu}"
    lazy val playerIcon = s"${scaleByCanvas(40)}pt ${japanese}"
    lazy val playerName = s"${scaleByCanvas(28)}pt ${japanese}"
    lazy val pieceBoxLabel = s"${scaleByCanvas(28)}pt ${menu}"
    lazy val moveForward = s"${scaleByCanvas(140)}pt ${japanese}"
  }

  // colors
  object color {
    // foreground
    val fg = "black"

    // background
    val bg = "#fefdfa"

    // promoted pieces
    val red = "#d9534f"

    // indicators
    val active = "#2b5f91"
    val win = "#5cb85c"
    val lose = "#d9534f"
    val draw = "#99877a"

    val cursor = "#E1B265"
    val flash = "#805819"
    val dark = "#353535"
    val light = "#f0f0f0"

    // indicator text
    val white = "#ffffff"
    val pieceBox = "#cccccc" // background of the piece box

    // numbers
    val number = "#f3f372"
    val stroke = "#333333"
  }

  lazy val strokeSize: Int = math.max(3, scaleByCanvas(10))
  lazy val moveForwardStrokeSize: Int = scaleByCanvas(20)
  val moveForwardAlpha: Double = 0.2

}
