package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.core.State
import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import org.scalajs.dom.html.{Button, Div}

import scalatags.JsDom.all._

/**
  *
  */
object EditReset extends ButtonLike[State, Button, Div] {
  override protected val keys = Seq(
    State.HIRATE, State.MATING_BLACK, State.MATING_WHITE,
    State.HANDICAP_LANCE, State.HANDICAP_BISHOP, State.HANDICAP_ROOK, State.HANDICAP_ROOK_LANCE,
    State.HANDICAP_2_PIECE, State.HANDICAP_3_PIECE, State.HANDICAP_4_PIECE, State.HANDICAP_5_PIECE,
    State.HANDICAP_6_PIECE, State.HANDICAP_8_PIECE, State.HANDICAP_10_PIECE,
    State.HANDICAP_THREE_PAWNS, State.HANDICAP_NAKED_KING
  )

  override protected val labels: Map[Language, Seq[String]] = Map(
    Japanese -> Seq(
      "平手", "詰将棋 (先手)", "詰将棋 (後手)",
      "香落ち", "角落ち", "飛車落ち", "飛香落ち",
      "二枚落ち", "三枚落ち", "四枚落ち", "五枚落ち",
      "六枚落ち", "八枚落ち", "十枚落ち",
      "歩三兵", "裸玉"),
    English -> Seq("Even", "Mating (Black)", "Mating (White)",
      "Lance", "Bishop", "Rook", "Rook-Lance",
      "2-Piece", "3-Piece", "4-Piece", "5-Piece",
      "6-Piece", "8-Piece", "10-Piece",
      "Three Pawns", "Naked King")
  )

  override protected def generateInput(key: State): Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block"
  ).render

  override protected def invoke(key: State): Unit = Controller.setEditInitialState(key)

  override val output: Div = div(
    h4("Reset"),
    div(cls := "row", inputs.map(e => div(cls := "col-md-4 col-xs-6", e)))
  ).render

}
