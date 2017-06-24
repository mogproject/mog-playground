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
import org.scalajs.dom.Event

import scalatags.JsDom.all._

/**
  * Branch buttons on Left Sidebar for PC/tablet, or Menu Modal for mobile
  */
case class BranchButton(isMobile: Boolean) {

  /** HTML elements */
  private[this] lazy val changeBranchButton: HTMLSelectElement = select(
    cls := "form-control",
    width := "100%",
    onchange := { e: Event =>
      e.target match {
        case elem: HTMLSelectElement => Controller.changeBranch(elem.selectedIndex, None)
        case _ => // do nothing
      }
    }
  ).render

  private[this] lazy val newBranchButton: RadioButton[Boolean] = RadioButton(
    Seq(false, true),
    Map(English -> Seq("Off", "On")),
    tooltip = (!isMobile).option("Creates a new branch")
  )

  private[this] lazy val deleteBranchButton = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := "Delete this branch",
    data("dismiss") := "modal",
    onclick := { () => Controller.askDeleteBranch() },
    isMobile.fold("Delete", span(cls := "glyphicon glyphicon-trash"))
  ).render

  private[this] lazy val forksButtons = div("").render

  /** Utility functions */
  private[this] def branchNoToString(branchNo: BranchNo): String = (branchNo == 0).fold("Trunk", s"Branch#${branchNo}")

  private[this] def updateBranchList(numBranches: Int, displayBranch: BranchNo): Unit = {
    val s = (0 to numBranches).map(s => option(branchNoToString(s))).toString
    changeBranchButton.innerHTML = s
    changeBranchButton.selectedIndex = displayBranch
  }

  private[this] def createForkButton(move: Move, branchNo: BranchNo, language: Language, tooltipPlacement: String): Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := tooltipPlacement,
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

  lazy val output: Div = isMobile.fold(outputOnMenu, outputCompact).render

  private[this] def outputOnMenu = div(
    div(cls := "row",
      div(cls := "col-xs-6 col-sm-8", label(paddingTop := "6px", "Change Branch")),
      div(cls := "col-xs-6 col-sm-4", changeBranchButton)
    ),
    label("Forks"),
    br(),
    forksButtons,
    playModeMenu
  )

  private[this] def outputCompact = div(
    div(cls := "row",
      marginRight := 12.px,
      marginBottom := 10.px,
      div(cls := "col-xs-6", label("Branch")),
      div(cls := "col-xs-6", marginTop := (-6).px,
        newBranchButton.output)
    ),
    div(
      marginLeft := 14.px,
      marginBottom := 20.px,
      div(cls := "btn-group", role := "group",
        div(cls := "btn-group", width := 130.px, marginBottom := 10.px, changeBranchButton),
        div(cls := "btn-group", deleteBranchButton)
      ),
      forksButtons
    )
  )

  //
  // initialize
  //
  def initialize(): Unit = {
    newBranchButton.initialize(false, English)
  }

  initialize()

  //
  // actions
  //
  def hide(): Unit = output.style.display = display.none.v

  def show(): Unit = output.style.display = display.block.v

  private[this] def playModeElements = Seq(
    playModeMenu
  ) ++ isMobile.fold(Seq.empty, Seq(newBranchButton.output, deleteBranchButton))

  def showEditMenu(): Unit = playModeElements.foreach(_.style.display = display.block.v)

  def hideEditMenu(): Unit = playModeElements.foreach(_.style.display = display.none.v)

  def updateButtons(game: Game, gamePosition: GamePosition, language: Language): Unit = {
    updateBranchList(game.branches.length, gamePosition.branch)

    deleteBranchButton.disabled = gamePosition.isTrunk

    val forks = game.getForks(gamePosition)

    if (forks.isEmpty) {
      forksButtons.innerHTML = isMobile.fold("No forks.", "")
    } else {
      val nextMove = game.getMove(gamePosition).map(_ -> gamePosition.branch)

      val buttons = (nextMove.toSeq ++ forks).map { case (m, b) => createForkButton(m, b, language, isMobile.fold("bottom", "right")) }
      Tooltip.enableHoverToolTip(buttons)

      val elem = (if (isMobile) {
        div(
          cls := "row",
          buttons.map(div(cls := "col-sm-4 col-xs-6", _))
        )
      } else {
        div(
          cls := "row",
          marginLeft := 0.px,
          buttons.map(div(cls := "col-xs-8", paddingLeft := 0.px, _))
        )
      }).render

      forksButtons.innerHTML = ""
      forksButtons.appendChild(elem)
    }
  }

  def getIsNewBranchMode: Boolean = newBranchButton.getValue
}
