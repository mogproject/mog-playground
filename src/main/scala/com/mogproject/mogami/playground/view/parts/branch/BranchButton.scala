package com.mogproject.mogami.playground.view.parts.branch

import com.mogproject.mogami.{BranchNo, Game, Move}
import com.mogproject.mogami.core.game.Game.GamePosition
import com.mogproject.mogami.playground.controller.{Controller, English, Japanese, Language}
import com.mogproject.mogami.playground.view.parts.common.RadioButton
import org.scalajs.dom.html.{Button, Div}
import org.scalajs.dom.raw.HTMLSelectElement

import scalatags.JsDom.all._

/**
  *
  */
object BranchButton {

  private[this] lazy val changeBranchButton: HTMLSelectElement = select(
    cls := "form-control",
    width := "100%",
    data("dismiss") := "modal",
    onchange := (() => Controller.changeBranch(changeBranchButton.selectedIndex, None))
  ).render

  private[this] lazy val forksButtons = div("No forks.").render

  private[this] lazy val newBranchButton = RadioButton(Seq(false, true), Map(English -> Seq("Off", "On")))

  private[this] lazy val deleteBranchButton = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := "Delete this branch",
    data("dismiss") := "modal",
    onclick := { () => Controller.deleteBranch() },
    "Delete"
  ).render

  private[this] def updateBranchList(numBranches: Int, displayBranch: BranchNo): Unit = {
    changeBranchButton.innerHTML = ("Trunk" +: (1 to numBranches).map(b => s"Branch#${b}")).map(s => option(s)).toString
    changeBranchButton.selectedIndex = displayBranch
  }

  private[this] def createForkButton(move: Move, branchNo: BranchNo, language: Language): Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("dismiss") := "modal",
    onclick := { () => Controller.changeBranch(branchNo, Some(1)) },
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
    label("New Branch Mode"),
    div(cls := "row",
      div(cls := "col-xs-8 col-lg-9", p(paddingTop := "6px", "Create a new branch when you make a different move.")),
      div(cls := "col-xs-4 col-lg-3", newBranchButton.output)
    ),
    br(),
    div(cls := "row",
      div(cls := "col-xs-8 col-lg-9", label(paddingTop := "6px", "Delete This Branch")),
      div(cls := "col-xs-4 col-lg-3", deleteBranchButton)
    )
  ).render

  lazy val output: Div = div(
    div(cls := "row",
      div(cls := "col-xs-6 col-lg-8", label(paddingTop := "6px", "Change Branch")),
      div(cls := "col-xs-6 col-lg-4", changeBranchButton)
    ),
    br(),
    label("Forks"),
    forksButtons,
    br(),
    playModeMenu
  ).render

  //
  // initialize
  //
  def initialize(): Unit = {
    newBranchButton.initialize()
    newBranchButton.updateValue(false)
    newBranchButton.updateLabel(English)
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
    forksButtons.innerHTML = ""
    forksButtons.appendChild(div(
      cls := "row",
      forks.map { case (m, b) => div(cls := "col-lg-4 col-xs-6", createForkButton(m, b, language)) }
    ).render)

    val isLastPosition = game.withBranch(gamePosition.branch)(br => br.offset + br.moves.length >= gamePosition.position).getOrElse(false)
    if (isLastPosition) newBranchButton.disable() else newBranchButton.enable()
  }
}
