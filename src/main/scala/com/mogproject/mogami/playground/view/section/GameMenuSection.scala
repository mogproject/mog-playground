package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.Game
import com.mogproject.mogami.core.game.Game.GamePosition
import com.mogproject.mogami.playground.controller.Language
import com.mogproject.mogami.playground.view.parts.branch.BranchButton
import com.mogproject.mogami.playground.view.parts.common.AccordionMenu
import com.mogproject.mogami.playground.view.parts.manage.SaveLoadButton
import com.mogproject.mogami.playground.view.parts.share._

import scalatags.JsDom.all._

/**
  *
  */
object GameMenuSection extends Section {
  private[this] val branchMenu = AccordionMenu(
    "Branch",
    "Branch",
    "share-alt",
    isExpanded = false,
    isVisible = false,
    div(
      BranchButton.output
    )
  )

  override val accordions: Seq[AccordionMenu] = Seq(
    AccordionMenu(
      "Share",
      "Share",
      "share",
      isExpanded = false,
      isVisible = true,
      div(
        RecordCopyButton.output,
        RecordShortenButton.output,
        br(),
        SnapshotCopyButton.output,
        SnapshotShortenButton.output,
        br(),
        ImageLinkButton.output,
        br(),
        SfenStringCopyButton.output,
        br(),
        NotesViewButton.output,
        NotesViewShortenButton.output
      )
    ),
    branchMenu,
    AccordionMenu(
      "Manage",
      "Manage",
      "file",
      isExpanded = false,
      isVisible = true,
      div(
        SaveLoadButton.output
      )
    )
  )

  def updateCommentOmissionWarning(displayWarning: Boolean): Unit =
    if (displayWarning) {
      RecordCopyButton.showWarning()
    } else {
      RecordCopyButton.hideWarning()
    }

  def updateBranchButtons(game: Game, gamePosition: GamePosition, language: Language): Unit =
    BranchButton.updateButtons(game, gamePosition, language)

  def showBranchEditMenu(): Unit = BranchButton.showEditMenu()

  def hideBranchEditMenu(): Unit = BranchButton.hideEditMenu()

  def showBranchMenu(): Unit = branchMenu.output.style.display = display.block.v

  def hideBranchMenu(): Unit = branchMenu.output.style.display = display.none.v

  def getIsNewBranchMode: Boolean = BranchButton.getIsNewBranchMode

  override def initialize(): Unit = {
    super.initialize()
    BranchButton.initialize()
  }

}
