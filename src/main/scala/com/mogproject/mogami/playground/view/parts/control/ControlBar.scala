package com.mogproject.mogami.playground.view.parts.control

import com.mogproject.mogami.{BranchNo, Game, GamePosition, GameStatus, Move}
import com.mogproject.mogami.core.move.SpecialMove
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import com.mogproject.mogami.playground.view.parts.common.EventManageable
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLSelectElement

import scalatags.JsDom.all._

/**
  *
  */
case class ControlBar(sectionWidth: Int, isSmall: Boolean) extends EventManageable {

  private[this] val CONTROL_WIDTH = 48
  private[this] val LONG_LIST_SIZE = 32

  private[this] val controlInput0 = createControlInput("step-backward")
  private[this] val controlInput1 = createControlInput("backward")
  private[this] val controlInput2 = createControlInput("forward")
  private[this] val controlInput3 = createControlInput("step-forward")

  private[this] val recordSelector: HTMLSelectElement = select(
    cls := "form-control rect-select" + isSmall.fold(" control-small", ""),
    width := sectionWidth - CONTROL_WIDTH * 4 + 4,
    onchange := (() => Controller.setRecord(recordSelector.selectedIndex))
  ).render

  private[this] val recordSelectorLong: HTMLSelectElement = select(
    cls := "form-control",
    width := "100%",
    size := LONG_LIST_SIZE,
    onchange := (() => Controller.setRecord(recordSelectorLong.selectedIndex))
  ).render

  def getMaxRecordIndex: Int = recordSelector.options.length - 1

  def getSelectedIndex: Int = recordSelector.selectedIndex

  def getRecordIndex(index: Int): Int = (index < 0).fold(getMaxRecordIndex, math.min(index, getMaxRecordIndex))

  def updateRecordIndex(index: Int): Unit = {
    val x = getRecordIndex(index)
    recordSelector.selectedIndex = x
    recordSelectorLong.selectedIndex = x
  }

  /**
    * Create move description
    *
    * @param game     Game instance
    * @param branchNo branch number (trunk:0)
    * @param lng      language
    * @return
    */
  private[this] def getMoves(game: Game, branchNo: BranchNo, lng: Language): List[String] = {
    val f: Move => String = lng match {
      case Japanese => _.toJapaneseNotationString
      case English => _.toWesternNotationString
    }
    val g: SpecialMove => String = lng match {
      case Japanese => _.toJapaneseNotationString
      case English => _.toWesternNotationString
    }
    game.withBranch(branchNo) { br =>
      (game.getAllMoves(branchNo).map(f) ++ (br.status match {
        case GameStatus.Resigned | GameStatus.TimedUp => List(g(br.finalAction.get))
        case GameStatus.IllegallyMoved => g(br.finalAction.get).split("\n").toList.take(1)
        case _ => Nil
      })).toList
    }.getOrElse(Nil)
  }

  def updateRecordContent(game: Game, branchNo: BranchNo, lng: Language): Unit = {

    val prefix = lng match {
      case Japanese => "初期局面"
      case English => "Start"
    }
    val initTurn = game.trunk.initialState.turn

    game.withBranch(branchNo) { br =>
      // moves
      val xs = (prefix +: getMoves(game, branchNo, lng)).zipWithIndex.map { case (m, i) =>
        val pos = i + game.trunk.offset
        val symbolMark = game.hasFork(GamePosition(branchNo, pos)).fold("+", game.hasComment(GamePosition(branchNo, pos)).fold("*", ""))
        val indexNotation = if (i == 0) "" else s"${i}: " + (i % 2 == 0).fold(!initTurn, initTurn).toSymbolString()
        symbolMark + indexNotation + m
      }

      val suffix = (br.status, lng) match {
        case (GameStatus.Mated, Japanese) => List("詰み")
        case (GameStatus.Mated, English) => List("Mated")
        case (GameStatus.Drawn, Japanese) => List("千日手")
        case (GameStatus.Drawn, English) => List("Drawn")
        case (GameStatus.PerpetualCheck, Japanese) => List("連続王手の千日手")
        case (GameStatus.PerpetualCheck, English) => List("Perpetual Check")
        case (GameStatus.Uchifuzume, Japanese) => List("打ち歩詰め")
        case (GameStatus.Uchifuzume, English) => List("Uchifuzume")
        case (GameStatus.IllegallyMoved, Japanese) => br.finalAction.get.toJapaneseNotationString.split("\n").toList.drop(1)
        case (GameStatus.IllegallyMoved, English) => br.finalAction.get.toWesternNotationString.split("\n").toList.drop(1)
        case _ => Nil
      }
      val s = (xs ++ suffix).map(s => option(s)).mkString

      recordSelector.innerHTML = s
      recordSelectorLong.innerHTML = s
    }
  }

  def initialize(): Unit = {
    setClickEvent(controlInput0, () => Controller.setControl(0))
    setClickEvent(controlInput1, () => Controller.setControl(1), Some(() => Controller.setControl(1)), Some(() => controlInput1.disabled == true))
    setClickEvent(controlInput2, () => Controller.setControl(2), Some(() => Controller.setControl(2)), Some(() => controlInput2.disabled == true))
    setClickEvent(controlInput3, () => Controller.setControl(3))
  }

  private[this] def createControlInput(glyph: String) = button(cls := "btn btn-default" + isSmall.fold(" control-small input-small", ""),
    width := CONTROL_WIDTH,
    span(cls := s"glyphicon glyphicon-${glyph}", aria.hidden := true)
  ).render

  val output: Div = div(cls := "btn-toolbar", role := "toolbar",
    div(cls := "btn-group", role := "group", aria.label := "...",
      div(cls := "btn-group", role := "group", controlInput0),
      div(cls := "btn-group", role := "group", controlInput1),
      div(cls := "btn-group", role := "group", recordSelector),
      div(cls := "btn-group", role := "group", controlInput2),
      div(cls := "btn-group", role := "group", controlInput3)
    )
  ).render

  val outputLongSelector: HTMLSelectElement = recordSelectorLong

  //
  // actions
  //
  def updateLabels(backwardEnabled: Boolean, forwardEnabled: Boolean): Unit = {
    controlInput0.disabled = !backwardEnabled
    controlInput1.disabled = !backwardEnabled
    controlInput2.disabled = !forwardEnabled
    controlInput3.disabled = !forwardEnabled
  }
}
