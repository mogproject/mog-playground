package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.core.Game.GameStatus
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

  def updateRecordContent(game: Game, lng: Language): Unit = {
    val f: Move => String = lng match {
      case Japanese => _.toKifString
      case English => _.toWesternNotationString
    }

    val xs = game.moves.zipWithIndex.map { case (m, i) =>
      s"${i + 1}: ${game.history(i).turn.toSymbolString + f(m)}"
    }.toList
    val prefix = lng match {
      case Japanese => "初期局面"
      case English => "Start"
    }
    val suffix = (game.status, lng) match {
      case (GameStatus.Mated, Japanese) => List("詰み")
      case (GameStatus.Drawn, Japanese) => List("千日手")
      case (GameStatus.PerpetualCheck, Japanese) => List("連続王手の千日手")
      case (GameStatus.Uchifuzume, Japanese) => List("打ち歩詰め")
      case (GameStatus.Mated, English) => List("Mated")
      case (GameStatus.Drawn, English) => List("Drawn")
      case (GameStatus.PerpetualCheck, English) => List("Perpetual Check")
      case (GameStatus.Uchifuzume, English) => List("Uchifuzume")
      case (GameStatus.Playing, _) => List()
    }
    val ys = prefix :: xs ++ suffix

    recordSelector.innerHTML = ys.map(s => option(s)).mkString
    updateRecordIndex(-1)
  }

  override def initialize(): Unit = {
    setClickEvent(controlInput0, () => Controller.setControl(0))
    setClickEvent(controlInput1, () => Controller.setControl(1))
    setClickEvent(controlInput2, () => Controller.setControl(2))
    setClickEvent(controlInput3, () => Controller.setControl(3))
  }

  private[this] def createControlInput(glyph: String) = button(cls := "btn btn-default",
    width := CONTROL_WIDTH,
    span(cls := s"glyphicon glyphicon-${glyph}", aria.hidden := true)
  ).render

  val output: Div = div(
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
