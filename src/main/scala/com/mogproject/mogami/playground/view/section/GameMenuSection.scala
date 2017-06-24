package com.mogproject.mogami.playground.view.section

import com.mogproject.mogami.playground.view.parts.common.AccordionMenu
import com.mogproject.mogami.playground.view.parts.manage.SaveLoadButton
import com.mogproject.mogami.playground.view.parts.share._

import scalatags.JsDom.all._

/**
  *
  */
object GameMenuSection extends Section {
  override lazy val accordions: Seq[AccordionMenu] = Seq(
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

}
