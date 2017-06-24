package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts.branch.BranchButton
import com.mogproject.mogami.playground.view.parts.common.AccordionMenu

import scalatags.JsDom.all._

/**
  *
  */
object BranchSection extends Section {
  lazy val branchButton = BranchButton(true)

  override lazy val accordions: Seq[AccordionMenu] = Seq(
    AccordionMenu(
      "Branch",
      "Branch",
      "share-alt",
      isExpanded = false,
      isVisible = true,
      div(
        branchButton.output
      )
    )
  )

  override def initialize(): Unit = {
    super.initialize()
    branchButton.initialize()
  }

}
