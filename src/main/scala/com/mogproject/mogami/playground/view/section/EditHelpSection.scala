package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts.common.AccordionMenu

import scalatags.JsDom.all._

/**
  *
  */
object EditHelpSection extends Section {
  override val accordions: Seq[AccordionMenu] = Seq(
    AccordionMenu(
      "EditHelp",
      "Help",
      "question-sign",
      isExpanded = false,
      isVisible = false,
      div(
        ul(
          li("Click on a player name to set the turn to move."),
          li("Double-click on a piece on the board to change the piece attributes: Black Unpromoted -> Black Promoted -> White Unpromoted -> White Promoted -> Black Unpromoted.")
        ))
    )
  )

}
