package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts._
import com.mogproject.mogami.playground.view.parts.common.AccordionMenu

import scalatags.JsDom.all._

/**
  *
  */
object GameMenuSection extends Section {
  override val accordions: Seq[AccordionMenu] = Seq(
    AccordionMenu(
      "Share",
      "Share",
      true,
      true,
      div(
        SnapshotCopyButton.output,
        SnapshotShortenButton.output,
        br(),
        RecordCopyButton.output,
        RecordShortenButton.output,
        br(),
        ImageLinkButton.output,
        br(),
        SfenStringCopyButton.output
      )
    ),
    AccordionMenu(
      "Manage",
      "Manage",
      false,
      true,
      div(
        RecordLoadButton.output,
        br(),
        RecordSaveButton.output
      )
    ),
    AccordionMenu(
      "Help",
      "Help",
      true,
      true,
      div(
        ul(
          li("Click on a player name to set the game information."),
          li("In Play Mode, you can move pieces by a flick."),
          li("In View Mode, you can move to the next position by clicking anywhere on the board."),
          li("If you click and hold 'forward' or 'backward' button, the position changes continuously.")
        )
      )
    )
  )
}
