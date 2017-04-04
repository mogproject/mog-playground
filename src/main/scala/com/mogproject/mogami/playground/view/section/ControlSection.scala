package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.core.Game.GameStatus
import com.mogproject.mogami.core.move.SpecialMove
import com.mogproject.mogami.{Game, Move}
import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import com.mogproject.mogami.playground.view.EventManageable
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLSelectElement
import com.mogproject.mogami.util.Implicits._

import scalatags.JsDom.all._

/**
  *
  */
case class ControlSection(canvasWidth: Int) extends Section with EventManageable {
  val CONTROL_WIDTH = 48

  private[this] val controlInput0 = createControlInput("step-backward")
  private[this] val controlInput1 = createControlInput("backward")
  private[this] val controlInput2 = createControlInput("forward")
  private[this] val controlInput3 = createControlInput("step-forward")

  private[this] val recordSelector: HTMLSelectElement = select(
    cls := "form-control rect-select",
    width := canvasWidth - CONTROL_WIDTH * 4,
    onchange := (() => Controller.setRecord(recordSelector.selectedIndex))
  ).render

  def getMaxRecordIndex: Int = recordSelector.options.length - 1

  def getSelectedIndex: Int = recordSelector.selectedIndex

  def getRecordIndex(index: Int): Int = (index < 0).fold(getMaxRecordIndex, math.min(index, getMaxRecordIndex))

  def updateRecordIndex(index: Int): Unit = recordSelector.selectedIndex = getRecordIndex(index)

  private[this] def getMoves(game: Game, lng: Language): List[String] = {
    val f: Move => String = lng match {
      case Japanese => _.toJapaneseNotationString
      case English => _.toWesternNotationString
    }
    val g: SpecialMove => String = lng match {
      case Japanese => _.toJapaneseNotationString
      case English => _.toWesternNotationString
    }
    (game.moves.map(f) ++ (game.status match {
      case GameStatus.Resigned | GameStatus.TimedUp => List(g(game.finalAction.get))
      case GameStatus.IllegallyMoved => g(game.finalAction.get).split("\n").toList.take(1)
      case _ => Nil
    })).toList
  }

  def updateRecordContent(game: Game, lng: Language): Unit = {
    // moves
    val xs = getMoves(game, lng).zipWithIndex.map { case (m, i) =>
      s"${i + 1}: ${game.history(i).turn.toSymbolString()}${m}"
    }
    val prefix = lng match {
      case Japanese => "初期局面"
      case English => "Start"
    }
    val suffix = (game.status, lng) match {
      case (GameStatus.Mated, Japanese) => List("詰み")
      case (GameStatus.Mated, English) => List("Mated")
      case (GameStatus.Drawn, Japanese) => List("千日手")
      case (GameStatus.Drawn, English) => List("Drawn")
      case (GameStatus.PerpetualCheck, Japanese) => List("連続王手の千日手")
      case (GameStatus.PerpetualCheck, English) => List("Perpetual Check")
      case (GameStatus.Uchifuzume, Japanese) => List("打ち歩詰め")
      case (GameStatus.Uchifuzume, English) => List("Uchifuzume")
      case (GameStatus.IllegallyMoved, Japanese) => game.finalAction.get.toJapaneseNotationString.split("\n").toList.drop(1)
      case (GameStatus.IllegallyMoved, English) => game.finalAction.get.toWesternNotationString.split("\n").toList.drop(1)
      case _ => Nil
    }
    val ys = prefix :: xs ++ suffix

    recordSelector.innerHTML = ys.map(s => option(s)).mkString
//    updateRecordIndex(-1)
  }

  override def initialize(): Unit = {
    setClickEvent(controlInput0, () => Controller.setControl(0))
    setClickEvent(controlInput1, () => Controller.setControl(1), Some(() => Controller.setControl(1)), Some(() => controlInput1.disabled == true))
    setClickEvent(controlInput2, () => Controller.setControl(2), Some(() => Controller.setControl(2)), Some(() => controlInput2.disabled == true))
    setClickEvent(controlInput3, () => Controller.setControl(3))
  }

  private[this] def createControlInput(glyph: String) = button(cls := "btn btn-default",
    width := CONTROL_WIDTH,
    span(cls := s"glyphicon glyphicon-${glyph}", aria.hidden := true)
  ).render

  override val output: Div = div(
    label("Control"),
    div(cls := "btn-toolbar", role := "toolbar",
      div(cls := "btn-group", role := "group", aria.label := "...",
        div(cls := "btn-group", role := "group", controlInput0),
        div(cls := "btn-group", role := "group", controlInput1),
        div(cls := "btn-group", role := "group", recordSelector),
        div(cls := "btn-group", role := "group", controlInput2),
        div(cls := "btn-group", role := "group", controlInput3)
      )
    )
  ).render

  def updateLabels(stepBackwardEnabled: Boolean, backwardEnabled: Boolean, forwardEnabled: Boolean, stepForwardEnabled: Boolean): Unit = {
    controlInput0.disabled = !stepBackwardEnabled
    controlInput1.disabled = !backwardEnabled
    controlInput2.disabled = !forwardEnabled
    controlInput3.disabled = !stepForwardEnabled
  }
}
