package com.mogproject.mogami.playground.view.parts

import com.mogproject.mogami.core.State
import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import org.scalajs.dom.html.{Button, Div}

import scalatags.JsDom.all._

/**
  * key: (state: State, isHandicap: Boolean)
  */
object EditReset extends ButtonLike[(State, Boolean), Button, Div] {
  override protected val keys = Seq(
    (State.HIRATE, false),
    (State.MATING_BLACK, false),
    (State.MATING_WHITE, false),
    (State.HANDICAP_LANCE, true),
    (State.HANDICAP_BISHOP, true),
    (State.HANDICAP_ROOK, true),
    (State.HANDICAP_ROOK_LANCE, true),
    (State.HANDICAP_2_PIECE, true),
    (State.HANDICAP_3_PIECE, true),
    (State.HANDICAP_4_PIECE, true),
    (State.HANDICAP_5_PIECE, true),
    (State.HANDICAP_6_PIECE, true),
    (State.HANDICAP_8_PIECE, true),
    (State.HANDICAP_10_PIECE, true),
    (State.HANDICAP_THREE_PAWNS, true),
    (State.HANDICAP_NAKED_KING, true)
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

  override protected def generateInput(key: (State, Boolean)): Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("dismiss") := "modal"
  ).render

  override protected def invoke(key: (State, Boolean)): Unit = Controller.setEditInitialState(key._1, key._2)

  override val output: Div = div(
    cls := "row", inputs.map(e => div(cls := "col-lg-4 col-xs-6", e))
  ).render

}
