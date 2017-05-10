package com.mogproject.mogami.playground.view.parts.branch

import com.mogproject.mogami.{BranchNo, Game, Move}
import com.mogproject.mogami.core.game.Game.GamePosition
import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import com.mogproject.mogami.playground.view.bootstrap.Tooltip
import com.mogproject.mogami.playground.view.parts.common.RadioButton
import org.scalajs.dom.html.{Button, Div}
import org.scalajs.dom.raw.HTMLSelectElement
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom

import scalatags.JsDom.all._

/**
  *
  */
object BranchButton {

  private[this] lazy val changeBranchButton: HTMLSelectElement = select(
    cls := "form-control",
    width := "100%",
    onchange := (() => Controller.changeBranch(changeBranchButton.selectedIndex, None))
  ).render

  private[this] lazy val forksButtons = div("").render

  private[this] lazy val newBranchButton: RadioButton[Boolean] = RadioButton(Seq(false, true), Map(English -> Seq("Off", "On")))

  private[this] lazy val deleteBranchButton = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := "Delete this branch",
    data("dismiss") := "modal",
    onclick := { () => Controller.askDeleteBranch() },
    "Delete"
  ).render

  private[this] def branchNoToString(branchNo: BranchNo): String = (branchNo == 0).fold("Trunk", s"Branch#${branchNo}")

  private[this] def updateBranchList(numBranches: Int, displayBranch: BranchNo): Unit = {
    changeBranchButton.innerHTML = (0 to numBranches).map(s => option(branchNoToString(s))).toString
    changeBranchButton.selectedIndex = displayBranch
  }

  private[this] def createForkButton(move: Move, branchNo: BranchNo, language: Language): Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := branchNoToString(branchNo),
    data("dismiss") := "modal",
    onclick := { () => dom.window.setTimeout(() => Controller.changeBranch(branchNo, Some(1)), 0) },
    move.player.toSymbolString() + (language match {
      case English => move.toWesternNotationString
      case Japanese => move.toJapaneseNotationString
    })
  ).render

  //
  // layout
  //
  private[this] val playModeMenu: Div = div(
    display := display.none.v,
    br(),
    label("New Branch Mode"),
    div(cls := "row",
      div(cls := "col-xs-7 col-sm-9", p(paddingTop := "6px", "Creates a new branch whenever you make a different move.")),
      div(cls := "col-xs-5 col-sm-3", newBranchButton.output)
    ),
    br(),
    div(cls := "row",
      div(cls := "col-xs-7 col-sm-9", label(paddingTop := "6px", "Delete This Branch")),
      div(cls := "col-xs-5 col-sm-3", deleteBranchButton)
    )
  ).render

  lazy val output: Div = div(
    div(cls := "row",
      div(cls := "col-xs-6 col-sm-8", label(paddingTop := "6px", "Change Branch")),
      div(cls := "col-xs-6 col-sm-4", changeBranchButton)
    ),
    label("Forks"),
    br(),
    forksButtons,
    playModeMenu
  ).render

  //
  // initialize
  //
  def initialize(): Unit = {
    newBranchButton.initialize(false, English)
  }

  //
  // actions
  //
  def showEditMenu(): Unit = playModeMenu.style.display = display.block.v

  def hideEditMenu(): Unit = playModeMenu.style.display = display.none.v

  def updateButtons(game: Game, gamePosition: GamePosition, language: Language): Unit = {
    updateBranchList(game.branches.length, gamePosition.branch)

    deleteBranchButton.disabled = gamePosition.isTrunk

    val forks = game.getForks(gamePosition)

    if (forks.isEmpty) {
      forksButtons.innerHTML = "No forks."
    } else {
      val nextMove = game.getMove(gamePosition).map(_ -> gamePosition.branch)
      val buttons = (nextMove.toSeq ++ forks).map { case (m, b) => createForkButton(m, b, language) }
      Tooltip.enableHoverToolTip(buttons)
      forksButtons.innerHTML = ""
      forksButtons.appendChild(div(
        cls := "row",
        buttons.map(div(cls := "col-sm-4 col-xs-6", _))
      ).render)
    }
  }

  def getIsNewBranchMode: Boolean = newBranchButton.getValue
}
