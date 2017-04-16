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
    )
  )
}
